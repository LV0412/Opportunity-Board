package com.opportunityboard.service.admin;

import com.opportunityboard.dto.request.admin.SaveCategoryRequest;
import com.opportunityboard.dto.request.admin.SaveTagRequest;
import com.opportunityboard.dto.request.admin.UpdateUserStatusRequest;
import com.opportunityboard.dto.request.opportunity.RejectOpportunityRequest;
import com.opportunityboard.dto.request.report.UpdateReportStatusRequest;
import com.opportunityboard.dto.response.admin.AdminUserResponse;
import com.opportunityboard.dto.response.admin.CategoryResponse;
import com.opportunityboard.dto.response.admin.TagResponse;
import com.opportunityboard.dto.response.opportunity.OpportunityResponse;
import com.opportunityboard.dto.response.report.ReportResponse;
import com.opportunityboard.security.CustomUserDetails;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface AdminService {
    Page<OpportunityResponse> listPendingOpportunities(Pageable pageable);

    OpportunityResponse approveOpportunity(CustomUserDetails currentUser, UUID opportunityId);

    OpportunityResponse rejectOpportunity(CustomUserDetails currentUser, UUID opportunityId, RejectOpportunityRequest request);

    Page<ReportResponse> listReports(Pageable pageable);

    ReportResponse updateReportStatus(CustomUserDetails currentUser, UUID reportId, UpdateReportStatusRequest request);

    Page<AdminUserResponse> listUsers(Pageable pageable);

    AdminUserResponse updateUserStatus(CustomUserDetails currentUser, UUID userId, UpdateUserStatusRequest request);

    List<CategoryResponse> listCategories();

    CategoryResponse createCategory(CustomUserDetails currentUser, SaveCategoryRequest request);

    CategoryResponse updateCategory(CustomUserDetails currentUser, UUID categoryId, SaveCategoryRequest request);

    void deleteCategory(CustomUserDetails currentUser, UUID categoryId);

    List<TagResponse> listTags();

    TagResponse createTag(CustomUserDetails currentUser, SaveTagRequest request);

    TagResponse updateTag(CustomUserDetails currentUser, UUID tagId, SaveTagRequest request);

    void deleteTag(CustomUserDetails currentUser, UUID tagId);
}
