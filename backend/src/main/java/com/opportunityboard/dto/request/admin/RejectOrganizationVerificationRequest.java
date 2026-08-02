package com.opportunityboard.dto.request.admin;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RejectOrganizationVerificationRequest(
        @NotBlank @Size(max = 1000) String reason
) {
}
