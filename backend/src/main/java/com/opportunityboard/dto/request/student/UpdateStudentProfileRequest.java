package com.opportunityboard.dto.request.student;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;

import java.util.List;

public record UpdateStudentProfileRequest(
        @Size(max = 150) String university,
        @Size(max = 120) String major,
        @Min(2000) @Max(2100) Integer graduationYear,
        @Size(max = 120) String location,
        @Size(max = 2000) String bio,
        @Size(max = 2000) String interests,
        List<@Size(max = 80) String> skills
) {
}
