package com.opportunityboard.repository;

import com.opportunityboard.common.enums.OpportunityStatus;
import com.opportunityboard.entity.Opportunity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public interface OpportunityRepository extends JpaRepository<Opportunity, UUID> {
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

    @Query(
            value = """
                    select distinct o from Opportunity o
                    join o.organization org
                    join o.category category
                    left join o.tags tag
                    where o.status = :status
                      and (o.deadlineAt is null or o.deadlineAt >= :now)
                      and (:keywordPattern is null
                           or lower(o.title) like :keywordPattern
                           or lower(o.description) like :keywordPattern
                           or lower(coalesce(o.requirements, '')) like :keywordPattern
                           or lower(org.organizationName) like :keywordPattern)
                      and (:categorySlug is null or category.slug = :categorySlug)
                      and (:locationPattern is null or lower(coalesce(o.location, '')) like :locationPattern)
                      and (:deadlineBefore is null or o.deadlineAt <= :deadlineBefore)
                      and (:remote is null or o.remote = :remote)
                      and (:fieldPattern is null
                           or lower(category.name) like :fieldPattern
                           or lower(category.slug) like :fieldPattern
                           or lower(o.title) like :fieldPattern
                           or lower(o.description) like :fieldPattern)
                      and (:skillPattern is null
                           or lower(coalesce(o.requirements, '')) like :skillPattern
                           or lower(o.description) like :skillPattern
                           or lower(tag.name) = :skillExact
                           or lower(tag.slug) = :skillExact)
                    """,
            countQuery = """
                    select count(distinct o) from Opportunity o
                    join o.organization org
                    join o.category category
                    left join o.tags tag
                    where o.status = :status
                      and (o.deadlineAt is null or o.deadlineAt >= :now)
                      and (:keywordPattern is null
                           or lower(o.title) like :keywordPattern
                           or lower(o.description) like :keywordPattern
                           or lower(coalesce(o.requirements, '')) like :keywordPattern
                           or lower(org.organizationName) like :keywordPattern)
                      and (:categorySlug is null or category.slug = :categorySlug)
                      and (:locationPattern is null or lower(coalesce(o.location, '')) like :locationPattern)
                      and (:deadlineBefore is null or o.deadlineAt <= :deadlineBefore)
                      and (:remote is null or o.remote = :remote)
                      and (:fieldPattern is null
                           or lower(category.name) like :fieldPattern
                           or lower(category.slug) like :fieldPattern
                           or lower(o.title) like :fieldPattern
                           or lower(o.description) like :fieldPattern)
                      and (:skillPattern is null
                           or lower(coalesce(o.requirements, '')) like :skillPattern
                           or lower(o.description) like :skillPattern
                           or lower(tag.name) = :skillExact
                           or lower(tag.slug) = :skillExact)
                    """
    )
    Page<Opportunity> searchApproved(
            @Param("status") OpportunityStatus status,
            @Param("now") Instant now,
            @Param("keywordPattern") String keywordPattern,
            @Param("categorySlug") String categorySlug,
            @Param("locationPattern") String locationPattern,
            @Param("deadlineBefore") Instant deadlineBefore,
            @Param("fieldPattern") String fieldPattern,
            @Param("skillPattern") String skillPattern,
            @Param("skillExact") String skillExact,
            @Param("remote") Boolean remote,
            Pageable pageable
    );

    @Query(
            value = """
                    select distinct o from Opportunity o
                    join o.organization org
                    join o.category category
                    left join o.tags tag
                    where o.status = :status
                      and (o.deadlineAt is null or o.deadlineAt >= :now)
                      and (:keywordPattern is null
                           or lower(o.title) like :keywordPattern
                           or lower(o.description) like :keywordPattern
                           or lower(coalesce(o.requirements, '')) like :keywordPattern
                           or lower(org.organizationName) like :keywordPattern)
                      and (:categorySlug is null or category.slug = :categorySlug)
                      and (:locationPattern is null or lower(coalesce(o.location, '')) like :locationPattern)
                      and (:deadlineBefore is null or o.deadlineAt <= :deadlineBefore)
                      and (:remote is null or o.remote = :remote)
                      and (:fieldPattern is null
                           or lower(category.name) like :fieldPattern
                           or lower(category.slug) like :fieldPattern
                           or lower(o.title) like :fieldPattern
                           or lower(o.description) like :fieldPattern)
                      and (:skillPattern is null
                           or lower(coalesce(o.requirements, '')) like :skillPattern
                           or lower(o.description) like :skillPattern
                           or lower(tag.name) = :skillExact
                           or lower(tag.slug) = :skillExact)
                    order by (size(o.bookmarks) + size(o.applications)) desc, o.createdAt desc
                    """,
            countQuery = """
                    select count(distinct o) from Opportunity o
                    join o.organization org
                    join o.category category
                    left join o.tags tag
                    where o.status = :status
                      and (o.deadlineAt is null or o.deadlineAt >= :now)
                      and (:keywordPattern is null
                           or lower(o.title) like :keywordPattern
                           or lower(o.description) like :keywordPattern
                           or lower(coalesce(o.requirements, '')) like :keywordPattern
                           or lower(org.organizationName) like :keywordPattern)
                      and (:categorySlug is null or category.slug = :categorySlug)
                      and (:locationPattern is null or lower(coalesce(o.location, '')) like :locationPattern)
                      and (:deadlineBefore is null or o.deadlineAt <= :deadlineBefore)
                      and (:remote is null or o.remote = :remote)
                      and (:fieldPattern is null
                           or lower(category.name) like :fieldPattern
                           or lower(category.slug) like :fieldPattern
                           or lower(o.title) like :fieldPattern
                           or lower(o.description) like :fieldPattern)
                      and (:skillPattern is null
                           or lower(coalesce(o.requirements, '')) like :skillPattern
                           or lower(o.description) like :skillPattern
                           or lower(tag.name) = :skillExact
                           or lower(tag.slug) = :skillExact)
                    """
    )
    Page<Opportunity> searchApprovedByPopularity(
            @Param("status") OpportunityStatus status,
            @Param("now") Instant now,
            @Param("keywordPattern") String keywordPattern,
            @Param("categorySlug") String categorySlug,
            @Param("locationPattern") String locationPattern,
            @Param("deadlineBefore") Instant deadlineBefore,
            @Param("fieldPattern") String fieldPattern,
            @Param("skillPattern") String skillPattern,
            @Param("skillExact") String skillExact,
            @Param("remote") Boolean remote,
            Pageable pageable
    );
}
