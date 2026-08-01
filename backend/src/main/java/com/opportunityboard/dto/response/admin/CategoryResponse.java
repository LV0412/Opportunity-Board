package com.opportunityboard.dto.response.admin;

import java.util.UUID;

public record CategoryResponse(
        UUID id,
        String name,
        String slug,
        String description
) {
}
