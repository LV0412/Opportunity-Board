package com.opportunityboard.dto.request.report;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateReportRequest(
        @NotBlank @Size(max = 120) String reason,
        @Size(max = 2000) String description
) {
}
