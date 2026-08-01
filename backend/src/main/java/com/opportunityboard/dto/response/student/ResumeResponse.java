package com.opportunityboard.dto.response.student;

import java.util.UUID;

public record ResumeResponse(
        UUID id,
        String fileName,
        String fileUrl,
        boolean primaryResume
) {
}
