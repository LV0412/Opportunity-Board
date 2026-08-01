import { apiClient } from "../../../config/apiClient";
import type { AdminDashboard, OrganizationDashboard, StudentDashboard } from "../../../types/dashboard";

export const dashboardApi = {
  getStudentDashboard() {
    return apiClient<StudentDashboard>("/dashboard/student");
  },
  getOrganizationDashboard() {
    return apiClient<OrganizationDashboard>("/dashboard/organization");
  },
  getAdminDashboard() {
    return apiClient<AdminDashboard>("/dashboard/admin");
  },
};
