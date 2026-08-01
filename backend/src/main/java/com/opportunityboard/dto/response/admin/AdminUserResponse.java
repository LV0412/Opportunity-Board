package com.opportunityboard.dto.response.admin;

import com.opportunityboard.common.enums.UserRole;
import com.opportunityboard.common.enums.UserStatus;

import java.time.Instant;
import java.util.UUID;

public record AdminUserResponse(
        UUID id,
        String email,
        String fullName,
        UserRole role,
        UserStatus status,
        Instant createdAt
) {
}
