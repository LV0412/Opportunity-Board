package com.opportunityboard.service.admin;

import com.opportunityboard.common.enums.AdminReviewStatus;
import com.opportunityboard.common.enums.OpportunityStatus;
import com.opportunityboard.common.enums.UserRole;
import com.opportunityboard.common.enums.UserStatus;
import com.opportunityboard.common.enums.VerificationStatus;
import com.opportunityboard.dto.request.admin.SaveCategoryRequest;
import com.opportunityboard.dto.request.opportunity.RejectOpportunityRequest;
import com.opportunityboard.dto.response.admin.CategoryResponse;
import com.opportunityboard.dto.response.opportunity.OpportunityResponse;
import com.opportunityboard.entity.AdminReview;
import com.opportunityboard.entity.Opportunity;
import com.opportunityboard.entity.OpportunityCategory;
import com.opportunityboard.entity.OrganizationProfile;
import com.opportunityboard.entity.User;
import com.opportunityboard.repository.AdminReviewRepository;
import com.opportunityboard.repository.OpportunityCategoryRepository;
import com.opportunityboard.repository.OpportunityRepository;
import com.opportunityboard.repository.OrganizationProfileRepository;
import com.opportunityboard.repository.ReportRepository;
import com.opportunityboard.repository.TagRepository;
import com.opportunityboard.repository.UserRepository;
import com.opportunityboard.security.CustomUserDetails;
import com.opportunityboard.service.admin.impl.AdminAuditLogger;
import com.opportunityboard.service.admin.impl.AdminServiceImpl;
import com.opportunityboard.service.notification.NotificationService;
import com.opportunityboard.service.opportunity.impl.OpportunityMapper;
import com.opportunityboard.service.report.impl.ReportMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AdminServiceTest {
    @Mock
    private OpportunityRepository opportunityRepository;

    @Mock
    private AdminReviewRepository adminReviewRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ReportRepository reportRepository;

    @Mock
    private OpportunityCategoryRepository categoryRepository;

    @Mock
    private TagRepository tagRepository;

    @Mock
    private OpportunityMapper opportunityMapper;

    @Mock
    private ReportMapper reportMapper;

    @Mock
    private AdminAuditLogger adminAuditLogger;

    @Mock
    private NotificationService notificationService;

    @Mock
    private OrganizationProfileRepository organizationProfileRepository;

    @InjectMocks
    private AdminServiceImpl adminService;

    private User adminUser;
    private CustomUserDetails adminDetails;

    @BeforeEach
    void setUp() {
        adminUser = new User();
        adminUser.setId(UUID.randomUUID());
        adminUser.setEmail("admin@example.com");
        adminUser.setPasswordHash("encoded");
        adminUser.setFullName("Admin");
        adminUser.setRole(UserRole.ADMIN);
        adminUser.setStatus(UserStatus.ACTIVE);
        adminDetails = new CustomUserDetails(adminUser);
    }

    @Test
    void approveOpportunitySavesReviewNotifiesOrganizationAndLogsAudit() {
        Opportunity opportunity = new Opportunity();
        opportunity.setId(UUID.randomUUID());
        opportunity.setTitle("Student Fellowship");
        opportunity.setStatus(OpportunityStatus.PENDING);

        when(opportunityRepository.findById(opportunity.getId())).thenReturn(Optional.of(opportunity));
        when(userRepository.findById(adminUser.getId())).thenReturn(Optional.of(adminUser));
        when(adminReviewRepository.save(any(AdminReview.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(opportunityRepository.save(opportunity)).thenReturn(opportunity);
        when(opportunityMapper.toResponse(opportunity)).thenReturn(dummyOpportunityResponse(opportunity.getId(), OpportunityStatus.APPROVED));

        OpportunityResponse response = adminService.approveOpportunity(adminDetails, opportunity.getId());

        assertThat(response.status()).isEqualTo(OpportunityStatus.APPROVED);
        assertThat(opportunity.getStatus()).isEqualTo(OpportunityStatus.APPROVED);
        ArgumentCaptor<AdminReview> reviewCaptor = ArgumentCaptor.forClass(AdminReview.class);
        verify(adminReviewRepository).save(reviewCaptor.capture());
        assertThat(reviewCaptor.getValue().getStatus()).isEqualTo(AdminReviewStatus.APPROVED);
        assertThat(reviewCaptor.getValue().getNote()).isEqualTo("Approved");
        verify(notificationService).notifyOpportunityReviewed(opportunity, true, "Approved");
        verify(adminAuditLogger).log(adminUser.getId(), "OPPORTUNITY_APPROVED", "OPPORTUNITY", opportunity.getId(), opportunity.getTitle());
    }

    @Test
    void createCategoryRejectsDuplicateSlug() {
        when(categoryRepository.existsBySlug("internship")).thenReturn(true);

        assertThatThrownBy(() -> adminService.createCategory(
                adminDetails,
                new SaveCategoryRequest("Internship", "internship", "Description")
        )).isInstanceOf(ResponseStatusException.class)
                .satisfies(exception -> {
                    ResponseStatusException responseStatusException = (ResponseStatusException) exception;
                    assertThat(responseStatusException.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
                    assertThat(responseStatusException.getReason()).isEqualTo("Category slug already exists");
                });
    }

    @Test
    void rejectOpportunityTrimsReasonAndReturnsRejectedStatus() {
        Opportunity opportunity = new Opportunity();
        opportunity.setId(UUID.randomUUID());
        opportunity.setTitle("Scholarship");
        opportunity.setStatus(OpportunityStatus.PENDING);

        when(opportunityRepository.findById(opportunity.getId())).thenReturn(Optional.of(opportunity));
        when(userRepository.findById(adminUser.getId())).thenReturn(Optional.of(adminUser));
        when(opportunityRepository.save(opportunity)).thenReturn(opportunity);
        when(opportunityMapper.toResponse(opportunity)).thenReturn(dummyOpportunityResponse(opportunity.getId(), OpportunityStatus.REJECTED));

        adminService.rejectOpportunity(adminDetails, opportunity.getId(), new RejectOpportunityRequest("  Missing criteria details  "));

        assertThat(opportunity.getStatus()).isEqualTo(OpportunityStatus.REJECTED);
        verify(notificationService).notifyOpportunityReviewed(opportunity, false, "Missing criteria details");
        verify(adminAuditLogger).log(adminUser.getId(), "OPPORTUNITY_REJECTED", "OPPORTUNITY", opportunity.getId(), "Missing criteria details");
    }

    @Test
    void approveOrganizationVerificationSetsBadgeMetadataAndLogsAudit() {
        OrganizationProfile organization = new OrganizationProfile();
        organization.setId(UUID.randomUUID());
        organization.setOrganizationName("Verified Org");
        organization.setIndustry("Technology");
        organization.setWebsiteUrl("https://verified.example");
        organization.setLogoUrl("https://cdn.verified.example/logo.png");
        organization.setDescription("A complete organization profile.");
        organization.setVerificationStatus(VerificationStatus.PENDING);
        User organizationUser = new User();
        organizationUser.setEmail("hello@verified.example");
        organization.setUser(organizationUser);

        when(organizationProfileRepository.findById(organization.getId())).thenReturn(Optional.of(organization));
        when(userRepository.findById(adminUser.getId())).thenReturn(Optional.of(adminUser));
        when(organizationProfileRepository.save(organization)).thenReturn(organization);

        var response = adminService.approveOrganizationVerification(adminDetails, organization.getId());

        assertThat(response.verificationStatus()).isEqualTo(VerificationStatus.VERIFIED);
        assertThat(organization.getVerifiedBy()).isEqualTo(adminUser);
        assertThat(organization.getVerifiedAt()).isNotNull();
        verify(adminAuditLogger).log(adminUser.getId(), "ORGANIZATION_VERIFIED", "ORGANIZATION", organization.getId(), "Verified Org");
    }

    @Test
    void approveOrganizationVerificationRejectsProfileChangedToIncomplete() {
        OrganizationProfile organization = new OrganizationProfile();
        organization.setId(UUID.randomUUID());
        organization.setOrganizationName("Incomplete Org");
        organization.setVerificationStatus(VerificationStatus.PENDING);
        when(organizationProfileRepository.findById(organization.getId())).thenReturn(Optional.of(organization));

        assertThatThrownBy(() -> adminService.approveOrganizationVerification(adminDetails, organization.getId()))
                .isInstanceOf(ResponseStatusException.class)
                .satisfies(exception -> assertThat(((ResponseStatusException) exception).getStatusCode())
                        .isEqualTo(HttpStatus.BAD_REQUEST));
    }

    private OpportunityResponse dummyOpportunityResponse(UUID id, OpportunityStatus status) {
        return new OpportunityResponse(
                id,
                "Title",
                "Description",
                null,
                null,
                false,
                null,
                null,
                status,
                "Internship",
                "internship",
                List.of(),
                UUID.randomUUID(),
                "Org",
                null,
                false,
                0,
                0,
                null,
                Instant.now(),
                Instant.now()
        );
    }
}
