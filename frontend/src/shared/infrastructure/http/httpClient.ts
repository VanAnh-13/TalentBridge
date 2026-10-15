import axios from "axios";
import type { AxiosError, InternalAxiosRequestConfig } from "axios";

import type { ApiResponse } from "./apiResponse";

export const API_BASE_URL = import.meta.env.VITE_API_URL || "http://localhost:8080";

/** What the HTTP client needs from the application; supplied once at start-up (see app/wiring). */
export interface HttpClientHooks {
  getAccessToken(): string | null;
  /** Gets a new access token with the refresh cookie and makes the application adopt it. */
  refreshAccessToken(): Promise<string>;
  /** The refresh failed: the user has to sign in again. */
  onSessionExpired(): void;
}

let hooks: HttpClientHooks | null = null;

export const configureHttpClient = (value: HttpClientHooks) => {
  hooks = value;
};

/** Requests that carry the access token and recover from an expired one. */
export const httpClient = axios.create({
  baseURL: API_BASE_URL,
  withCredentials: true,
});

/** Requests that must not trigger a token refresh themselves (login, refresh, logout, register). */
export const plainHttpClient = axios.create({
  baseURL: API_BASE_URL,
  withCredentials: true,
});

httpClient.interceptors.request.use((config: InternalAxiosRequestConfig) => {
  const accessToken = hooks?.getAccessToken();
  if (accessToken) config.headers.Authorization = `Bearer ${accessToken}`;
  return config;
});

// Requests that fail with 401 while a refresh is running wait here and are replayed afterwards.
type Waiting = { resolve: () => void; reject: (reason?: unknown) => void };
let waiting: Waiting[] = [];
let isRefreshing = false;

const settleWaiting = (error: unknown) => {
  waiting.forEach(({ resolve, reject }) => (error ? reject(error) : resolve()));
  waiting = [];
};

httpClient.interceptors.response.use(
  (response) => response,
  async (error: AxiosError) => {
    const original = error.config as InternalAxiosRequestConfig & { _retry?: boolean };
    const errorCode = (error.response?.data as ApiResponse<null> | undefined)?.errorCode;

    // 401 UNAUTHORIZED = missing/expired token: refresh and retry once.
    // 403 ACCESS_DENIED = signed in but not allowed: a new token would not help.
    const expired = error.response?.status === 401 && errorCode === "UNAUTHORIZED";
    if (!expired || original._retry || !hooks) return Promise.reject(error);
    original._retry = true;

    if (isRefreshing) {
      return new Promise<void>((resolve, reject) => waiting.push({ resolve, reject }))
        .then(() => httpClient(original));
    }

    isRefreshing = true;
    try {
      const accessToken = await hooks.refreshAccessToken();
      settleWaiting(null);
      original.headers.Authorization = `Bearer ${accessToken}`;
      return httpClient(original);
    } catch (refreshError) {
      hooks.onSessionExpired();
      settleWaiting(refreshError);
      return Promise.reject(refreshError);
    } finally {
      isRefreshing = false;
    }
  },
);
