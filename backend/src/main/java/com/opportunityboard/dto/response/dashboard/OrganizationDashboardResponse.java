package com.opportunityboard.dto.response.dashboard;

import java.util.List;

public record OrganizationDashboardResponse(
        long totalOpportunities,
        long pendingOpportunities,
        long approvedOpportunities,
        long totalViews,
        long totalBookmarks,
        long totalApplications,
        List<OrganizationOpportunityMetricResponse> recentOpportunities
) {
}
