package com.opportunityboard.dto.response.application;

import com.opportunityboard.common.enums.ApplicationStatus;

import java.time.Instant;
import java.util.UUID;

public record ApplicationResponse(
        UUID id,
        ApplicationStatus status,
        String coverLetter,
        UUID opportunityId,
        String opportunityTitle,
        String opportunityCategoryName,
        String organizationName,
        UUID studentId,
        String studentName,
        String studentEmail,
        String studentUniversity,
        String studentMajor,
        UUID resumeId,
        String resumeFileName,
        String resumeFileUrl,
        Instant appliedAt,
        Instant updatedAt
) {
}
