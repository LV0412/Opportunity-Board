package com.opportunityboard.dto.response.opportunity;

import com.opportunityboard.common.enums.OpportunityStatus;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record OpportunityResponse(
        UUID id,
        String title,
        String description,
        String requirements,
        String location,
        boolean remote,
        String applyUrl,
        Instant deadlineAt,
        OpportunityStatus status,
        String categoryName,
        String categorySlug,
        List<String> tags,
        UUID organizationId,
        String organizationName,
        String organizationLogoUrl,
        boolean organizationVerified,
        long viewCount,
        long bookmarkCount,
        String latestReviewNote,
        Instant createdAt,
        Instant updatedAt
) {
}
