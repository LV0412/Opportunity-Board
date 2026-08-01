package com.opportunityboard.service.application;

import com.opportunityboard.common.enums.ApplicationStatus;
import com.opportunityboard.common.enums.OpportunityStatus;
import com.opportunityboard.common.enums.UserRole;
import com.opportunityboard.common.enums.UserStatus;
import com.opportunityboard.dto.request.application.CreateApplicationRequest;
import com.opportunityboard.dto.request.application.UpdateApplicationStatusRequest;
import com.opportunityboard.dto.response.application.ApplicationResponse;
import com.opportunityboard.entity.Application;
import com.opportunityboard.entity.Opportunity;
import com.opportunityboard.entity.OrganizationProfile;
import com.opportunityboard.entity.Resume;
import com.opportunityboard.entity.StudentProfile;
import com.opportunityboard.entity.User;
import com.opportunityboard.repository.ApplicationRepository;
import com.opportunityboard.repository.OpportunityRepository;
import com.opportunityboard.repository.OrganizationProfileRepository;
import com.opportunityboard.repository.ResumeRepository;
import com.opportunityboard.repository.StudentProfileRepository;
import com.opportunityboard.security.CustomUserDetails;
import com.opportunityboard.service.application.impl.ApplicationMapper;
import com.opportunityboard.service.application.impl.ApplicationServiceImpl;
import com.opportunityboard.service.notification.NotificationService;
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
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ApplicationServiceTest {
    @Mock
    private ApplicationRepository applicationRepository;

    @Mock
    private OpportunityRepository opportunityRepository;

    @Mock
    private StudentProfileRepository studentProfileRepository;

    @Mock
    private OrganizationProfileRepository organizationProfileRepository;

    @Mock
    private ResumeRepository resumeRepository;

    @Mock
    private NotificationService notificationService;

    @Mock
    private ApplicationMapper applicationMapper;

    @InjectMocks
    private ApplicationServiceImpl applicationService;

    private CustomUserDetails studentUser;
    private StudentProfile studentProfile;
    private Opportunity opportunity;

    @BeforeEach
    void setUp() {
        User student = new User();
        student.setId(UUID.randomUUID());
        student.setEmail("student@example.com");
        student.setPasswordHash("encoded");
        student.setFullName("Student");
        student.setRole(UserRole.STUDENT);
        student.setStatus(UserStatus.ACTIVE);
        studentUser = new CustomUserDetails(student);

        studentProfile = new StudentProfile();
        studentProfile.setId(UUID.randomUUID());
        studentProfile.setUser(student);

        User organizationUser = new User();
        organizationUser.setId(UUID.randomUUID());
        organizationUser.setRole(UserRole.ORGANIZATION);

        OrganizationProfile organization = new OrganizationProfile();
        organization.setId(UUID.randomUUID());
        organization.setUser(organizationUser);
        organization.setOrganizationName("Org");

        opportunity = new Opportunity();
        opportunity.setId(UUID.randomUUID());
        opportunity.setTitle("Internship");
        opportunity.setStatus(OpportunityStatus.APPROVED);
        opportunity.setDeadlineAt(Instant.now().plusSeconds(86_400));
        opportunity.setOrganization(organization);
    }

    @Test
    void applyRejectsDuplicateApplication() {
        when(studentProfileRepository.findByUserId(studentUser.getId())).thenReturn(Optional.of(studentProfile));
        when(opportunityRepository.findById(opportunity.getId())).thenReturn(Optional.of(opportunity));
        Application existing = new Application();
        existing.setId(UUID.randomUUID());
        when(applicationRepository.findByStudentIdAndOpportunityId(studentProfile.getId(), opportunity.getId()))
                .thenReturn(Optional.of(existing));

        assertThatThrownBy(() -> applicationService.apply(
                studentUser,
                opportunity.getId(),
                new CreateApplicationRequest(null, "I am interested")
        )).isInstanceOf(ResponseStatusException.class)
                .satisfies(exception -> {
                    ResponseStatusException responseStatusException = (ResponseStatusException) exception;
                    assertThat(responseStatusException.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
                    assertThat(responseStatusException.getReason()).isEqualTo("You already applied to this opportunity");
                });
    }

    @Test
    void applyRejectsResumeBelongingToAnotherStudent() {
        UUID resumeId = UUID.randomUUID();
        Resume foreignResume = new Resume();
        foreignResume.setId(resumeId);
        StudentProfile anotherStudent = new StudentProfile();
        anotherStudent.setId(UUID.randomUUID());
        foreignResume.setStudent(anotherStudent);

        when(studentProfileRepository.findByUserId(studentUser.getId())).thenReturn(Optional.of(studentProfile));
        when(opportunityRepository.findById(opportunity.getId())).thenReturn(Optional.of(opportunity));
        when(applicationRepository.findByStudentIdAndOpportunityId(studentProfile.getId(), opportunity.getId()))
                .thenReturn(Optional.empty());
        when(resumeRepository.findById(resumeId)).thenReturn(Optional.of(foreignResume));

        assertThatThrownBy(() -> applicationService.apply(
                studentUser,
                opportunity.getId(),
                new CreateApplicationRequest(resumeId, "Cover letter")
        )).isInstanceOf(ResponseStatusException.class)
                .satisfies(exception -> {
                    ResponseStatusException responseStatusException = (ResponseStatusException) exception;
                    assertThat(responseStatusException.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
                    assertThat(responseStatusException.getReason()).isEqualTo("Resume does not belong to current student");
                });
    }

    @Test
    void updateStatusPersistsChangeAndSendsNotification() {
        User organizationUser = opportunity.getOrganization().getUser();
        organizationUser.setEmail("org@example.com");
        organizationUser.setPasswordHash("encoded");
        organizationUser.setFullName("Organization");
        organizationUser.setStatus(UserStatus.ACTIVE);
        CustomUserDetails organizationDetails = new CustomUserDetails(organizationUser);

        Application application = new Application();
        application.setId(UUID.randomUUID());
        application.setOpportunity(opportunity);
        application.setStudent(studentProfile);
        application.setStatus(ApplicationStatus.APPLIED);

        when(applicationRepository.findById(application.getId())).thenReturn(Optional.of(application));
        when(organizationProfileRepository.findByUserId(organizationDetails.getId())).thenReturn(Optional.of(opportunity.getOrganization()));
        when(applicationRepository.save(application)).thenReturn(application);
        when(applicationMapper.toResponse(application)).thenReturn(dummyApplicationResponse(application.getId(), ApplicationStatus.REVIEWING));

        ApplicationResponse response = applicationService.updateStatus(
                organizationDetails,
                application.getId(),
                new UpdateApplicationStatusRequest(ApplicationStatus.REVIEWING)
        );

        assertThat(response.status()).isEqualTo(ApplicationStatus.REVIEWING);
        assertThat(application.getStatus()).isEqualTo(ApplicationStatus.REVIEWING);
        verify(notificationService).notifyApplicationStatusChanged(
                studentUser.getId(),
                "Internship",
                ApplicationStatus.REVIEWING
        );
        ArgumentCaptor<Application> captor = ArgumentCaptor.forClass(Application.class);
        verify(applicationRepository).save(captor.capture());
        assertThat(captor.getValue().getStatus()).isEqualTo(ApplicationStatus.REVIEWING);
    }

    private ApplicationResponse dummyApplicationResponse(UUID id, ApplicationStatus status) {
        return new ApplicationResponse(
                id,
                status,
                "Cover",
                opportunity.getId(),
                opportunity.getTitle(),
                "Internship",
                "Org",
                studentProfile.getId(),
                "Student",
                "student@example.com",
                null,
                null,
                null,
                null,
                null,
                Instant.now(),
                Instant.now()
        );
    }
}
