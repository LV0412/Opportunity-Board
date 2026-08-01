package com.opportunityboard.dto.response.organization;

import java.util.UUID;

public record OrganizationProfileResponse(
        UUID id,
        UUID userId,
        String email,
        String fullName,
        String organizationName,
        String industry,
        String websiteUrl,
        String logoUrl,
        String description,
        boolean verified
) {
}
