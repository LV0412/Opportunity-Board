package com.opportunityboard.service.report;

import com.opportunityboard.dto.request.report.CreateReportRequest;
import com.opportunityboard.dto.request.report.UpdateReportStatusRequest;
import com.opportunityboard.dto.response.report.ReportResponse;
import com.opportunityboard.security.CustomUserDetails;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface ReportService {
    ReportResponse create(CustomUserDetails currentUser, UUID opportunityId, CreateReportRequest request);

    Page<ReportResponse> listReports(Pageable pageable);

    ReportResponse updateStatus(CustomUserDetails currentUser, UUID reportId, UpdateReportStatusRequest request);
}
