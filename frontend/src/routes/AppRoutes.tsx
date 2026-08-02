import { Route, Routes } from "react-router-dom";
import type { ReactNode } from "react";
import { ROUTES } from "../config/routes";
import AdminDashboardPage from "../pages/admin/AdminDashboardPage";
import CategoriesPage from "../pages/admin/CategoriesPage";
import PendingOpportunitiesPage from "../pages/admin/PendingOpportunitiesPage";
import ReportsPage from "../pages/admin/ReportsPage";
import UsersPage from "../pages/admin/UsersPage";
import OrganizationVerificationsPage from "../pages/admin/OrganizationVerificationsPage";
import LoginPage from "../pages/auth/LoginPage";
import RegisterPage from "../pages/auth/RegisterPage";
import VerifyEmailPage from "../pages/auth/VerifyEmailPage";
import NotFoundPage from "../pages/NotFoundPage";
import ApplicantsPage from "../pages/organization/ApplicantsPage";
import OrganizationDashboardPage from "../pages/organization/OrganizationDashboardPage";
import OrganizationOpportunitiesPage from "../pages/organization/OrganizationOpportunitiesPage";
import OrganizationProfilePage from "../pages/organization/OrganizationProfilePage";
import ExplorePage from "../pages/public/ExplorePage";
import HomePage from "../pages/public/HomePage";
import OpportunityDetailPage from "../pages/public/OpportunityDetailPage";
import ApplicationsPage from "../pages/student/ApplicationsPage";
import ProfilePage from "../pages/student/ProfilePage";
import SavedOpportunitiesPage from "../pages/student/SavedOpportunitiesPage";
import StudentDashboardPage from "../pages/student/StudentDashboardPage";
import { ProtectedRoute } from "./ProtectedRoute";
import { RoleBasedRoute } from "./RoleBasedRoute";

export function AppRoutes() {
  return (
    <Routes>
      <Route path={ROUTES.home} element={<HomePage />} />
      <Route path={ROUTES.login} element={<LoginPage />} />
      <Route path={ROUTES.register} element={<RegisterPage />} />
      <Route path={ROUTES.verifyEmail} element={<VerifyEmailPage />} />
      <Route path={ROUTES.explore} element={<ExplorePage />} />
      <Route
        path={`${ROUTES.opportunityDetail}/:id`}
        element={<ProtectedRoute><OpportunityDetailPage /></ProtectedRoute>}
      />

      <Route
        path={ROUTES.studentDashboard}
        element={<ProtectedRole roles={["STUDENT"]}><StudentDashboardPage /></ProtectedRole>}
      />
      <Route
        path={ROUTES.studentProfile}
        element={<ProtectedRole roles={["STUDENT"]}><ProfilePage /></ProtectedRole>}
      />
      <Route
        path={ROUTES.studentSavedOpportunities}
        element={<ProtectedRole roles={["STUDENT"]}><SavedOpportunitiesPage /></ProtectedRole>}
      />
      <Route
        path={ROUTES.studentApplications}
        element={<ProtectedRole roles={["STUDENT"]}><ApplicationsPage /></ProtectedRole>}
      />

      <Route
        path={ROUTES.organizationDashboard}
        element={<ProtectedRole roles={["ORGANIZATION"]}><OrganizationDashboardPage /></ProtectedRole>}
      />
      <Route
        path={ROUTES.organizationProfile}
        element={<ProtectedRole roles={["ORGANIZATION"]}><OrganizationProfilePage /></ProtectedRole>}
      />
      <Route
        path={ROUTES.organizationOpportunities}
        element={<ProtectedRole roles={["ORGANIZATION"]}><OrganizationOpportunitiesPage /></ProtectedRole>}
      />
      <Route
        path={ROUTES.organizationApplicants}
        element={<ProtectedRole roles={["ORGANIZATION"]}><ApplicantsPage /></ProtectedRole>}
      />

      <Route
        path={ROUTES.adminDashboard}
        element={<ProtectedRole roles={["ADMIN"]}><AdminDashboardPage /></ProtectedRole>}
      />
      <Route
        path={ROUTES.adminPendingOpportunities}
        element={<ProtectedRole roles={["ADMIN"]}><PendingOpportunitiesPage /></ProtectedRole>}
      />
      <Route
        path={ROUTES.adminReports}
        element={<ProtectedRole roles={["ADMIN"]}><ReportsPage /></ProtectedRole>}
      />
      <Route
        path={ROUTES.adminUsers}
        element={<ProtectedRole roles={["ADMIN"]}><UsersPage /></ProtectedRole>}
      />
      <Route
        path={ROUTES.adminOrganizationVerifications}
        element={<ProtectedRole roles={["ADMIN"]}><OrganizationVerificationsPage /></ProtectedRole>}
      />
      <Route
        path={ROUTES.adminCategories}
        element={<ProtectedRole roles={["ADMIN"]}><CategoriesPage /></ProtectedRole>}
      />

      <Route path="*" element={<NotFoundPage />} />
    </Routes>
  );
}

function ProtectedRole({ roles, children }: { roles: Array<"STUDENT" | "ORGANIZATION" | "ADMIN">; children: ReactNode }) {
  return (
    <ProtectedRoute>
      <RoleBasedRoute allowedRoles={roles}>{children}</RoleBasedRoute>
    </ProtectedRoute>
  );
}
