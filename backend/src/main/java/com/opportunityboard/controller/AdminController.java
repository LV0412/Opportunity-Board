package com.opportunityboard.controller;

import com.opportunityboard.common.dto.PageResponse;
import com.opportunityboard.dto.request.admin.SaveCategoryRequest;
import com.opportunityboard.dto.request.admin.SaveTagRequest;
import com.opportunityboard.dto.request.admin.UpdateUserStatusRequest;
import com.opportunityboard.dto.request.admin.RejectOrganizationVerificationRequest;
import com.opportunityboard.dto.request.opportunity.RejectOpportunityRequest;
import com.opportunityboard.dto.request.report.UpdateReportStatusRequest;
import com.opportunityboard.dto.response.admin.AdminUserResponse;
import com.opportunityboard.dto.response.admin.CategoryResponse;
import com.opportunityboard.dto.response.admin.TagResponse;
import com.opportunityboard.dto.response.admin.OrganizationVerificationResponse;
import com.opportunityboard.dto.response.opportunity.OpportunityResponse;
import com.opportunityboard.dto.response.report.ReportResponse;
import com.opportunityboard.security.CustomUserDetails;
import com.opportunityboard.service.admin.AdminService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {
    private final AdminService adminService;

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    @GetMapping("/opportunities/pending")
    public PageResponse<OpportunityResponse> listPendingOpportunities(Pageable pageable) {
        return PageResponse.from(adminService.listPendingOpportunities(pageable));
    }

    @PostMapping("/opportunities/{id}/approve")
    public OpportunityResponse approve(
            @AuthenticationPrincipal CustomUserDetails currentUser,
            @PathVariable UUID id
    ) {
        return adminService.approveOpportunity(currentUser, id);
    }

    @PostMapping("/opportunities/{id}/reject")
    public OpportunityResponse reject(
            @AuthenticationPrincipal CustomUserDetails currentUser,
            @PathVariable UUID id,
            @Valid @RequestBody RejectOpportunityRequest request
    ) {
        return adminService.rejectOpportunity(currentUser, id, request);
    }

    @GetMapping("/reports")
    public PageResponse<ReportResponse> listReports(Pageable pageable) {
        return PageResponse.from(adminService.listReports(pageable));
    }

    @PatchMapping("/reports/{id}/status")
    public ReportResponse updateReportStatus(
            @AuthenticationPrincipal CustomUserDetails currentUser,
            @PathVariable UUID id,
            @Valid @RequestBody UpdateReportStatusRequest request
    ) {
        return adminService.updateReportStatus(currentUser, id, request);
    }

    @GetMapping("/users")
    public PageResponse<AdminUserResponse> listUsers(Pageable pageable) {
        return PageResponse.from(adminService.listUsers(pageable));
    }

    @PatchMapping("/users/{id}/status")
    public AdminUserResponse updateUserStatus(
            @AuthenticationPrincipal CustomUserDetails currentUser,
            @PathVariable UUID id,
            @Valid @RequestBody UpdateUserStatusRequest request
    ) {
        return adminService.updateUserStatus(currentUser, id, request);
    }

    @GetMapping("/organization-verifications")
    public PageResponse<OrganizationVerificationResponse> listPendingOrganizationVerifications(Pageable pageable) {
        return PageResponse.from(adminService.listPendingOrganizationVerifications(pageable));
    }

    @PostMapping("/organization-verifications/{id}/approve")
    public OrganizationVerificationResponse approveOrganizationVerification(
            @AuthenticationPrincipal CustomUserDetails currentUser,
            @PathVariable UUID id
    ) {
        return adminService.approveOrganizationVerification(currentUser, id);
    }

    @PostMapping("/organization-verifications/{id}/reject")
    public OrganizationVerificationResponse rejectOrganizationVerification(
            @AuthenticationPrincipal CustomUserDetails currentUser,
            @PathVariable UUID id,
            @Valid @RequestBody RejectOrganizationVerificationRequest request
    ) {
        return adminService.rejectOrganizationVerification(currentUser, id, request);
    }

    @GetMapping("/categories")
    public List<CategoryResponse> listCategories() {
        return adminService.listCategories();
    }

    @PostMapping("/categories")
    public CategoryResponse createCategory(
            @AuthenticationPrincipal CustomUserDetails currentUser,
            @Valid @RequestBody SaveCategoryRequest request
    ) {
        return adminService.createCategory(currentUser, request);
    }

    @PatchMapping("/categories/{id}")
    public CategoryResponse updateCategory(
            @AuthenticationPrincipal CustomUserDetails currentUser,
            @PathVariable UUID id,
            @Valid @RequestBody SaveCategoryRequest request
    ) {
        return adminService.updateCategory(currentUser, id, request);
    }

    @DeleteMapping("/categories/{id}")
    public void deleteCategory(
            @AuthenticationPrincipal CustomUserDetails currentUser,
            @PathVariable UUID id
    ) {
        adminService.deleteCategory(currentUser, id);
    }

    @GetMapping("/tags")
    public List<TagResponse> listTags() {
        return adminService.listTags();
    }

    @PostMapping("/tags")
    public TagResponse createTag(
            @AuthenticationPrincipal CustomUserDetails currentUser,
            @Valid @RequestBody SaveTagRequest request
    ) {
        return adminService.createTag(currentUser, request);
    }

    @PatchMapping("/tags/{id}")
    public TagResponse updateTag(
            @AuthenticationPrincipal CustomUserDetails currentUser,
            @PathVariable UUID id,
            @Valid @RequestBody SaveTagRequest request
    ) {
        return adminService.updateTag(currentUser, id, request);
    }

    @DeleteMapping("/tags/{id}")
    public void deleteTag(
            @AuthenticationPrincipal CustomUserDetails currentUser,
            @PathVariable UUID id
    ) {
        adminService.deleteTag(currentUser, id);
    }
}
