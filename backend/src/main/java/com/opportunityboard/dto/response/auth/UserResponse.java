package com.opportunityboard.dto.response.auth;

import com.opportunityboard.common.enums.UserRole;
import com.opportunityboard.common.enums.UserStatus;

import java.util.UUID;

public record UserResponse(
        UUID id,
        String email,
        String fullName,
        UserRole role,
        UserStatus status
) {
}
