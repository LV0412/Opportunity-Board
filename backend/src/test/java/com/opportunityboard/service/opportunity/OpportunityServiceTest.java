package com.opportunityboard.service.opportunity;

import com.opportunityboard.common.enums.OpportunityStatus;
import com.opportunityboard.common.enums.UserRole;
import com.opportunityboard.common.enums.UserStatus;
import com.opportunityboard.dto.request.opportunity.CreateOpportunityRequest;
import com.opportunityboard.dto.request.opportunity.UpdateOpportunityRequest;
import com.opportunityboard.dto.response.opportunity.OpportunityResponse;
import com.opportunityboard.entity.Opportunity;
import com.opportunityboard.entity.OpportunityCategory;
import com.opportunityboard.entity.OrganizationProfile;
import com.opportunityboard.entity.Tag;
import com.opportunityboard.entity.User;
import com.opportunityboard.repository.OpportunityCategoryRepository;
import com.opportunityboard.repository.OpportunityRepository;
import com.opportunityboard.repository.OrganizationProfileRepository;
import com.opportunityboard.repository.TagRepository;
import com.opportunityboard.security.CustomUserDetails;
import com.opportunityboard.service.opportunity.impl.OpportunityMapper;
import com.opportunityboard.service.opportunity.impl.OpportunityServiceImpl;
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
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OpportunityServiceTest {
    @Mock
    private OpportunityRepository opportunityRepository;

    @Mock
    private OrganizationProfileRepository organizationProfileRepository;

    @Mock
    private OpportunityCategoryRepository categoryRepository;

    @Mock
    private TagRepository tagRepository;

    @Mock
    private OpportunityMapper opportunityMapper;

    @InjectMocks
    private OpportunityServiceImpl opportunityService;

    private CustomUserDetails organizationUser;
    private OrganizationProfile organizationProfile;
    private OpportunityCategory category;

    @BeforeEach
    void setUp() {
        User user = new User();
        user.setId(UUID.randomUUID());
        user.setEmail("org@example.com");
        user.setPasswordHash("encoded");
        user.setFullName("Organization Owner");
        user.setRole(UserRole.ORGANIZATION);
        user.setStatus(UserStatus.ACTIVE);
        organizationUser = new CustomUserDetails(user);

        organizationProfile = new OrganizationProfile();
        organizationProfile.setId(UUID.randomUUID());
        organizationProfile.setUser(user);
        organizationProfile.setOrganizationName("Opportunity Org");

        category = new OpportunityCategory();
        category.setId(UUID.randomUUID());
        category.setName("Internship");
        category.setSlug("internship");
    }

    @Test
    void createNormalizesFieldsCreatesMissingTagAndMarksPending() {
        CreateOpportunityRequest request = new CreateOpportunityRequest(
                "  Frontend Internship  ",
                "  Build useful products.  ",
                "  React basics  ",
                "  Ho Chi Minh City  ",
                true,
                "  https://example.com/apply  ",
                Instant.now().plusSeconds(86_400),
                "internship",
                List.of("Remote Friendly", "React")
        );
        when(organizationProfileRepository.findByUserId(organizationUser.getId())).thenReturn(Optional.of(organizationProfile));
        when(categoryRepository.findBySlug("internship")).thenReturn(Optional.of(category));
        when(tagRepository.findBySlug("remote-friendly")).thenReturn(Optional.empty());
        when(tagRepository.findBySlug("react")).thenReturn(Optional.of(existingTag("react", "React")));
        when(tagRepository.save(any(Tag.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(opportunityRepository.save(any(Opportunity.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(opportunityMapper.toResponse(any(Opportunity.class))).thenReturn(dummyOpportunityResponse());

        opportunityService.create(organizationUser, request);

        ArgumentCaptor<Opportunity> opportunityCaptor = ArgumentCaptor.forClass(Opportunity.class);
        verify(opportunityRepository).save(opportunityCaptor.capture());
        Opportunity saved = opportunityCaptor.getValue();
        assertThat(saved.getTitle()).isEqualTo("Frontend Internship");
        assertThat(saved.getDescription()).isEqualTo("Build useful products.");
        assertThat(saved.getRequirements()).isEqualTo("React basics");
        assertThat(saved.getLocation()).isEqualTo("Ho Chi Minh City");
        assertThat(saved.getApplyUrl()).isEqualTo("https://example.com/apply");
        assertThat(saved.getStatus()).isEqualTo(OpportunityStatus.PENDING);
        assertThat(saved.getCategory().getSlug()).isEqualTo("internship");
        assertThat(saved.getTags()).extracting(Tag::getSlug).containsExactlyInAnyOrder("remote-friendly", "react");
    }

    @Test
    void getPublicOpportunityIncrementsViewCountForApprovedOpportunity() {
        UUID opportunityId = UUID.randomUUID();
        Opportunity opportunity = new Opportunity();
        opportunity.setId(opportunityId);
        opportunity.setStatus(OpportunityStatus.APPROVED);
        opportunity.setViewCount(7);
        opportunity.setDeadlineAt(Instant.now().plusSeconds(3_600));

        when(opportunityRepository.findById(opportunityId)).thenReturn(Optional.of(opportunity));
        when(opportunityRepository.save(any(Opportunity.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(opportunityMapper.toResponse(opportunity)).thenReturn(dummyOpportunityResponse());

        opportunityService.getPublicOpportunity(opportunityId);

        assertThat(opportunity.getViewCount()).isEqualTo(8);
        verify(opportunityRepository).save(opportunity);
    }

    @Test
    void updateRejectsNonOwner() {
        User owner = new User();
        owner.setId(UUID.randomUUID());
        owner.setRole(UserRole.ORGANIZATION);
        OrganizationProfile ownerProfile = new OrganizationProfile();
        ownerProfile.setId(UUID.randomUUID());
        ownerProfile.setUser(owner);

        Opportunity opportunity = new Opportunity();
        opportunity.setId(UUID.randomUUID());
        opportunity.setOrganization(ownerProfile);
        opportunity.setStatus(OpportunityStatus.PENDING);

        when(opportunityRepository.findById(opportunity.getId())).thenReturn(Optional.of(opportunity));

        assertThatThrownBy(() -> opportunityService.update(
                organizationUser,
                opportunity.getId(),
                new UpdateOpportunityRequest(
                        "Updated title",
                        "Updated description",
                        null,
                        null,
                        false,
                        null,
                        Instant.now().plusSeconds(7_200),
                        "internship",
                        List.of()
                )
        )).isInstanceOf(ResponseStatusException.class)
                .satisfies(exception -> {
                    ResponseStatusException responseStatusException = (ResponseStatusException) exception;
                    assertThat(responseStatusException.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
                    assertThat(responseStatusException.getReason()).isEqualTo("You do not own this opportunity");
                });
    }

    private Tag existingTag(String slug, String name) {
        Tag tag = new Tag();
        tag.setId(UUID.randomUUID());
        tag.setSlug(slug);
        tag.setName(name);
        return tag;
    }

    private OpportunityResponse dummyOpportunityResponse() {
        return new OpportunityResponse(
                UUID.randomUUID(),
                "Title",
                "Description",
                null,
                null,
                false,
                null,
                null,
                OpportunityStatus.PENDING,
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
