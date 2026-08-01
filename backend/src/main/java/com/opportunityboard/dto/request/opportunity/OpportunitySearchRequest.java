package com.opportunityboard.dto.request.opportunity;

import org.springframework.format.annotation.DateTimeFormat;

import java.time.Instant;

public record OpportunitySearchRequest(
        String query,
        String categorySlug,
        String location,
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant deadlineBefore,
        String field,
        String skill,
        Boolean remote,
        String sort
) {
}
