package com.opportunityboard.dto.request.auth;

import com.opportunityboard.common.enums.UserRole;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record RegisterRequest(
        @NotBlank @Email String email,
        @NotBlank @Size(min = 8, max = 72) String password,
        @NotBlank @Size(max = 120) String fullName,
        @NotNull UserRole role,
        @Size(max = 160) String organizationName,
        @Size(max = 150) String university,
        @Size(max = 120) String major
) {
}
