package com.opportunityboard.service.opportunity.impl;

import com.opportunityboard.common.enums.OpportunityStatus;
import com.opportunityboard.dto.request.opportunity.OpportunitySearchRequest;
import com.opportunityboard.dto.response.opportunity.OpportunityResponse;
import com.opportunityboard.repository.OpportunityRepository;
import com.opportunityboard.service.opportunity.OpportunitySearchService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
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

        if ("popular".equalsIgnoreCase(trimToNull(request.sort()))) {
            return opportunityRepository.searchApprovedByPopularity(
                    OpportunityStatus.APPROVED,
                    Instant.now(),
                    keywordPattern,
                    categorySlug,
                    locationPattern,
                    request.deadlineBefore(),
                    fieldPattern,
                    skillPattern,
                    skillExact,
                    request.remote(),
                    PageRequest.of(effectivePageable.getPageNumber(), effectivePageable.getPageSize())
            ).map(opportunityMapper::toResponse);
        }

        return opportunityRepository.searchApproved(
                OpportunityStatus.APPROVED,
                Instant.now(),
                keywordPattern,
                categorySlug,
                locationPattern,
                request.deadlineBefore(),
                fieldPattern,
                skillPattern,
                skillExact,
                request.remote(),
                effectivePageable
        ).map(opportunityMapper::toResponse);
    }

    private Pageable normalizePageable(String sortName, Pageable pageable) {
        int page = Math.max(pageable.getPageNumber(), 0);
        int size = pageable.getPageSize() <= 0 ? 12 : Math.min(pageable.getPageSize(), MAX_PAGE_SIZE);

        Sort sort = switch (trimToNull(sortName) == null ? "newest" : sortName.toLowerCase()) {
            case "deadline" -> Sort.by(Sort.Order.asc("deadlineAt").nullsLast(), Sort.Order.desc("createdAt"));
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
