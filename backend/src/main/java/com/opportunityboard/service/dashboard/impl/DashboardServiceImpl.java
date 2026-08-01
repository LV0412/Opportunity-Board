package com.opportunityboard.service.dashboard.impl;

import com.opportunityboard.common.enums.OpportunityStatus;
import com.opportunityboard.common.enums.ReportStatus;
import com.opportunityboard.common.enums.UserRole;
import com.opportunityboard.dto.response.application.ApplicationResponse;
import com.opportunityboard.dto.response.dashboard.AdminDashboardResponse;
import com.opportunityboard.dto.response.dashboard.DashboardDeadlineResponse;
import com.opportunityboard.dto.response.dashboard.OrganizationDashboardResponse;
import com.opportunityboard.dto.response.dashboard.OrganizationOpportunityMetricResponse;
import com.opportunityboard.dto.response.dashboard.StudentDashboardResponse;
import com.opportunityboard.dto.response.opportunity.OpportunityResponse;
import com.opportunityboard.dto.response.report.ReportResponse;
import com.opportunityboard.entity.Application;
import com.opportunityboard.entity.Bookmark;
import com.opportunityboard.entity.Opportunity;
import com.opportunityboard.entity.OrganizationProfile;
import com.opportunityboard.entity.Skill;
import com.opportunityboard.entity.StudentProfile;
import com.opportunityboard.repository.ApplicationRepository;
import com.opportunityboard.repository.BookmarkRepository;
import com.opportunityboard.repository.NotificationRepository;
import com.opportunityboard.repository.OpportunityRepository;
import com.opportunityboard.repository.OrganizationProfileRepository;
import com.opportunityboard.repository.ReportRepository;
import com.opportunityboard.repository.StudentProfileRepository;
import com.opportunityboard.repository.UserRepository;
import com.opportunityboard.security.CustomUserDetails;
import com.opportunityboard.service.application.impl.ApplicationMapper;
import com.opportunityboard.service.dashboard.DashboardService;
import com.opportunityboard.service.opportunity.impl.OpportunityMapper;
import com.opportunityboard.service.report.impl.ReportMapper;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

@Service
public class DashboardServiceImpl implements DashboardService {
    private final StudentProfileRepository studentProfileRepository;
    private final OrganizationProfileRepository organizationProfileRepository;
    private final OpportunityRepository opportunityRepository;
    private final BookmarkRepository bookmarkRepository;
    private final ApplicationRepository applicationRepository;
    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;
    private final ReportRepository reportRepository;
    private final OpportunityMapper opportunityMapper;
    private final ApplicationMapper applicationMapper;
    private final ReportMapper reportMapper;

    public DashboardServiceImpl(
            StudentProfileRepository studentProfileRepository,
            OrganizationProfileRepository organizationProfileRepository,
            OpportunityRepository opportunityRepository,
            BookmarkRepository bookmarkRepository,
            ApplicationRepository applicationRepository,
            NotificationRepository notificationRepository,
            UserRepository userRepository,
            ReportRepository reportRepository,
            OpportunityMapper opportunityMapper,
            ApplicationMapper applicationMapper,
            ReportMapper reportMapper
    ) {
        this.studentProfileRepository = studentProfileRepository;
        this.organizationProfileRepository = organizationProfileRepository;
        this.opportunityRepository = opportunityRepository;
        this.bookmarkRepository = bookmarkRepository;
        this.applicationRepository = applicationRepository;
        this.notificationRepository = notificationRepository;
        this.userRepository = userRepository;
        this.reportRepository = reportRepository;
        this.opportunityMapper = opportunityMapper;
        this.applicationMapper = applicationMapper;
        this.reportMapper = reportMapper;
    }

