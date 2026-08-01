package com.opportunityboard.service.opportunity;

import com.opportunityboard.dto.request.opportunity.CreateOpportunityRequest;
import com.opportunityboard.dto.request.opportunity.UpdateOpportunityRequest;
import com.opportunityboard.dto.response.opportunity.OpportunityResponse;
import com.opportunityboard.security.CustomUserDetails;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface OpportunityService {
    Page<OpportunityResponse> listApproved(Pageable pageable);

    OpportunityResponse getPublicOpportunity(UUID id);

    Page<OpportunityResponse> listMyOpportunities(CustomUserDetails currentUser, Pageable pageable);

    OpportunityResponse create(CustomUserDetails currentUser, CreateOpportunityRequest request);

    OpportunityResponse update(CustomUserDetails currentUser, UUID id, UpdateOpportunityRequest request);

    void close(CustomUserDetails currentUser, UUID id);
}
