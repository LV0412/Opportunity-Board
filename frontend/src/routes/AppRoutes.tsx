import { useEffect, useState } from "react";
import { ROUTES } from "../config/routes";
import { AdminDashboardPage } from "../pages/admin/AdminDashboardPage";
import { CategoriesPage } from "../pages/admin/CategoriesPage";
import { PendingOpportunitiesPage } from "../pages/admin/PendingOpportunitiesPage";
import { ReportsPage } from "../pages/admin/ReportsPage";
import { UsersPage } from "../pages/admin/UsersPage";
import { LoginPage } from "../pages/auth/LoginPage";
import { RegisterPage } from "../pages/auth/RegisterPage";
import { ExplorePage } from "../pages/public/ExplorePage";
import { HomePage } from "../pages/public/HomePage";
import { NotFoundPage } from "../pages/NotFoundPage";
import { OrganizationDashboardPage } from "../pages/organization/OrganizationDashboardPage";
import { ApplicantsPage } from "../pages/organization/ApplicantsPage";
import { OrganizationOpportunitiesPage } from "../pages/organization/OrganizationOpportunitiesPage";
import { OrganizationProfilePage } from "../pages/organization/OrganizationProfilePage";
import { OpportunityDetailPage } from "../pages/public/OpportunityDetailPage";
import { ProfilePage } from "../pages/student/ProfilePage";
import { ApplicationsPage } from "../pages/student/ApplicationsPage";
import { SavedOpportunitiesPage } from "../pages/student/SavedOpportunitiesPage";
import { StudentDashboardPage } from "../pages/student/StudentDashboardPage";
import { ProtectedRoute } from "./ProtectedRoute";
import { RoleBasedRoute } from "./RoleBasedRoute";

export function AppRoutes() {
  const [path, setPath] = useState(window.location.pathname);

  useEffect(() => {
    function handleRouteChange() {
      setPath(window.location.pathname);
    }

    window.addEventListener("popstate", handleRouteChange);
    return () => window.removeEventListener("popstate", handleRouteChange);
  }, []);

  if (path === ROUTES.home) {
    return <HomePage />;
  }

  if (path === ROUTES.login) {
    return <LoginPage />;
  }

  if (path === ROUTES.register) {
    return <RegisterPage />;
  }

  if (path === ROUTES.explore) {
    return <ExplorePage />;
  }

  if (path.startsWith(`${ROUTES.opportunityDetail}/`)) {
    const id = path.replace(`${ROUTES.opportunityDetail}/`, "");
    return <OpportunityDetailPage id={id} />;
  }

  if (path === ROUTES.studentDashboard) {
    return (
      <ProtectedRoute>
        <RoleBasedRoute allowedRoles={["STUDENT"]}>
          <StudentDashboardPage />
        </RoleBasedRoute>
      </ProtectedRoute>
    );
  }

  if (path === ROUTES.studentProfile) {
    return (
      <ProtectedRoute>
        <RoleBasedRoute allowedRoles={["STUDENT"]}>
          <ProfilePage />
        </RoleBasedRoute>
      </ProtectedRoute>
    );
  }

  if (path === ROUTES.studentSavedOpportunities) {
    return (
      <ProtectedRoute>
        <RoleBasedRoute allowedRoles={["STUDENT"]}>
          <SavedOpportunitiesPage />
        </RoleBasedRoute>
      </ProtectedRoute>
    );
  }

  if (path === ROUTES.studentApplications) {
    return (
      <ProtectedRoute>
        <RoleBasedRoute allowedRoles={["STUDENT"]}>
          <ApplicationsPage />
        </RoleBasedRoute>
      </ProtectedRoute>
    );
  }

  if (path === ROUTES.organizationDashboard) {
    return (
      <ProtectedRoute>
        <RoleBasedRoute allowedRoles={["ORGANIZATION"]}>
          <OrganizationDashboardPage />
        </RoleBasedRoute>
      </ProtectedRoute>
    );
  }

  if (path === ROUTES.organizationProfile) {
    return (
      <ProtectedRoute>
        <RoleBasedRoute allowedRoles={["ORGANIZATION"]}>
          <OrganizationProfilePage />
        </RoleBasedRoute>
      </ProtectedRoute>
    );
  }

  if (path === ROUTES.organizationOpportunities) {
    return (
      <ProtectedRoute>
        <RoleBasedRoute allowedRoles={["ORGANIZATION"]}>
          <OrganizationOpportunitiesPage />
        </RoleBasedRoute>
      </ProtectedRoute>
    );
  }

  if (path === ROUTES.organizationApplicants) {
    return (
      <ProtectedRoute>
        <RoleBasedRoute allowedRoles={["ORGANIZATION"]}>
          <ApplicantsPage />
        </RoleBasedRoute>
      </ProtectedRoute>
    );
  }

  if (path === ROUTES.adminDashboard) {
    return (
      <ProtectedRoute>
        <RoleBasedRoute allowedRoles={["ADMIN"]}>
          <AdminDashboardPage />
        </RoleBasedRoute>
      </ProtectedRoute>
    );
  }

  if (path === ROUTES.adminPendingOpportunities) {
    return (
      <ProtectedRoute>
        <RoleBasedRoute allowedRoles={["ADMIN"]}>
          <PendingOpportunitiesPage />
        </RoleBasedRoute>
      </ProtectedRoute>
    );
  }

  if (path === ROUTES.adminReports) {
    return (
      <ProtectedRoute>
        <RoleBasedRoute allowedRoles={["ADMIN"]}>
          <ReportsPage />
        </RoleBasedRoute>
      </ProtectedRoute>
    );
  }

  if (path === ROUTES.adminUsers) {
    return (
      <ProtectedRoute>
        <RoleBasedRoute allowedRoles={["ADMIN"]}>
          <UsersPage />
        </RoleBasedRoute>
      </ProtectedRoute>
    );
  }

  if (path === ROUTES.adminCategories) {
    return (
      <ProtectedRoute>
        <RoleBasedRoute allowedRoles={["ADMIN"]}>
          <CategoriesPage />
        </RoleBasedRoute>
      </ProtectedRoute>
    );
  }

  return <NotFoundPage />;
}
