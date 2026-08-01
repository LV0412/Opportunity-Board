package com.opportunityboard.controller;

import com.opportunityboard.common.dto.PageResponse;
import com.opportunityboard.dto.request.opportunity.CreateOpportunityRequest;
import com.opportunityboard.dto.request.opportunity.OpportunitySearchRequest;
import com.opportunityboard.dto.request.opportunity.UpdateOpportunityRequest;
import com.opportunityboard.dto.response.opportunity.OpportunityResponse;
import com.opportunityboard.security.CustomUserDetails;
import com.opportunityboard.service.opportunity.OpportunitySearchService;
import com.opportunityboard.service.opportunity.OpportunityService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/opportunities")
public class OpportunityController {
    private final OpportunityService opportunityService;
    private final OpportunitySearchService opportunitySearchService;

    public OpportunityController(
            OpportunityService opportunityService,
            OpportunitySearchService opportunitySearchService
    ) {
        this.opportunityService = opportunityService;
        this.opportunitySearchService = opportunitySearchService;
    }

    @GetMapping
    public PageResponse<OpportunityResponse> listApproved(Pageable pageable) {
        return PageResponse.from(opportunityService.listApproved(pageable));
    }

    @GetMapping("/search")
    public PageResponse<OpportunityResponse> search(OpportunitySearchRequest request, Pageable pageable) {
        return PageResponse.from(opportunitySearchService.search(request, pageable));
    }

    @GetMapping("/{id}")
    public OpportunityResponse getById(@PathVariable UUID id) {
        return opportunityService.getPublicOpportunity(id);
    }

    @GetMapping("/mine")
    @PreAuthorize("hasRole('ORGANIZATION')")
    public PageResponse<OpportunityResponse> listMine(
            @AuthenticationPrincipal CustomUserDetails currentUser,
            Pageable pageable
    ) {
        return PageResponse.from(opportunityService.listMyOpportunities(currentUser, pageable));
    }

    @PostMapping
    @PreAuthorize("hasRole('ORGANIZATION')")
    public OpportunityResponse create(
            @AuthenticationPrincipal CustomUserDetails currentUser,
            @Valid @RequestBody CreateOpportunityRequest request
    ) {
        return opportunityService.create(currentUser, request);
    }

    @PatchMapping("/{id}")
    @PreAuthorize("hasRole('ORGANIZATION')")
    public OpportunityResponse update(
            @AuthenticationPrincipal CustomUserDetails currentUser,
            @PathVariable UUID id,
            @Valid @RequestBody UpdateOpportunityRequest request
    ) {
        return opportunityService.update(currentUser, id, request);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ORGANIZATION', 'ADMIN')")
    public void close(
            @AuthenticationPrincipal CustomUserDetails currentUser,
            @PathVariable UUID id
    ) {
        opportunityService.close(currentUser, id);
    }
}
