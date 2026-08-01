package com.opportunityboard.dto.request.opportunity;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RejectOpportunityRequest(
        @NotBlank @Size(max = 1000) String reason
) {
}
