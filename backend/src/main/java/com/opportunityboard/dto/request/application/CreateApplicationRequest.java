package com.opportunityboard.dto.request.application;

import jakarta.validation.constraints.Size;

import java.util.UUID;

public record CreateApplicationRequest(
        UUID resumeId,
        @Size(max = 4000) String coverLetter
) {
}
