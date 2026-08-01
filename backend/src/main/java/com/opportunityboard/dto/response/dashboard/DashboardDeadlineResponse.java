package com.opportunityboard.dto.response.dashboard;

import java.time.Instant;
import java.util.UUID;

public record DashboardDeadlineResponse(
        String source,
        UUID opportunityId,
        String opportunityTitle,
        String organizationName,
        Instant deadlineAt
) {
}
