package com.opportunityboard.dto.request.admin;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record SaveCategoryRequest(
        @NotBlank @Size(max = 80) String name,
        @NotBlank @Size(max = 90) String slug,
        @Size(max = 2000) String description
) {
}
