import type { ReactNode } from "react";
import { Navigate } from "react-router-dom";
import { ROUTES } from "../config/routes";
import { useAuth } from "../features/auth/hooks/useAuth";
import type { UserRole } from "../types/auth";

type RoleBasedRouteProps = {
  children: ReactNode;
  allowedRoles: UserRole[];
};

export function RoleBasedRoute({ children, allowedRoles }: RoleBasedRouteProps) {
  const { user } = useAuth();

  if (!user) {
    return <Navigate to={ROUTES.login} replace />;
  }

  if (!allowedRoles.includes(user.role)) {
    const pathByRole: Record<UserRole, string> = {
      STUDENT: ROUTES.studentDashboard,
      ORGANIZATION: ROUTES.organizationDashboard,
      ADMIN: ROUTES.adminDashboard,
    };
    return <Navigate to={pathByRole[user.role]} replace />;
  }

  return children;
}
