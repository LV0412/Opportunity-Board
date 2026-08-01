package com.opportunityboard.dto.response.dashboard;

import com.opportunityboard.common.enums.OpportunityStatus;

import java.time.Instant;
import java.util.UUID;

public record OrganizationOpportunityMetricResponse(
        UUID opportunityId,
        String title,
        OpportunityStatus status,
        Instant deadlineAt,
        long viewCount,
        long bookmarkCount,
        long applicationCount,
        Instant updatedAt
) {
}
