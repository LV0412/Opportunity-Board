package com.opportunityboard.dto.response.admin;

import java.util.UUID;

public record TagResponse(
        UUID id,
        String name,
        String slug
) {
}
