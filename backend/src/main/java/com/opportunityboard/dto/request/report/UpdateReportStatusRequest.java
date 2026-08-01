package com.opportunityboard.dto.request.report;

import com.opportunityboard.common.enums.ReportStatus;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record UpdateReportStatusRequest(
        @NotNull ReportStatus status,
        @Size(max = 500) String note
) {
}
