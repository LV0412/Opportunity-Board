package com.opportunityboard.controller;

import com.opportunityboard.dto.request.organization.UpdateOrganizationProfileRequest;
import com.opportunityboard.dto.response.organization.OrganizationProfileResponse;
import com.opportunityboard.security.CustomUserDetails;
import com.opportunityboard.service.organization.OrganizationService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/organizations")
@PreAuthorize("hasRole('ORGANIZATION')")
public class OrganizationController {
    private final OrganizationService organizationService;

    public OrganizationController(OrganizationService organizationService) {
        this.organizationService = organizationService;
    }

    @GetMapping("/me")
    public OrganizationProfileResponse me(@AuthenticationPrincipal CustomUserDetails currentUser) {
        return organizationService.getMyProfile(currentUser);
    }

    @PatchMapping("/me")
    public OrganizationProfileResponse update(
            @AuthenticationPrincipal CustomUserDetails currentUser,
            @Valid @RequestBody UpdateOrganizationProfileRequest request
    ) {
        return organizationService.updateMyProfile(currentUser, request);
    }

    @PostMapping("/me/logo")
    public OrganizationProfileResponse uploadLogo(
            @AuthenticationPrincipal CustomUserDetails currentUser,
            @RequestPart("file") MultipartFile file
    ) {
        return organizationService.uploadLogo(currentUser, file);
    }
}
