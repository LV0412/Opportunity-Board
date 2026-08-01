package com.opportunityboard.controller;

import com.opportunityboard.dto.request.report.CreateReportRequest;
import com.opportunityboard.dto.response.report.ReportResponse;
import com.opportunityboard.security.CustomUserDetails;
import com.opportunityboard.service.report.ReportService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/opportunities")
public class ReportController {
    private final ReportService reportService;

    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    @PostMapping("/{id}/reports")
    @PreAuthorize("hasRole('STUDENT')")
    public ReportResponse create(
            @AuthenticationPrincipal CustomUserDetails currentUser,
            @PathVariable UUID id,
            @Valid @RequestBody CreateReportRequest request
    ) {
        return reportService.create(currentUser, id, request);
    }
}
