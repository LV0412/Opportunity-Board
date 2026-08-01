package com.opportunityboard.dto.request.organization;

import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Pattern;

public record UpdateOrganizationProfileRequest(
        @Size(max = 160) String organizationName,
        @Size(max = 120) String industry,
        @Pattern(regexp = "^$|https?://.+") @Size(max = 255) String websiteUrl,
        @Size(max = 2000) String description
) {
}
