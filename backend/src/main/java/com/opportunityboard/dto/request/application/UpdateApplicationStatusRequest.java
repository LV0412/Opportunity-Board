package com.opportunityboard.dto.request.application;

import com.opportunityboard.common.enums.ApplicationStatus;
import jakarta.validation.constraints.NotNull;

public record UpdateApplicationStatusRequest(
        @NotNull ApplicationStatus status
) {
}
