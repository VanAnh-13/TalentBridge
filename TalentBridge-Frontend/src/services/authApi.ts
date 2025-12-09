import axiosClient from "@/lib/axiosClient";
import type { ApiResponse } from "@/types/apiResponse.d.ts";
import type {
  UserLoginRequestDto,
  AuthTokenResponseDto,
  UserSessionResponseDto,
  UserDetailsResponseDto,
  SessionMetaRequest,
  SessionMetaResponse,
  UserRegisterRequestDto,
  ForgotPasswordRequest,
  ResetPasswordRequest,
  TokenValidationResponse,
} from "@/types/user.d.ts";
import { getSessionMeta } from "@/utils/sessionHelper";
import axios from "axios";

const API_BASE_URL = import.meta.env.VITE_API_URL || "http://localhost:8080";

export const registerApi = (data: UserRegisterRequestDto) => {
  return axios.post(`${API_BASE_URL}/auth/register`, data, {
    withCredentials: true,
  });
};

export const loginApi = (data: UserLoginRequestDto) => {
  data = {
    ...data,
    sessionMetaRequest: getSessionMeta(),
  };

  return axiosClient.post<ApiResponse<AuthTokenResponseDto>>(
    "/auth/login",
    data,
  );
};

export const logoutApi = () => {
  return axios.post(
    `${API_BASE_URL}/auth/logout`,
    {},
    { withCredentials: true },
  );
};

export const getUserSession = () => {
  return axiosClient.get<ApiResponse<UserSessionResponseDto>>("/auth/me");
};

export const getUserDetails = () => {
  return axiosClient.get<ApiResponse<UserDetailsResponseDto>>(
    "/auth/me/details",
  );
};

export const refreshTokenApi = () => {
  const data: SessionMetaRequest = getSessionMeta();

  return axios.post<ApiResponse<AuthTokenResponseDto>>(
    `${API_BASE_URL}/auth/refresh-token`,
    data,
    { withCredentials: true },
  );
};

export const getSessions = () => {
  return axiosClient.get<ApiResponse<SessionMetaResponse[]>>(`/auth/sessions`);
};

export const removeSessionId = (sessionId: string) => {
  return axiosClient.delete(`/auth/sessions/${sessionId}`);
};


// =============================
// PASSWORD RESET APIs
// =============================
export const forgotPasswordApi = (data: ForgotPasswordRequest) => {
  return axios.post(`${API_BASE_URL}/auth/forgot-password`, data);
};

export const verifyResetTokenApi = (token: string) => {
  return axios.get<ApiResponse<TokenValidationResponse>>(
    `${API_BASE_URL}/auth/reset-password/verify?token=${token}`
  );
};

export const resetPasswordApi = (data: ResetPasswordRequest) => {
  return axios.post(`${API_BASE_URL}/auth/reset-password`, data);
};