    @Override
    @Transactional(readOnly = true)
    public StudentDashboardResponse getStudentDashboard(CustomUserDetails currentUser) {
        StudentProfile student = studentProfileRepository.findByUserId(currentUser.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Student profile not found"));

        long savedCount = bookmarkRepository.countByStudentId(student.getId());
        long applicationCount = applicationRepository.countByStudentId(student.getId());
        long unreadNotificationCount = notificationRepository.countByUserIdAndReadAtIsNull(currentUser.getId());

        List<OpportunityResponse> recommended = buildRecommendedOpportunities(student);
        List<ApplicationResponse> recentApplications = applicationRepository
                .findByStudentId(student.getId(), PageRequest.of(0, 3, Sort.by(Sort.Direction.DESC, "updatedAt")))
                .stream()
                .map(applicationMapper::toResponse)
                .toList();

        return new StudentDashboardResponse(
                savedCount,
                applicationCount,
                unreadNotificationCount,
                findNearestDeadline(student),
                recommended,
                recentApplications
        );
    }

    @Override
    @Transactional(readOnly = true)
    public OrganizationDashboardResponse getOrganizationDashboard(CustomUserDetails currentUser) {
        OrganizationProfile organization = organizationProfileRepository.findByUserId(currentUser.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Organization profile not found"));

        List<OrganizationOpportunityMetricResponse> recentOpportunities = opportunityRepository
                .findByOrganizationIdOrderByUpdatedAtDesc(organization.getId(), PageRequest.of(0, 5))
                .stream()
                .map(opportunity -> new OrganizationOpportunityMetricResponse(
                        opportunity.getId(),
                        opportunity.getTitle(),
                        opportunity.getStatus(),
                        opportunity.getDeadlineAt(),
                        opportunity.getViewCount(),
                        bookmarkRepository.countByOpportunityId(opportunity.getId()),
                        applicationRepository.findAllByOpportunityId(opportunity.getId()).size(),
                        opportunity.getUpdatedAt()
                ))
                .toList();

        return new OrganizationDashboardResponse(
                opportunityRepository.countByOrganizationId(organization.getId()),
                opportunityRepository.countByOrganizationIdAndStatus(organization.getId(), OpportunityStatus.PENDING),
                opportunityRepository.countByOrganizationIdAndStatus(organization.getId(), OpportunityStatus.APPROVED),
                opportunityRepository.sumViewCountByOrganizationId(organization.getId()),
                bookmarkRepository.countByOpportunityOrganizationId(organization.getId()),
                applicationRepository.countByOpportunityOrganizationId(organization.getId()),
                recentOpportunities
        );
    }

    @Override
    @Transactional(readOnly = true)
    public AdminDashboardResponse getAdminDashboard() {
        Pageable topFiveNewest = PageRequest.of(0, 5, Sort.by(Sort.Direction.DESC, "createdAt"));
        List<OpportunityResponse> recentPendingOpportunities = opportunityRepository
                .findByStatus(OpportunityStatus.PENDING, topFiveNewest)
                .stream()
                .map(opportunityMapper::toResponse)
                .toList();
        List<ReportResponse> recentReports = reportRepository
                .findByStatus(ReportStatus.PENDING, topFiveNewest)
                .stream()
                .map(reportMapper::toResponse)
                .toList();

        return new AdminDashboardResponse(
                opportunityRepository.countByStatus(OpportunityStatus.PENDING),
                reportRepository.countByStatus(ReportStatus.PENDING),
                userRepository.count(),
                userRepository.countByRole(UserRole.STUDENT),
                userRepository.countByRole(UserRole.ORGANIZATION),
                opportunityRepository.count(),
                applicationRepository.count(),
                recentPendingOpportunities,
                recentReports
        );
    }

    private List<OpportunityResponse> buildRecommendedOpportunities(StudentProfile student) {
        List<Opportunity> candidates = opportunityRepository.findByStatusAndDeadlineAtAfterOrStatusAndDeadlineAtIsNull(
                        OpportunityStatus.APPROVED,
                        Instant.now(),
                        OpportunityStatus.APPROVED,
                        PageRequest.of(0, 12, Sort.by(Sort.Direction.DESC, "createdAt"))
                )
                .getContent();

        List<Opportunity> matched = candidates.stream()
                .filter(opportunity -> matchesStudent(student, opportunity))
                .limit(4)
                .toList();

        List<Opportunity> finalSelection = new ArrayList<>(matched);
        if (finalSelection.size() < 4) {
            Set<java.util.UUID> selectedIds = matched.stream().map(Opportunity::getId).collect(java.util.stream.Collectors.toCollection(LinkedHashSet::new));
            for (Opportunity candidate : candidates) {
                if (selectedIds.add(candidate.getId())) {
                    finalSelection.add(candidate);
                }
                if (finalSelection.size() == 4) {
                    break;
                }
            }
        }

        return finalSelection.stream().map(opportunityMapper::toResponse).toList();
    }

    private DashboardDeadlineResponse findNearestDeadline(StudentProfile student) {
        Instant now = Instant.now();
        List<DashboardDeadlineResponse> deadlines = new ArrayList<>();

        applicationRepository.findByStudentIdOrderByOpportunityDeadline(student.getId(), PageRequest.of(0, 5))
                .stream()
                .filter(item -> item.getOpportunity().getDeadlineAt() != null && item.getOpportunity().getDeadlineAt().isAfter(now))
                .forEach(item -> deadlines.add(new DashboardDeadlineResponse(
                        "APPLICATION",
                        item.getOpportunity().getId(),
                        item.getOpportunity().getTitle(),
                        item.getOpportunity().getOrganization().getOrganizationName(),
                        item.getOpportunity().getDeadlineAt()
                )));

        bookmarkRepository.findByStudentIdOrderByOpportunityDeadline(student.getId(), PageRequest.of(0, 5))
                .stream()
                .filter(item -> item.getOpportunity().getDeadlineAt() != null && item.getOpportunity().getDeadlineAt().isAfter(now))
                .forEach(item -> deadlines.add(new DashboardDeadlineResponse(
                        "BOOKMARK",
                        item.getOpportunity().getId(),
                        item.getOpportunity().getTitle(),
                        item.getOpportunity().getOrganization().getOrganizationName(),
                        item.getOpportunity().getDeadlineAt()
                )));

        return deadlines.stream()
                .min(Comparator.comparing(DashboardDeadlineResponse::deadlineAt))
                .orElse(null);
    }

    private boolean matchesStudent(StudentProfile student, Opportunity opportunity) {
        Set<String> keywords = new LinkedHashSet<>();
        addTokens(keywords, student.getMajor());
        addTokens(keywords, student.getInterests());
        for (Skill skill : student.getSkills()) {
            addTokens(keywords, skill.getName());
            addTokens(keywords, skill.getSlug());
        }

        if (keywords.isEmpty()) {
            return true;
        }

        String content = String.join(" ",
                safe(opportunity.getTitle()),
                safe(opportunity.getDescription()),
                safe(opportunity.getRequirements()),
                safe(opportunity.getCategory().getName()),
                safe(opportunity.getCategory().getSlug()),
                opportunity.getTags().stream().map(tag -> tag.getName() + " " + tag.getSlug()).reduce("", (left, right) -> left + " " + right)
        ).toLowerCase(Locale.ROOT);

        return keywords.stream().anyMatch(content::contains);
    }

    private void addTokens(Set<String> keywords, String value) {
        if (value == null || value.isBlank()) {
            return;
        }
        for (String token : value.toLowerCase(Locale.ROOT).split("[,\\s]+")) {
            String normalized = token.trim();
            if (normalized.length() >= 3) {
                keywords.add(normalized);
            }
        }
    }

    private String safe(String value) {
        return value == null ? "" : value.trim();
    }
}
