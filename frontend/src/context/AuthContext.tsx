import { createContext, useCallback, useEffect, useMemo, useState } from "react";
import type { ReactNode } from "react";
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
  register: (payload: RegisterRequest) => Promise<void>;
  logout: () => void;
  redirectToRoleDashboard: (role?: UserRole) => void;
};

export const AuthContext = createContext<AuthContextValue | null>(null);

type AuthProviderProps = {
  children: ReactNode;
};

export function AuthProvider({ children }: AuthProviderProps) {
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
    window.history.pushState({}, "", nextRole ? pathByRole[nextRole] : ROUTES.home);
    window.dispatchEvent(new PopStateEvent("popstate"));
  }, [user?.role]);

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
    applyAuthResponse(response.accessToken, response.user);
  }, [applyAuthResponse]);

  const logout = useCallback(() => {
    clearStoredToken();
    setToken(null);
    setUser(null);
    window.history.pushState({}, "", ROUTES.login);
    window.dispatchEvent(new PopStateEvent("popstate"));
  }, []);

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
