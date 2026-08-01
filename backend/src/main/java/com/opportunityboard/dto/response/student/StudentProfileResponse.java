package com.opportunityboard.dto.response.student;

import java.util.List;
import java.util.UUID;

public record StudentProfileResponse(
        UUID id,
        UUID userId,
        String email,
        String fullName,
        String university,
        String major,
        Integer graduationYear,
        String location,
        String bio,
        String interests,
        List<String> skills,
        List<ResumeResponse> resumes
) {
}
