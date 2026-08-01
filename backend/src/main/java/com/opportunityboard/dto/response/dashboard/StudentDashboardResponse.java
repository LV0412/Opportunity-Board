package com.opportunityboard.dto.response.dashboard;

import com.opportunityboard.dto.response.application.ApplicationResponse;
import com.opportunityboard.dto.response.opportunity.OpportunityResponse;

import java.util.List;

public record StudentDashboardResponse(
        long savedCount,
        long applicationCount,
        long unreadNotificationCount,
        DashboardDeadlineResponse nearestDeadline,
        List<OpportunityResponse> recommendedOpportunities,
        List<ApplicationResponse> recentApplications
) {
}
