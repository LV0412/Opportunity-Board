package com.opportunityboard.service.report.impl;

import com.opportunityboard.dto.response.report.ReportResponse;
import com.opportunityboard.entity.Report;
import org.springframework.stereotype.Component;

@Component
public class ReportMapper {
    public ReportResponse toResponse(Report report) {
        return new ReportResponse(
                report.getId(),
                report.getOpportunity().getId(),
                report.getOpportunity().getTitle(),
                report.getReporter().getId(),
                report.getReporter().getFullName(),
                report.getReporter().getEmail(),
                report.getReason(),
                report.getDescription(),
                report.getStatus(),
                report.getCreatedAt(),
                report.getUpdatedAt()
        );
    }
}
