package com.opportunityboard.dto.request.admin;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record SaveTagRequest(
        @NotBlank @Size(max = 60) String name,
        @NotBlank @Size(max = 70) String slug
) {
}
