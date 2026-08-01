package com.opportunityboard.dto.response.bookmark;

import com.opportunityboard.dto.response.opportunity.OpportunityResponse;

import java.time.Instant;
import java.util.UUID;

public record BookmarkResponse(
        UUID id,
        OpportunityResponse opportunity,
        Instant savedAt
) {
}
