import type { ReactNode } from "react";
import { ROUTES } from "../config/routes";
import { useAuth } from "../features/auth/hooks/useAuth";
import type { UserRole } from "../types/auth";

type RoleBasedRouteProps = {
  children: ReactNode;
  allowedRoles: UserRole[];
};

export function RoleBasedRoute({ children, allowedRoles }: RoleBasedRouteProps) {
  const { user, redirectToRoleDashboard } = useAuth();

  if (!user) {
    window.history.replaceState({}, "", ROUTES.login);
    window.dispatchEvent(new PopStateEvent("popstate"));
    return null;
  }

  if (!allowedRoles.includes(user.role)) {
    redirectToRoleDashboard(user.role);
    return null;
  }

  return children;
}
