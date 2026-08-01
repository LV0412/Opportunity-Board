package com.opportunityboard.repository;

import com.opportunityboard.entity.OpportunityCategory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface OpportunityCategoryRepository extends JpaRepository<OpportunityCategory, UUID> {
    Optional<OpportunityCategory> findBySlug(String slug);

    boolean existsBySlug(String slug);

    boolean existsBySlugAndIdNot(String slug, UUID id);
}
