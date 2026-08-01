package com.opportunityboard.dto.response.report;

import com.opportunityboard.common.enums.ReportStatus;

import java.time.Instant;
import java.util.UUID;

public record ReportResponse(
        UUID id,
        UUID opportunityId,
        String opportunityTitle,
        UUID reporterId,
        String reporterName,
        String reporterEmail,
        String reason,
        String description,
        ReportStatus status,
        Instant createdAt,
        Instant updatedAt
) {
}
