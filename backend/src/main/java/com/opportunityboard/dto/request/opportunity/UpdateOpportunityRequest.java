package com.opportunityboard.dto.request.opportunity;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.Instant;
import java.util.List;

public record UpdateOpportunityRequest(
        @NotBlank @Size(max = 180) String title,
        @NotBlank @Size(max = 8000) String description,
        @Size(max = 4000) String requirements,
        @Size(max = 120) String location,
        boolean remote,
        @Size(max = 255) String applyUrl,
        Instant deadlineAt,
        @NotNull String categorySlug,
        List<@Size(max = 60) String> tags
) {
}
