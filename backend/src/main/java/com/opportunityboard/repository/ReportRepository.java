package com.opportunityboard.repository;

import com.opportunityboard.common.enums.ReportStatus;
import com.opportunityboard.entity.Report;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ReportRepository extends JpaRepository<Report, UUID> {
    Page<Report> findByStatus(ReportStatus status, Pageable pageable);

    long countByStatus(ReportStatus status);

    Page<Report> findByOpportunityId(UUID opportunityId, Pageable pageable);
}
