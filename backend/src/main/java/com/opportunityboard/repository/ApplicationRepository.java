package com.opportunityboard.repository;

import com.opportunityboard.common.enums.ApplicationStatus;
import com.opportunityboard.entity.Application;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;
import java.util.UUID;
import java.util.List;

public interface ApplicationRepository extends JpaRepository<Application, UUID> {
    Page<Application> findByStudentId(UUID studentId, Pageable pageable);

    long countByStudentId(UUID studentId);

    Page<Application> findByOpportunityId(UUID opportunityId, Pageable pageable);

    List<Application> findAllByOpportunityId(UUID opportunityId);

    @Query("select a from Application a where a.opportunity.organization.id = :organizationId")
    Page<Application> findByOrganizationId(UUID organizationId, Pageable pageable);

    @Query("select a from Application a where a.student.id = :studentId order by a.opportunity.deadlineAt asc nulls last, a.createdAt desc")
    Page<Application> findByStudentIdOrderByOpportunityDeadline(UUID studentId, Pageable pageable);

    @Query("select a from Application a where a.opportunity.organization.id = :organizationId order by a.updatedAt desc")
    Page<Application> findRecentByOrganizationId(UUID organizationId, Pageable pageable);

    @Query("select count(a) from Application a where a.opportunity.organization.id = :organizationId")
    long countByOpportunityOrganizationId(UUID organizationId);

    Optional<Application> findByStudentIdAndOpportunityId(UUID studentId, UUID opportunityId);

    long countByStatus(ApplicationStatus status);
}
