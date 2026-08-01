package com.opportunityboard.repository;

import com.opportunityboard.entity.AdminReview;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface AdminReviewRepository extends JpaRepository<AdminReview, UUID> {
    Page<AdminReview> findByOpportunityId(UUID opportunityId, Pageable pageable);

    Optional<AdminReview> findFirstByOpportunityIdOrderByCreatedAtDesc(UUID opportunityId);
}
