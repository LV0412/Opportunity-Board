package com.opportunityboard.service.opportunity.impl;

import com.opportunityboard.common.enums.OpportunityStatus;
import com.opportunityboard.dto.request.opportunity.OpportunitySearchRequest;
import com.opportunityboard.dto.response.opportunity.OpportunityResponse;
import com.opportunityboard.entity.Opportunity;
import com.opportunityboard.repository.OpportunityRepository;
import com.opportunityboard.service.opportunity.OpportunitySearchService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.criteria.Predicate;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Service
public class OpportunitySearchServiceImpl implements OpportunitySearchService {
    private static final int MAX_PAGE_SIZE = 50;

    private final OpportunityRepository opportunityRepository;
    private final OpportunityMapper opportunityMapper;

    public OpportunitySearchServiceImpl(
            OpportunityRepository opportunityRepository,
            OpportunityMapper opportunityMapper
    ) {
        this.opportunityRepository = opportunityRepository;
        this.opportunityMapper = opportunityMapper;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<OpportunityResponse> search(OpportunitySearchRequest request, Pageable pageable) {
        Pageable effectivePageable = normalizePageable(request.sort(), pageable);
        String keyword = trimToNull(request.query());
        String categorySlug = trimToNull(request.categorySlug());
        String location = trimToNull(request.location());
        String field = trimToNull(request.field());
        String skill = trimToNull(request.skill());
        String keywordPattern = containsPattern(keyword);
        String locationPattern = containsPattern(location);
        String fieldPattern = containsPattern(field);
        String skillPattern = containsPattern(skill);
        String skillExact = normalize(skill);

        String requestedSort = trimToNull(request.sort());
        boolean popular = "popular".equalsIgnoreCase(requestedSort);
        boolean deadlineSort = "deadline".equalsIgnoreCase(requestedSort);
        Specification<Opportunity> specification = buildSpecification(
                keywordPattern, categorySlug, locationPattern, request.deadlineBefore(),
                fieldPattern, skillPattern, skillExact, request.remote(), popular, deadlineSort
        );
        Pageable queryPageable = popular || deadlineSort
                ? PageRequest.of(effectivePageable.getPageNumber(), effectivePageable.getPageSize())
                : effectivePageable;

        return opportunityRepository.findAll(specification, queryPageable)
                .map(opportunityMapper::toResponse);
    }

    private Specification<Opportunity> buildSpecification(
            String keywordPattern,
            String categorySlug,
            String locationPattern,
            Instant deadlineBefore,
            String fieldPattern,
            String skillPattern,
            String skillExact,
            Boolean remote,
            boolean popular,
            boolean deadlineSort
    ) {
        Instant now = Instant.now();
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(criteriaBuilder.equal(root.get("status"), OpportunityStatus.APPROVED));
            predicates.add(criteriaBuilder.or(
                    criteriaBuilder.isNull(root.get("deadlineAt")),
                    criteriaBuilder.greaterThanOrEqualTo(root.get("deadlineAt"), now)
            ));

            if (keywordPattern != null) {
                predicates.add(criteriaBuilder.or(
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("title")), keywordPattern),
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("description")), keywordPattern),
                        criteriaBuilder.like(criteriaBuilder.lower(criteriaBuilder.coalesce(root.get("requirements"), "")), keywordPattern),
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("organization").get("organizationName")), keywordPattern)
                ));
            }
            if (categorySlug != null) {
                predicates.add(criteriaBuilder.equal(root.get("category").get("slug"), categorySlug));
            }
            if (locationPattern != null) {
                predicates.add(criteriaBuilder.like(
                        criteriaBuilder.lower(criteriaBuilder.coalesce(root.get("location"), "")),
                        locationPattern
                ));
            }
            if (deadlineBefore != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("deadlineAt"), deadlineBefore));
            }
            if (remote != null) {
                predicates.add(criteriaBuilder.equal(root.get("remote"), remote));
            }
            if (fieldPattern != null) {
                predicates.add(criteriaBuilder.or(
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("category").get("name")), fieldPattern),
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("category").get("slug")), fieldPattern),
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("title")), fieldPattern),
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("description")), fieldPattern)
                ));
            }
            if (skillPattern != null) {
                var tagMatch = query.subquery(Integer.class);
                var correlatedOpportunity = tagMatch.correlate(root);
                var tag = correlatedOpportunity.join("tags");
                tagMatch.select(criteriaBuilder.literal(1)).where(criteriaBuilder.or(
                        criteriaBuilder.equal(criteriaBuilder.lower(tag.get("name")), skillExact),
                        criteriaBuilder.equal(criteriaBuilder.lower(tag.get("slug")), skillExact)
                ));
                predicates.add(criteriaBuilder.or(
                        criteriaBuilder.like(criteriaBuilder.lower(criteriaBuilder.coalesce(root.get("requirements"), "")), skillPattern),
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("description")), skillPattern),
                        criteriaBuilder.exists(tagMatch)
                ));
            }

            if (popular && query.getResultType() != Long.class && query.getResultType() != long.class) {
                query.orderBy(
                        criteriaBuilder.desc(criteriaBuilder.size(root.get("bookmarks"))),
                        criteriaBuilder.desc(root.get("createdAt"))
                );
            } else if (deadlineSort && query.getResultType() != Long.class && query.getResultType() != long.class) {
                query.orderBy(
                        criteriaBuilder.asc(criteriaBuilder.selectCase()
                                .when(criteriaBuilder.isNull(root.get("deadlineAt")), 1)
                                .otherwise(0)),
                        criteriaBuilder.asc(root.get("deadlineAt")),
                        criteriaBuilder.desc(root.get("createdAt"))
                );
            }
            return criteriaBuilder.and(predicates.toArray(Predicate[]::new));
        };
    }

    private Pageable normalizePageable(String sortName, Pageable pageable) {
        int page = Math.max(pageable.getPageNumber(), 0);
        int size = pageable.getPageSize() <= 0 ? 12 : Math.min(pageable.getPageSize(), MAX_PAGE_SIZE);

        Sort sort = switch (trimToNull(sortName) == null ? "newest" : sortName.toLowerCase()) {
            case "deadline" -> Sort.by(Sort.Order.asc("deadlineAt"), Sort.Order.desc("createdAt"));
            case "newest", "popular" -> Sort.by(Sort.Order.desc("createdAt"));
            default -> Sort.by(Sort.Order.desc("createdAt"));
        };
        return PageRequest.of(page, size, sort);
    }

    private String trimToNull(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return value.trim();
    }

    private String containsPattern(String value) {
        String normalized = normalize(value);
        return normalized == null ? null : "%" + normalized + "%";
    }

    private String normalize(String value) {
        String trimmed = trimToNull(value);
        return trimmed == null ? null : trimmed.toLowerCase(Locale.ROOT);
    }
}
