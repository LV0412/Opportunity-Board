package com.opportunityboard.service.application.impl;

import com.opportunityboard.dto.response.application.ApplicationResponse;
import com.opportunityboard.entity.Application;
import com.opportunityboard.entity.Resume;
import com.opportunityboard.entity.StudentProfile;
import org.springframework.stereotype.Component;

@Component
public class ApplicationMapper {
    public ApplicationResponse toResponse(Application application) {
        StudentProfile student = application.getStudent();
        Resume resume = application.getResume();

        return new ApplicationResponse(
                application.getId(),
                application.getStatus(),
                application.getCoverLetter(),
                application.getOpportunity().getId(),
                application.getOpportunity().getTitle(),
                application.getOpportunity().getCategory().getName(),
                application.getOpportunity().getOrganization().getOrganizationName(),
                student.getId(),
                student.getUser().getFullName(),
                student.getUser().getEmail(),
                student.getUniversity(),
                student.getMajor(),
                resume == null ? null : resume.getId(),
                resume == null ? null : resume.getFileName(),
                resume == null ? null : resume.getFileUrl(),
                application.getCreatedAt(),
                application.getUpdatedAt()
        );
    }
}
