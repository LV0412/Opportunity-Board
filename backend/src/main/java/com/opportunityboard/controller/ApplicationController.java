package com.opportunityboard.controller;

import com.opportunityboard.common.dto.PageResponse;
import com.opportunityboard.dto.request.application.CreateApplicationRequest;
import com.opportunityboard.dto.request.application.UpdateApplicationStatusRequest;
import com.opportunityboard.dto.response.application.ApplicationResponse;
import com.opportunityboard.security.CustomUserDetails;
import com.opportunityboard.service.application.ApplicationService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api")
public class ApplicationController {
    private final ApplicationService applicationService;

    public ApplicationController(ApplicationService applicationService) {
        this.applicationService = applicationService;
    }

    @PostMapping("/opportunities/{id}/apply")
    @PreAuthorize("hasRole('STUDENT')")
    public ApplicationResponse apply(
            @AuthenticationPrincipal CustomUserDetails currentUser,
            @PathVariable UUID id,
            @Valid @RequestBody CreateApplicationRequest request
    ) {
        return applicationService.apply(currentUser, id, request);
    }

    @GetMapping("/applications/me")
    @PreAuthorize("hasRole('STUDENT')")
    public PageResponse<ApplicationResponse> listMine(
            @AuthenticationPrincipal CustomUserDetails currentUser,
            Pageable pageable
    ) {
        return PageResponse.from(applicationService.listMine(currentUser, pageable));
    }

    @GetMapping("/applications/{id}")
    @PreAuthorize("hasAnyRole('STUDENT', 'ORGANIZATION')")
    public ApplicationResponse getById(
            @AuthenticationPrincipal CustomUserDetails currentUser,
            @PathVariable UUID id
    ) {
        return applicationService.getById(currentUser, id);
    }

    @PatchMapping("/applications/{id}/status")
    @PreAuthorize("hasRole('ORGANIZATION')")
    public ApplicationResponse updateStatus(
            @AuthenticationPrincipal CustomUserDetails currentUser,
            @PathVariable UUID id,
            @Valid @RequestBody UpdateApplicationStatusRequest request
    ) {
        return applicationService.updateStatus(currentUser, id, request);
    }

    @GetMapping("/organizations/me/applications")
    @PreAuthorize("hasRole('ORGANIZATION')")
    public PageResponse<ApplicationResponse> listOrganizationApplications(
            @AuthenticationPrincipal CustomUserDetails currentUser,
            Pageable pageable
    ) {
        return PageResponse.from(applicationService.listOrganizationApplications(currentUser, pageable));
    }
}
