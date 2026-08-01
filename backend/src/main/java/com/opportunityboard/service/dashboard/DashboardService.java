package com.opportunityboard.service.dashboard;

import com.opportunityboard.dto.response.dashboard.AdminDashboardResponse;
import com.opportunityboard.dto.response.dashboard.OrganizationDashboardResponse;
import com.opportunityboard.dto.response.dashboard.StudentDashboardResponse;
import com.opportunityboard.security.CustomUserDetails;

public interface DashboardService {
    StudentDashboardResponse getStudentDashboard(CustomUserDetails currentUser);

    OrganizationDashboardResponse getOrganizationDashboard(CustomUserDetails currentUser);

    AdminDashboardResponse getAdminDashboard();
}
