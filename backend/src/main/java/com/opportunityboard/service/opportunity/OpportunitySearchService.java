package com.opportunityboard.service.opportunity;

import com.opportunityboard.dto.request.opportunity.OpportunitySearchRequest;
import com.opportunityboard.dto.response.opportunity.OpportunityResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface OpportunitySearchService {
    Page<OpportunityResponse> search(OpportunitySearchRequest request, Pageable pageable);
}
