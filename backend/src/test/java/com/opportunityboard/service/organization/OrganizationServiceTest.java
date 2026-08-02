package com.opportunityboard.service.organization;

import com.opportunityboard.common.enums.UserRole;
import com.opportunityboard.common.enums.UserStatus;
import com.opportunityboard.common.enums.VerificationStatus;
import com.opportunityboard.entity.OrganizationProfile;
import com.opportunityboard.entity.User;
import com.opportunityboard.infrastructure.storage.StorageService;
import com.opportunityboard.repository.OrganizationProfileRepository;
import com.opportunityboard.security.CustomUserDetails;
import com.opportunityboard.service.organization.impl.OrganizationServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrganizationServiceTest {
    @Mock
    private OrganizationProfileRepository organizationProfileRepository;

    @Mock
    private StorageService storageService;

    @InjectMocks
    private OrganizationServiceImpl organizationService;

    private OrganizationProfile profile;
    private CustomUserDetails organizationDetails;

    @BeforeEach
    void setUp() {
        User user = new User();
        user.setId(UUID.randomUUID());
        user.setEmail("hello@example.org");
        user.setPasswordHash("encoded");
        user.setFullName("Example Organization");
        user.setRole(UserRole.ORGANIZATION);
        user.setStatus(UserStatus.ACTIVE);
        user.setEmailVerifiedAt(Instant.now());
        organizationDetails = new CustomUserDetails(user);

        profile = new OrganizationProfile();
        profile.setId(UUID.randomUUID());
        profile.setUser(user);
        profile.setOrganizationName("Example Organization");
        profile.setIndustry("Education");
        profile.setWebsiteUrl("https://example.org");
        profile.setLogoUrl("https://cdn.example.org/logo.png");
        profile.setDescription("Student opportunities and learning programs.");
        profile.setVerificationStatus(VerificationStatus.UNVERIFIED);
    }

    @Test
    void requestVerificationMovesCompleteProfileToPending() {
        when(organizationProfileRepository.findByUserId(organizationDetails.getId())).thenReturn(Optional.of(profile));
        when(organizationProfileRepository.save(profile)).thenReturn(profile);

        var response = organizationService.requestVerification(organizationDetails);

        assertThat(response.verificationStatus()).isEqualTo(VerificationStatus.PENDING);
        assertThat(response.verificationRequestedAt()).isNotNull();
        assertThat(profile.getVerificationNote()).isNull();
    }

    @Test
    void requestVerificationRejectsIncompleteProfile() {
        profile.setLogoUrl(null);
        when(organizationProfileRepository.findByUserId(organizationDetails.getId())).thenReturn(Optional.of(profile));

        assertThatThrownBy(() -> organizationService.requestVerification(organizationDetails))
                .isInstanceOf(ResponseStatusException.class)
                .satisfies(exception -> assertThat(((ResponseStatusException) exception).getStatusCode())
                        .isEqualTo(HttpStatus.BAD_REQUEST));
    }
}
