package com.opportunityboard.service.opportunity.impl;

import com.opportunityboard.dto.response.opportunity.OpportunityResponse;
import com.opportunityboard.entity.Opportunity;
import com.opportunityboard.entity.Tag;
import com.opportunityboard.repository.AdminReviewRepository;
import com.opportunityboard.repository.BookmarkRepository;
import org.springframework.stereotype.Component;

@Component
public class OpportunityMapper {
    private final AdminReviewRepository adminReviewRepository;
    private final BookmarkRepository bookmarkRepository;

    public OpportunityMapper(
            AdminReviewRepository adminReviewRepository,
            BookmarkRepository bookmarkRepository
    ) {
        this.adminReviewRepository = adminReviewRepository;
        this.bookmarkRepository = bookmarkRepository;
    }

    public OpportunityResponse toResponse(Opportunity opportunity) {
        String latestReviewNote = adminReviewRepository.findFirstByOpportunityIdOrderByCreatedAtDesc(opportunity.getId())
                .map(review -> review.getNote())
                .orElse(null);

        return new OpportunityResponse(
                opportunity.getId(),
                opportunity.getTitle(),
                opportunity.getDescription(),
                opportunity.getRequirements(),
                opportunity.getLocation(),
                opportunity.isRemote(),
                opportunity.getApplyUrl(),
                opportunity.getDeadlineAt(),
                opportunity.getStatus(),
                opportunity.getCategory().getName(),
                opportunity.getCategory().getSlug(),
                opportunity.getTags().stream().map(Tag::getName).sorted().toList(),
                opportunity.getOrganization().getId(),
                opportunity.getOrganization().getOrganizationName(),
                opportunity.getOrganization().getLogoUrl(),
                opportunity.getViewCount(),
                bookmarkRepository.countByOpportunityId(opportunity.getId()),
                latestReviewNote,
                opportunity.getCreatedAt(),
                opportunity.getUpdatedAt()
        );
    }
}
