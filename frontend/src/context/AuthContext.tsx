import { createContext, useCallback, useEffect, useMemo, useState } from "react";
import type { ReactNode } from "react";
import { useNavigate } from "react-router-dom";
import { clearStoredToken, getStoredToken, setStoredToken } from "../config/apiClient";
import { ROUTES } from "../config/routes";
import { authApi } from "../features/auth/api/authApi";
import type { AuthUser, LoginRequest, RegisterRequest, UserRole } from "../types/auth";

type AuthContextValue = {
  user: AuthUser | null;
  token: string | null;
  isAuthenticated: boolean;
  isLoading: boolean;
  login: (payload: LoginRequest) => Promise<void>;
  register: (payload: RegisterRequest) => Promise<string>;
  logout: () => void;
  redirectToRoleDashboard: (role?: UserRole) => void;
};

export const AuthContext = createContext<AuthContextValue | null>(null);

type AuthProviderProps = {
  children: ReactNode;
};

export function AuthProvider({ children }: AuthProviderProps) {
  const navigate = useNavigate();
  const [user, setUser] = useState<AuthUser | null>(null);
  const [token, setToken] = useState<string | null>(() => getStoredToken());
  const [isLoading, setIsLoading] = useState(true);

  useEffect(() => {
    if (!token) {
      setIsLoading(false);
      return;
    }

    authApi
      .me()
      .then(setUser)
      .catch(() => {
        clearStoredToken();
        setToken(null);
        setUser(null);
      })
      .finally(() => setIsLoading(false));
  }, [token]);

  const redirectToRoleDashboard = useCallback((role?: UserRole) => {
    const nextRole = role ?? user?.role;
    const pathByRole: Record<UserRole, string> = {
      STUDENT: ROUTES.studentDashboard,
      ORGANIZATION: ROUTES.organizationDashboard,
      ADMIN: ROUTES.adminDashboard,
    };
    navigate(nextRole ? pathByRole[nextRole] : ROUTES.home);
  }, [navigate, user?.role]);

  const applyAuthResponse = useCallback((accessToken: string, nextUser: AuthUser) => {
    setStoredToken(accessToken);
    setToken(accessToken);
    setUser(nextUser);
    redirectToRoleDashboard(nextUser.role);
  }, [redirectToRoleDashboard]);

  const login = useCallback(async (payload: LoginRequest) => {
    const response = await authApi.login(payload);
    applyAuthResponse(response.accessToken, response.user);
  }, [applyAuthResponse]);

  const register = useCallback(async (payload: RegisterRequest) => {
    const response = await authApi.register(payload);
    if (response.accessToken) {
      applyAuthResponse(response.accessToken, response.user);
    }
    return response.message;
  }, [applyAuthResponse]);

  const logout = useCallback(() => {
    clearStoredToken();
    setToken(null);
    setUser(null);
    navigate(ROUTES.login);
  }, [navigate]);

  const value = useMemo<AuthContextValue>(() => ({
    user,
    token,
    isAuthenticated: Boolean(token && user),
    isLoading,
    login,
    register,
    logout,
    redirectToRoleDashboard,
  }), [isLoading, login, logout, redirectToRoleDashboard, register, token, user]);

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
}
