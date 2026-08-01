package com.opportunityboard.service.admin.impl;

import com.opportunityboard.common.enums.UserStatus;
import com.opportunityboard.dto.request.admin.SaveCategoryRequest;
import com.opportunityboard.dto.request.admin.SaveTagRequest;
import com.opportunityboard.dto.request.admin.UpdateUserStatusRequest;
import com.opportunityboard.common.enums.AdminReviewStatus;
import com.opportunityboard.common.enums.OpportunityStatus;
import com.opportunityboard.dto.request.opportunity.RejectOpportunityRequest;
import com.opportunityboard.dto.request.report.UpdateReportStatusRequest;
import com.opportunityboard.dto.response.admin.AdminUserResponse;
import com.opportunityboard.dto.response.admin.CategoryResponse;
import com.opportunityboard.dto.response.admin.TagResponse;
import com.opportunityboard.dto.response.opportunity.OpportunityResponse;
import com.opportunityboard.dto.response.report.ReportResponse;
import com.opportunityboard.entity.AdminReview;
import com.opportunityboard.entity.Opportunity;
import com.opportunityboard.entity.OpportunityCategory;
import com.opportunityboard.entity.Report;
import com.opportunityboard.entity.Tag;
import com.opportunityboard.entity.User;
import com.opportunityboard.repository.OpportunityCategoryRepository;
import com.opportunityboard.repository.AdminReviewRepository;
import com.opportunityboard.repository.OpportunityRepository;
import com.opportunityboard.repository.ReportRepository;
import com.opportunityboard.repository.TagRepository;
import com.opportunityboard.repository.UserRepository;
import com.opportunityboard.security.CustomUserDetails;
import com.opportunityboard.service.admin.AdminService;
import com.opportunityboard.service.notification.NotificationService;
import com.opportunityboard.service.opportunity.impl.OpportunityMapper;
import com.opportunityboard.service.report.impl.ReportMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

@Service
public class AdminServiceImpl implements AdminService {
    private final OpportunityRepository opportunityRepository;
    private final AdminReviewRepository adminReviewRepository;
    private final UserRepository userRepository;
    private final ReportRepository reportRepository;
    private final OpportunityCategoryRepository categoryRepository;
    private final TagRepository tagRepository;
    private final OpportunityMapper opportunityMapper;
    private final ReportMapper reportMapper;
    private final AdminAuditLogger adminAuditLogger;
    private final NotificationService notificationService;

