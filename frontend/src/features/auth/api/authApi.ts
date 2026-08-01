import { apiClient } from "../../../config/apiClient";
import type { AuthResponse, AuthUser, LoginRequest, RegisterRequest } from "../../../types/auth";

export const authApi = {
  register(payload: RegisterRequest) {
    return apiClient<AuthResponse>("/auth/register", {
      method: "POST",
      body: JSON.stringify(payload),
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
