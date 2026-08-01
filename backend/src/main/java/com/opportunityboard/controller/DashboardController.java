package com.opportunityboard.controller;

import com.opportunityboard.dto.response.dashboard.AdminDashboardResponse;
import com.opportunityboard.dto.response.dashboard.OrganizationDashboardResponse;
import com.opportunityboard.dto.response.dashboard.StudentDashboardResponse;
import com.opportunityboard.security.CustomUserDetails;
import com.opportunityboard.service.dashboard.DashboardService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {
    private final DashboardService dashboardService;

    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping("/student")
    @PreAuthorize("hasRole('STUDENT')")
    public StudentDashboardResponse student(@AuthenticationPrincipal CustomUserDetails currentUser) {
        return dashboardService.getStudentDashboard(currentUser);
    }

    @GetMapping("/organization")
    @PreAuthorize("hasRole('ORGANIZATION')")
    public OrganizationDashboardResponse organization(@AuthenticationPrincipal CustomUserDetails currentUser) {
        return dashboardService.getOrganizationDashboard(currentUser);
    }

    @GetMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public AdminDashboardResponse admin() {
        return dashboardService.getAdminDashboard();
    }
}