    public AdminServiceImpl(
            OpportunityRepository opportunityRepository,
            AdminReviewRepository adminReviewRepository,
            UserRepository userRepository,
            ReportRepository reportRepository,
            OpportunityCategoryRepository categoryRepository,
            TagRepository tagRepository,
            OpportunityMapper opportunityMapper,
            ReportMapper reportMapper,
            AdminAuditLogger adminAuditLogger,
            NotificationService notificationService
    ) {
        this.opportunityRepository = opportunityRepository;
        this.adminReviewRepository = adminReviewRepository;
        this.userRepository = userRepository;
        this.reportRepository = reportRepository;
        this.categoryRepository = categoryRepository;
        this.tagRepository = tagRepository;
        this.opportunityMapper = opportunityMapper;
        this.reportMapper = reportMapper;
        this.adminAuditLogger = adminAuditLogger;
        this.notificationService = notificationService;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<OpportunityResponse> listPendingOpportunities(Pageable pageable) {
        return opportunityRepository.findByStatus(OpportunityStatus.PENDING, pageable)
                .map(opportunityMapper::toResponse);
    }

    @Override
    @Transactional
    public OpportunityResponse approveOpportunity(CustomUserDetails currentUser, UUID opportunityId) {
        Opportunity opportunity = findOpportunity(opportunityId);
        opportunity.setStatus(OpportunityStatus.APPROVED);
        saveReview(currentUser, opportunity, AdminReviewStatus.APPROVED, "Approved");
        notificationService.notifyOpportunityReviewed(opportunity, true, "Approved");
        adminAuditLogger.log(currentUser.getId(), "OPPORTUNITY_APPROVED", "OPPORTUNITY", opportunity.getId(), opportunity.getTitle());
        return opportunityMapper.toResponse(opportunityRepository.save(opportunity));
    }

    @Override
    @Transactional
    public OpportunityResponse rejectOpportunity(CustomUserDetails currentUser, UUID opportunityId, RejectOpportunityRequest request) {
        Opportunity opportunity = findOpportunity(opportunityId);
        opportunity.setStatus(OpportunityStatus.REJECTED);
        saveReview(currentUser, opportunity, AdminReviewStatus.REJECTED, request.reason().trim());
        notificationService.notifyOpportunityReviewed(opportunity, false, request.reason().trim());
        adminAuditLogger.log(currentUser.getId(), "OPPORTUNITY_REJECTED", "OPPORTUNITY", opportunity.getId(), request.reason().trim());
        return opportunityMapper.toResponse(opportunityRepository.save(opportunity));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ReportResponse> listReports(Pageable pageable) {
        return reportRepository.findAll(pageable).map(reportMapper::toResponse);
    }

    @Override
    @Transactional
    public ReportResponse updateReportStatus(CustomUserDetails currentUser, UUID reportId, UpdateReportStatusRequest request) {
        Report report = reportRepository.findById(reportId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Report not found"));
        report.setStatus(request.status());
        Report saved = reportRepository.save(report);
        adminAuditLogger.log(
                currentUser.getId(),
                "REPORT_STATUS_UPDATED",
                "REPORT",
                saved.getId(),
                request.status().name() + (request.note() == null || request.note().isBlank() ? "" : ": " + request.note().trim())
        );
        return reportMapper.toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<AdminUserResponse> listUsers(Pageable pageable) {
        return userRepository.findAll(pageable)
                .map(user -> new AdminUserResponse(
                        user.getId(),
                        user.getEmail(),
                        user.getFullName(),
                        user.getRole(),
                        user.getStatus(),
                        user.getCreatedAt()
                ));
    }

    @Override
    @Transactional
    public AdminUserResponse updateUserStatus(CustomUserDetails currentUser, UUID userId, UpdateUserStatusRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
        user.setStatus(request.status());
        User saved = userRepository.save(user);
        adminAuditLogger.log(currentUser.getId(), "USER_STATUS_UPDATED", "USER", saved.getId(), request.status().name());
        return new AdminUserResponse(
                saved.getId(),
                saved.getEmail(),
                saved.getFullName(),
                saved.getRole(),
                saved.getStatus(),
                saved.getCreatedAt()
        );
    }

    @Override
    @Transactional(readOnly = true)
    public List<CategoryResponse> listCategories() {
        return categoryRepository.findAll().stream()
                .map(category -> new CategoryResponse(
                        category.getId(),
                        category.getName(),
                        category.getSlug(),
                        category.getDescription()
                ))
                .toList();
    }

    @Override
    @Transactional
    public CategoryResponse createCategory(CustomUserDetails currentUser, SaveCategoryRequest request) {
        String slug = request.slug().trim();
        if (categoryRepository.existsBySlug(slug)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Category slug already exists");
        }

        OpportunityCategory category = new OpportunityCategory();
        applyCategory(category, request);
        OpportunityCategory saved = categoryRepository.save(category);
        adminAuditLogger.log(currentUser.getId(), "CATEGORY_CREATED", "CATEGORY", saved.getId(), saved.getSlug());
        return new CategoryResponse(saved.getId(), saved.getName(), saved.getSlug(), saved.getDescription());
    }

    @Override
    @Transactional
    public CategoryResponse updateCategory(CustomUserDetails currentUser, UUID categoryId, SaveCategoryRequest request) {
        OpportunityCategory category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Category not found"));
        if (categoryRepository.existsBySlugAndIdNot(request.slug().trim(), categoryId)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Category slug already exists");
        }
        applyCategory(category, request);
        OpportunityCategory saved = categoryRepository.save(category);
        adminAuditLogger.log(currentUser.getId(), "CATEGORY_UPDATED", "CATEGORY", saved.getId(), saved.getSlug());
        return new CategoryResponse(saved.getId(), saved.getName(), saved.getSlug(), saved.getDescription());
    }

    @Override
    @Transactional
    public void deleteCategory(CustomUserDetails currentUser, UUID categoryId) {
        OpportunityCategory category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Category not found"));
        if (!category.getOpportunities().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Category is being used by opportunities");
        }
        categoryRepository.delete(category);
        adminAuditLogger.log(currentUser.getId(), "CATEGORY_DELETED", "CATEGORY", categoryId, category.getSlug());
    }

    @Override
    @Transactional(readOnly = true)
    public List<TagResponse> listTags() {
        return tagRepository.findAll().stream()
                .map(tag -> new TagResponse(tag.getId(), tag.getName(), tag.getSlug()))
                .toList();
    }

    @Override
    @Transactional
    public TagResponse createTag(CustomUserDetails currentUser, SaveTagRequest request) {
        String slug = request.slug().trim();
        if (tagRepository.existsBySlug(slug)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Tag slug already exists");
        }
        Tag tag = new Tag();
        applyTag(tag, request);
        Tag saved = tagRepository.save(tag);
        adminAuditLogger.log(currentUser.getId(), "TAG_CREATED", "TAG", saved.getId(), saved.getSlug());
        return new TagResponse(saved.getId(), saved.getName(), saved.getSlug());
    }

    @Override
    @Transactional
    public TagResponse updateTag(CustomUserDetails currentUser, UUID tagId, SaveTagRequest request) {
        Tag tag = tagRepository.findById(tagId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Tag not found"));
        if (tagRepository.existsBySlugAndIdNot(request.slug().trim(), tagId)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Tag slug already exists");
        }
        applyTag(tag, request);
        Tag saved = tagRepository.save(tag);
        adminAuditLogger.log(currentUser.getId(), "TAG_UPDATED", "TAG", saved.getId(), saved.getSlug());
        return new TagResponse(saved.getId(), saved.getName(), saved.getSlug());
    }

    @Override
    @Transactional
    public void deleteTag(CustomUserDetails currentUser, UUID tagId) {
        Tag tag = tagRepository.findById(tagId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Tag not found"));
        if (!tag.getOpportunities().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Tag is being used by opportunities");
        }
        tagRepository.delete(tag);
        adminAuditLogger.log(currentUser.getId(), "TAG_DELETED", "TAG", tagId, tag.getSlug());
    }

    private Opportunity findOpportunity(UUID id) {
        return opportunityRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Opportunity not found"));
    }

    private void saveReview(CustomUserDetails currentUser, Opportunity opportunity, AdminReviewStatus status, String note) {
        User admin = userRepository.findById(currentUser.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Admin user not found"));

        AdminReview review = new AdminReview();
        review.setOpportunity(opportunity);
        review.setAdmin(admin);
        review.setStatus(status);
        review.setNote(note);
        adminReviewRepository.save(review);
    }

    private void applyCategory(OpportunityCategory category, SaveCategoryRequest request) {
        category.setName(request.name().trim());
        category.setSlug(request.slug().trim());
        category.setDescription(request.description() == null || request.description().isBlank() ? null : request.description().trim());
    }

    private void applyTag(Tag tag, SaveTagRequest request) {
        tag.setName(request.name().trim());
        tag.setSlug(request.slug().trim());
    }
}
