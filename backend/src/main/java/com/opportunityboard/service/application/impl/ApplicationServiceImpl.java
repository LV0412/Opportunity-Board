package com.opportunityboard.service.application.impl;

import com.opportunityboard.common.enums.ApplicationStatus;
import com.opportunityboard.common.enums.OpportunityStatus;
import com.opportunityboard.common.enums.UserRole;
import com.opportunityboard.dto.request.application.CreateApplicationRequest;
import com.opportunityboard.dto.request.application.UpdateApplicationStatusRequest;
import com.opportunityboard.dto.response.application.ApplicationResponse;
import com.opportunityboard.entity.Application;
import com.opportunityboard.entity.Opportunity;
import com.opportunityboard.entity.OrganizationProfile;
import com.opportunityboard.entity.Resume;
import com.opportunityboard.entity.StudentProfile;
import com.opportunityboard.repository.ApplicationRepository;
import com.opportunityboard.repository.OpportunityRepository;
import com.opportunityboard.repository.OrganizationProfileRepository;
import com.opportunityboard.repository.ResumeRepository;
import com.opportunityboard.repository.StudentProfileRepository;
import com.opportunityboard.security.CustomUserDetails;
import com.opportunityboard.service.application.ApplicationService;
import com.opportunityboard.service.notification.NotificationService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.UUID;

@Service
public class ApplicationServiceImpl implements ApplicationService {
    private final ApplicationRepository applicationRepository;
    private final OpportunityRepository opportunityRepository;
    private final StudentProfileRepository studentProfileRepository;
    private final OrganizationProfileRepository organizationProfileRepository;
    private final ResumeRepository resumeRepository;
    private final NotificationService notificationService;
    private final ApplicationMapper applicationMapper;

    public ApplicationServiceImpl(
            ApplicationRepository applicationRepository,
            OpportunityRepository opportunityRepository,
            StudentProfileRepository studentProfileRepository,
            OrganizationProfileRepository organizationProfileRepository,
            ResumeRepository resumeRepository,
            NotificationService notificationService,
            ApplicationMapper applicationMapper
    ) {
        this.applicationRepository = applicationRepository;
        this.opportunityRepository = opportunityRepository;
        this.studentProfileRepository = studentProfileRepository;
        this.organizationProfileRepository = organizationProfileRepository;
        this.resumeRepository = resumeRepository;
        this.notificationService = notificationService;
        this.applicationMapper = applicationMapper;
    }

    @Override
    @Transactional
    public ApplicationResponse apply(CustomUserDetails currentUser, UUID opportunityId, CreateApplicationRequest request) {
        StudentProfile student = findStudent(currentUser);
        Opportunity opportunity = findOpenApprovedOpportunity(opportunityId);
        applicationRepository.findByStudentIdAndOpportunityId(student.getId(), opportunity.getId())
                .ifPresent(existing -> {
                    throw new ResponseStatusException(HttpStatus.CONFLICT, "You already applied to this opportunity");
                });

        Application application = new Application();
        application.setStudent(student);
        application.setOpportunity(opportunity);
        application.setResume(resolveResume(student, request.resumeId()));
        application.setCoverLetter(trimToNull(request.coverLetter()));
        application.setStatus(ApplicationStatus.APPLIED);
        return applicationMapper.toResponse(applicationRepository.save(application));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ApplicationResponse> listMine(CustomUserDetails currentUser, Pageable pageable) {
        StudentProfile student = findStudent(currentUser);
        return applicationRepository.findByStudentId(student.getId(), pageable)
                .map(applicationMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public ApplicationResponse getById(CustomUserDetails currentUser, UUID applicationId) {
        Application application = findApplication(applicationId);
        ensureCanView(currentUser, application);
        return applicationMapper.toResponse(application);
    }

    @Override
    @Transactional
    public ApplicationResponse updateStatus(
            CustomUserDetails currentUser,
            UUID applicationId,
            UpdateApplicationStatusRequest request
    ) {
        Application application = findApplication(applicationId);
        OrganizationProfile organization = findOrganization(currentUser);
        ensureOrganizationOwnsApplication(organization, application);

        application.setStatus(request.status());
        Application saved = applicationRepository.save(application);
        notificationService.notifyApplicationStatusChanged(
                saved.getStudent().getUser().getId(),
                saved.getOpportunity().getTitle(),
                saved.getStatus()
        );
        return applicationMapper.toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ApplicationResponse> listOrganizationApplications(CustomUserDetails currentUser, Pageable pageable) {
        OrganizationProfile organization = findOrganization(currentUser);
        return applicationRepository.findByOrganizationId(organization.getId(), pageable)
                .map(applicationMapper::toResponse);
    }

    private StudentProfile findStudent(CustomUserDetails currentUser) {
        return studentProfileRepository.findByUserId(currentUser.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Student profile not found"));
    }

    private OrganizationProfile findOrganization(CustomUserDetails currentUser) {
        return organizationProfileRepository.findByUserId(currentUser.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Organization profile not found"));
    }

    private Application findApplication(UUID applicationId) {
        return applicationRepository.findById(applicationId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Application not found"));
    }

    private Opportunity findOpenApprovedOpportunity(UUID opportunityId) {
        Opportunity opportunity = opportunityRepository.findById(opportunityId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Opportunity not found"));
        if (opportunity.getStatus() != OpportunityStatus.APPROVED || isExpired(opportunity)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Opportunity not found");
        }
        return opportunity;
    }

    private Resume resolveResume(StudentProfile student, UUID resumeId) {
        if (resumeId == null) {
            return resumeRepository.findByStudentIdAndPrimaryResumeTrue(student.getId()).orElse(null);
        }

        Resume resume = resumeRepository.findById(resumeId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Resume not found"));
        if (!resume.getStudent().getId().equals(student.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Resume does not belong to current student");
        }
        return resume;
    }

    private void ensureCanView(CustomUserDetails currentUser, Application application) {
        UserRole role = extractRole(currentUser);
        if (role == UserRole.STUDENT && application.getStudent().getUser().getId().equals(currentUser.getId())) {
            return;
        }
        if (role == UserRole.ORGANIZATION
                && application.getOpportunity().getOrganization().getUser().getId().equals(currentUser.getId())) {
            return;
        }
        throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You cannot access this application");
    }

    private void ensureOrganizationOwnsApplication(OrganizationProfile organization, Application application) {
        if (!application.getOpportunity().getOrganization().getId().equals(organization.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You cannot manage this application");
        }
    }

    private boolean isExpired(Opportunity opportunity) {
        return opportunity.getDeadlineAt() != null && opportunity.getDeadlineAt().isBefore(Instant.now());
    }

    private UserRole extractRole(CustomUserDetails currentUser) {
        String authority = currentUser.getAuthorities().stream()
                .findFirst()
                .map(item -> item.getAuthority().replace("ROLE_", ""))
                .orElse(UserRole.STUDENT.name());
        return UserRole.valueOf(authority);
    }

    private String trimToNull(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return value.trim();
    }
}
