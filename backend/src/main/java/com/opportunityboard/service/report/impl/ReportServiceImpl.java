package com.opportunityboard.service.report.impl;

import com.opportunityboard.common.enums.OpportunityStatus;
import com.opportunityboard.dto.request.report.CreateReportRequest;
import com.opportunityboard.dto.request.report.UpdateReportStatusRequest;
import com.opportunityboard.dto.response.report.ReportResponse;
import com.opportunityboard.entity.Opportunity;
import com.opportunityboard.entity.Report;
import com.opportunityboard.entity.User;
import com.opportunityboard.repository.OpportunityRepository;
import com.opportunityboard.repository.ReportRepository;
import com.opportunityboard.repository.UserRepository;
import com.opportunityboard.security.CustomUserDetails;
import com.opportunityboard.service.admin.impl.AdminAuditLogger;
import com.opportunityboard.service.report.ReportService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.UUID;

@Service
public class ReportServiceImpl implements ReportService {
    private final ReportRepository reportRepository;
    private final OpportunityRepository opportunityRepository;
    private final UserRepository userRepository;
    private final ReportMapper reportMapper;
    private final AdminAuditLogger adminAuditLogger;

    public ReportServiceImpl(
            ReportRepository reportRepository,
            OpportunityRepository opportunityRepository,
            UserRepository userRepository,
            ReportMapper reportMapper,
            AdminAuditLogger adminAuditLogger
    ) {
        this.reportRepository = reportRepository;
        this.opportunityRepository = opportunityRepository;
        this.userRepository = userRepository;
        this.reportMapper = reportMapper;
        this.adminAuditLogger = adminAuditLogger;
    }

    @Override
    @Transactional
    public ReportResponse create(CustomUserDetails currentUser, UUID opportunityId, CreateReportRequest request) {
        Opportunity opportunity = opportunityRepository.findById(opportunityId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Opportunity not found"));
        if (opportunity.getStatus() != OpportunityStatus.APPROVED || isExpired(opportunity)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Opportunity not found");
        }

        User reporter = userRepository.findById(currentUser.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Reporter not found"));

        Report report = new Report();
        report.setOpportunity(opportunity);
        report.setReporter(reporter);
        report.setReason(request.reason().trim());
        report.setDescription(trimToNull(request.description()));
        return reportMapper.toResponse(reportRepository.save(report));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ReportResponse> listReports(Pageable pageable) {
        return reportRepository.findAll(pageable).map(reportMapper::toResponse);
    }

    @Override
    @Transactional
    public ReportResponse updateStatus(CustomUserDetails currentUser, UUID reportId, UpdateReportStatusRequest request) {
        Report report = reportRepository.findById(reportId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Report not found"));
        report.setStatus(request.status());
        Report saved = reportRepository.save(report);
        adminAuditLogger.log(
                currentUser.getId(),
                "REPORT_STATUS_UPDATED",
                "REPORT",
                saved.getId(),
                request.status().name() + (request.note() == null || request.note().isBlank() ? "" : ": " + request.note().trim())
        );
        return reportMapper.toResponse(saved);
    }

    private boolean isExpired(Opportunity opportunity) {
        return opportunity.getDeadlineAt() != null && opportunity.getDeadlineAt().isBefore(Instant.now());
    }

    private String trimToNull(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return value.trim();
    }
}
