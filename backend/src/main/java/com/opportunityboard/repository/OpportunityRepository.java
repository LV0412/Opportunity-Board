package com.opportunityboard.repository;

import com.opportunityboard.common.enums.OpportunityStatus;
import com.opportunityboard.entity.Opportunity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public interface OpportunityRepository extends JpaRepository<Opportunity, UUID>, JpaSpecificationExecutor<Opportunity> {
    Page<Opportunity> findByStatus(OpportunityStatus status, Pageable pageable);

    long countByStatus(OpportunityStatus status);

    Page<Opportunity> findByStatusAndDeadlineAtAfterOrStatusAndDeadlineAtIsNull(
            OpportunityStatus statusWithDeadline,
            Instant now,
            OpportunityStatus statusWithoutDeadline,
            Pageable pageable
    );

    Page<Opportunity> findByOrganizationId(UUID organizationId, Pageable pageable);

    long countByOrganizationId(UUID organizationId);

    long countByOrganizationIdAndStatus(UUID organizationId, OpportunityStatus status);

    Page<Opportunity> findByOrganizationIdOrderByUpdatedAtDesc(UUID organizationId, Pageable pageable);

    Page<Opportunity> findByCategoryIdAndStatus(UUID categoryId, OpportunityStatus status, Pageable pageable);

    List<Opportunity> findByStatusAndDeadlineAtBetween(OpportunityStatus status, Instant start, Instant end);

    List<Opportunity> findTop10ByStatusAndCreatedAtAfterOrderByCreatedAtDesc(OpportunityStatus status, Instant createdAfter);

    @Query("select coalesce(sum(o.viewCount), 0) from Opportunity o where o.organization.id = :organizationId")
    long sumViewCountByOrganizationId(@Param("organizationId") UUID organizationId);

}
