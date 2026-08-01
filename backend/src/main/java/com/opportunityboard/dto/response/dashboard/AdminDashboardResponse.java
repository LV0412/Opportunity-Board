package com.opportunityboard.dto.response.dashboard;

import com.opportunityboard.dto.response.opportunity.OpportunityResponse;
import com.opportunityboard.dto.response.report.ReportResponse;

import java.util.List;

public record AdminDashboardResponse(
        long pendingOpportunities,
        long pendingReports,
        long totalUsers,
        long totalStudents,
        long totalOrganizations,
        long totalOpportunities,
        long totalApplications,
        List<OpportunityResponse> recentPendingOpportunities,
        List<ReportResponse> recentReports
) {
}
