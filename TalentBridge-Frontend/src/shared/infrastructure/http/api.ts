import axios from "axios";
import type { AxiosRequestConfig, AxiosResponse } from "axios";

import { ApiError } from "@/shared/domain/errors";
import type { PageQuery } from "@/shared/domain/page";

import type { ApiResponse } from "./apiResponse";
import { httpClient, plainHttpClient } from "./httpClient";

const toApiError = (err: unknown): never => {
  if (axios.isAxiosError(err)) {
    const body = err.response?.data as Partial<ApiResponse<unknown>> | undefined;
    throw new ApiError(body?.message || err.message, {
      status: err.response?.status,
      errorCode: body?.errorCode,
      serverMessage: body?.message || undefined,
    });
  }
  throw err;
};

/** Sends the request, unwraps the `data` of the envelope and turns failures into ApiError. */
const send = async <T>(request: Promise<AxiosResponse<ApiResponse<T>>>): Promise<T> => {
  try {
    return (await request).data?.data as T;
  } catch (err) {
    return toApiError(err);
  }
};

const multipart: AxiosRequestConfig = { headers: { "Content-Type": "multipart/form-data" } };

type Client = typeof httpClient;

const verbs = (client: Client) => ({
  get: <T>(url: string, config?: AxiosRequestConfig) =>
    send<T>(client.get<ApiResponse<T>>(url, config)),
  post: <T = void>(url: string, body?: unknown, config?: AxiosRequestConfig) =>
    send<T>(client.post<ApiResponse<T>>(url, body, config)),
  put: <T = void>(url: string, body?: unknown, config?: AxiosRequestConfig) =>
    send<T>(client.put<ApiResponse<T>>(url, body, config)),
  patch: <T = void>(url: string, body?: unknown, config?: AxiosRequestConfig) =>
    send<T>(client.patch<ApiResponse<T>>(url, body, config)),
  delete: <T = void>(url: string, config?: AxiosRequestConfig) =>
    send<T>(client.delete<ApiResponse<T>>(url, config)),
});

/** Authenticated calls (access token attached, refreshed on expiry). */
export const api = {
  ...verbs(httpClient),
  postForm: <T = void>(url: string, form: FormData) => api.post<T>(url, form, multipart),
  putForm: <T = void>(url: string, form: FormData) => api.put<T>(url, form, multipart),
};

/** Calls that must not go through the refresh logic (sign-in, refresh, sign-out, register). */
export const plainApi = verbs(plainHttpClient);

/** `?page=&size=&filter=` for list endpoints. */
export const pageQueryString = ({ page, size, filter }: PageQuery): string => {
  const params = new URLSearchParams({ page: page.toString(), size: size.toString() });
  if (filter) params.append("filter", filter);
  return params.toString();
};
