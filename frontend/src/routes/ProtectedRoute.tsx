import type { ReactNode } from "react";
import { ROUTES } from "../config/routes";
import { useAuth } from "../features/auth/hooks/useAuth";

type ProtectedRouteProps = {
  children: ReactNode;
};

export function ProtectedRoute({ children }: ProtectedRouteProps) {
  const { isAuthenticated, isLoading } = useAuth();

  if (isLoading) {
    return <main className="grid min-h-screen place-items-center bg-background text-foreground">Đang tải...</main>;
  }

  if (!isAuthenticated) {
    window.history.replaceState({}, "", ROUTES.login);
    window.dispatchEvent(new PopStateEvent("popstate"));
    return null;
  }

  return children;
}
