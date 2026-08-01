import { apiClient } from "../../../config/apiClient";
import type { AuthResponse, AuthUser, LoginRequest, RegisterRequest, RegisterResponse } from "../../../types/auth";

export const authApi = {
  register(payload: RegisterRequest) {
    return apiClient<RegisterResponse>("/auth/register", {
      method: "POST",
      body: JSON.stringify(payload),
      skipAuth: true,
    });
  },
  verifyEmail(token: string) {
    return apiClient<AuthUser>(`/auth/verify-email?token=${encodeURIComponent(token)}`, {
      skipAuth: true,
    });
  },
  login(payload: LoginRequest) {
    return apiClient<AuthResponse>("/auth/login", {
      method: "POST",
      body: JSON.stringify(payload),
      skipAuth: true,
    });
  },
  me() {
    return apiClient<AuthUser>("/auth/me");
  },
  refreshToken() {
    return apiClient<AuthResponse>("/auth/refresh-token", {
      method: "POST",
    });
  },
};
