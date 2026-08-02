package com.opportunityboard.dto.response.admin;

import com.opportunityboard.common.enums.VerificationStatus;

import java.time.Instant;
import java.util.UUID;

public record OrganizationVerificationResponse(
        UUID organizationId,
        String organizationName,
        String email,
        String industry,
        String websiteUrl,
        String logoUrl,
        String description,
        VerificationStatus verificationStatus,
        String verificationNote,
        Instant verificationRequestedAt,
        Instant verifiedAt
) {
}
