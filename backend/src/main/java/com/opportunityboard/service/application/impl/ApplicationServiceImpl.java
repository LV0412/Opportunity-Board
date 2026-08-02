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
import com.opportunityboard.infrastructure.storage.StorageService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.time.Instant;
import java.util.UUID;

@Service
public class ApplicationServiceImpl implements ApplicationService {
    private static final long MAX_RESUME_SIZE_BYTES = 5 * 1024 * 1024;
    private static final int MAX_COVER_LETTER_LENGTH = 4000;
    private final ApplicationRepository applicationRepository;
    private final OpportunityRepository opportunityRepository;
    private final StudentProfileRepository studentProfileRepository;
    private final OrganizationProfileRepository organizationProfileRepository;
    private final ResumeRepository resumeRepository;
    private final NotificationService notificationService;
    private final ApplicationMapper applicationMapper;
    private final StorageService storageService;

    public ApplicationServiceImpl(
            ApplicationRepository applicationRepository,
            OpportunityRepository opportunityRepository,
            StudentProfileRepository studentProfileRepository,
            OrganizationProfileRepository organizationProfileRepository,
            ResumeRepository resumeRepository,
            NotificationService notificationService,
            ApplicationMapper applicationMapper,
            StorageService storageService
    ) {
        this.applicationRepository = applicationRepository;
        this.opportunityRepository = opportunityRepository;
        this.studentProfileRepository = studentProfileRepository;
        this.organizationProfileRepository = organizationProfileRepository;
        this.resumeRepository = resumeRepository;
        this.notificationService = notificationService;
        this.applicationMapper = applicationMapper;
        this.storageService = storageService;
    }

    @Override
    @Transactional
    public ApplicationResponse apply(CustomUserDetails currentUser, UUID opportunityId, CreateApplicationRequest request) {
        StudentProfile student = findStudent(currentUser);
        Opportunity opportunity = findOpenApprovedOpportunity(opportunityId);
        ensureNotAlreadyApplied(student, opportunity);
        return saveApplication(student, opportunity, resolveResume(student, request.resumeId()), request.coverLetter());
    }

    @Override
    @Transactional
    public ApplicationResponse applyWithResume(
            CustomUserDetails currentUser,
            UUID opportunityId,
            MultipartFile resumeFile,
            String coverLetter
    ) {
        StudentProfile student = findStudent(currentUser);
        Opportunity opportunity = findOpenApprovedOpportunity(opportunityId);
        ensureNotAlreadyApplied(student, opportunity);
        validateResume(resumeFile);
        validateCoverLetter(coverLetter);

        String fileUrl = storageService.uploadResume(resumeFile);
        Resume resume = new Resume();
        resume.setStudent(student);
        resume.setFileName(safeFileName(resumeFile.getOriginalFilename()));
        resume.setFileUrl(fileUrl);
        resume.setPrimaryResume(resumeRepository.findByStudentId(student.getId()).isEmpty());
        Resume savedResume = resumeRepository.save(resume);

        return saveApplication(student, opportunity, savedResume, coverLetter);
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

    private void ensureNotAlreadyApplied(StudentProfile student, Opportunity opportunity) {
        applicationRepository.findByStudentIdAndOpportunityId(student.getId(), opportunity.getId())
                .ifPresent(existing -> {
                    throw new ResponseStatusException(HttpStatus.CONFLICT, "You already applied to this opportunity");
                });
    }

    private ApplicationResponse saveApplication(
            StudentProfile student,
            Opportunity opportunity,
            Resume resume,
            String coverLetter
    ) {
        validateCoverLetter(coverLetter);
        Application application = new Application();
        application.setStudent(student);
        application.setOpportunity(opportunity);
        application.setResume(resume);
        application.setCoverLetter(trimToNull(coverLetter));
        application.setStatus(ApplicationStatus.APPLIED);
        return applicationMapper.toResponse(applicationRepository.save(application));
    }

    private void validateResume(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Resume file is required");
        }
        if (file.getSize() > MAX_RESUME_SIZE_BYTES) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Resume file must be 5MB or smaller");
        }
        String fileName = file.getOriginalFilename() == null ? "" : file.getOriginalFilename().toLowerCase();
        if (!"application/pdf".equalsIgnoreCase(file.getContentType()) || !fileName.endsWith(".pdf") || !hasPdfSignature(file)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Resume file must be a valid PDF");
        }
    }

    private boolean hasPdfSignature(MultipartFile file) {
        try {
            byte[] header = file.getInputStream().readNBytes(5);
            return header.length == 5
                    && header[0] == '%'
                    && header[1] == 'P'
                    && header[2] == 'D'
                    && header[3] == 'F'
                    && header[4] == '-';
        } catch (IOException exception) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Unable to read resume file");
        }
    }

    private void validateCoverLetter(String coverLetter) {
        if (coverLetter != null && coverLetter.length() > MAX_COVER_LETTER_LENGTH) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cover letter must be 4000 characters or fewer");
        }
    }

    private String safeFileName(String originalFileName) {
        String fileName = originalFileName == null || originalFileName.isBlank() ? "resume.pdf" : originalFileName.trim();
        return fileName.length() <= 120 ? fileName : fileName.substring(fileName.length() - 120);
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
