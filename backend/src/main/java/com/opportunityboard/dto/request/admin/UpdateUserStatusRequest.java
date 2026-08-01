package com.opportunityboard.dto.request.admin;

import com.opportunityboard.common.enums.UserStatus;
import jakarta.validation.constraints.NotNull;

public record UpdateUserStatusRequest(
        @NotNull UserStatus status
) {
}
