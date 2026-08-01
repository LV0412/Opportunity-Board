package com.opportunityboard.service.notification.impl;

import com.opportunityboard.common.enums.ApplicationStatus;
import com.opportunityboard.common.enums.NotificationType;
import com.opportunityboard.common.enums.OpportunityStatus;
import com.opportunityboard.common.enums.UserStatus;
import com.opportunityboard.dto.response.notification.NotificationResponse;
import com.opportunityboard.entity.Application;
import com.opportunityboard.entity.Bookmark;
import com.opportunityboard.entity.Notification;
import com.opportunityboard.entity.Opportunity;
import com.opportunityboard.entity.Skill;
import com.opportunityboard.entity.StudentProfile;
import com.opportunityboard.entity.User;
import com.opportunityboard.infrastructure.mail.MailService;
import com.opportunityboard.infrastructure.template.EmailTemplateService;
import com.opportunityboard.repository.ApplicationRepository;
import com.opportunityboard.repository.BookmarkRepository;
import com.opportunityboard.repository.NotificationRepository;
import com.opportunityboard.repository.OpportunityRepository;
import com.opportunityboard.repository.StudentProfileRepository;
import com.opportunityboard.repository.UserRepository;
import com.opportunityboard.security.CustomUserDetails;
import com.opportunityboard.service.notification.NotificationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Service
public class NotificationServiceImpl implements NotificationService {
    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;
    private final ApplicationRepository applicationRepository;
    private final BookmarkRepository bookmarkRepository;
    private final OpportunityRepository opportunityRepository;
    private final StudentProfileRepository studentProfileRepository;
    private final MailService mailService;
    private final EmailTemplateService emailTemplateService;
    private final NotificationMapper notificationMapper;

    public NotificationServiceImpl(
            NotificationRepository notificationRepository,
            UserRepository userRepository,
            ApplicationRepository applicationRepository,
            BookmarkRepository bookmarkRepository,
            OpportunityRepository opportunityRepository,
            StudentProfileRepository studentProfileRepository,
            MailService mailService,
            EmailTemplateService emailTemplateService,
            NotificationMapper notificationMapper
    ) {
        this.notificationRepository = notificationRepository;
        this.userRepository = userRepository;
        this.applicationRepository = applicationRepository;
        this.bookmarkRepository = bookmarkRepository;
        this.opportunityRepository = opportunityRepository;
        this.studentProfileRepository = studentProfileRepository;
        this.mailService = mailService;
        this.emailTemplateService = emailTemplateService;
        this.notificationMapper = notificationMapper;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<NotificationResponse> listMine(CustomUserDetails currentUser, Pageable pageable) {
        return notificationRepository.findByUserIdOrderByCreatedAtDesc(currentUser.getId(), pageable)
                .map(notificationMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public long countUnread(CustomUserDetails currentUser) {
        return notificationRepository.countByUserIdAndReadAtIsNull(currentUser.getId());
    }

    @Override
    @Transactional
    public NotificationResponse markAsRead(CustomUserDetails currentUser, UUID notificationId) {
        Notification notification = notificationRepository.findByIdAndUserId(notificationId, currentUser.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Notification not found"));
        if (notification.getReadAt() == null) {
            notification.setReadAt(Instant.now());
        }
        return notificationMapper.toResponse(notificationRepository.save(notification));
    }

    @Override
    @Transactional
    public void markAllAsRead(CustomUserDetails currentUser) {
        Page<Notification> page = notificationRepository.findByUserIdOrderByCreatedAtDesc(
                currentUser.getId(),
                Pageable.ofSize(200)
        );
        Instant readAt = Instant.now();
        page.getContent().forEach(notification -> {
            if (notification.getReadAt() == null) {
                notification.setReadAt(readAt);
            }
        });
        notificationRepository.saveAll(page.getContent());
    }

    @Override
    @Transactional
    public void notifyApplicationStatusChanged(UUID recipientUserId, String opportunityTitle, ApplicationStatus status) {
        User recipient = findUser(recipientUserId);
        Notification notification = new Notification();
        notification.setUser(recipient);
        notification.setType(NotificationType.APPLICATION_STATUS);
        notification.setTitle("Trang thai ung tuyen duoc cap nhat");
        notification.setMessage("Ho so cua ban tai \"" + opportunityTitle + "\" hien o trang thai " + status.name() + ".");
        notification.setActionUrl("/student/applications");
        notificationRepository.save(notification);

        mailService.send(emailTemplateService.applicationStatusChanged(
                recipient.getEmail(),
                recipient.getFullName(),
                opportunityTitle,
                status
        ));
    }

    @Override
    @Transactional
    public void notifyOpportunityReviewed(Opportunity opportunity, boolean approved, String note) {
        User recipient = opportunity.getOrganization().getUser();
        Notification notification = new Notification();
        notification.setUser(recipient);
        notification.setType(approved ? NotificationType.OPPORTUNITY_APPROVED : NotificationType.OPPORTUNITY_REJECTED);
        notification.setTitle(approved ? "Co hoi da duoc phe duyet" : "Co hoi can duoc dieu chinh");
        notification.setMessage(approved
                ? "Co hoi \"" + opportunity.getTitle() + "\" da duoc admin phe duyet."
                : "Co hoi \"" + opportunity.getTitle() + "\" da bi tu choi. " + trimToEmpty(note));
        notification.setActionUrl("/organization/opportunities");
        notificationRepository.save(notification);

        mailService.send(emailTemplateService.opportunityReviewed(
                recipient.getEmail(),
                recipient.getFullName(),
                opportunity.getTitle(),
                approved,
                note
        ));
    }

    @Override
    @Transactional
    public void sendDeadlineReminders() {
        sendDeadlineReminderForDays(3);
        sendDeadlineReminderForDays(1);
    }

    @Override
    @Transactional
    public void sendWeeklyDigests() {
        Instant now = Instant.now();
        Instant createdAfter = now.minus(7, ChronoUnit.DAYS);
        List<Opportunity> recentOpportunities = opportunityRepository
                .findTop10ByStatusAndCreatedAtAfterOrderByCreatedAtDesc(OpportunityStatus.APPROVED, createdAfter);

        if (recentOpportunities.isEmpty()) {
            return;
        }

        Instant weekStart = now.truncatedTo(ChronoUnit.DAYS)
                .minus(now.atZone(java.time.ZoneOffset.UTC).getDayOfWeek().getValue() - 1L, ChronoUnit.DAYS);

        for (StudentProfile student : studentProfileRepository.findAll()) {
            User user = student.getUser();
            if (user.getStatus() != UserStatus.ACTIVE) {
                continue;
            }

            String dedupeKey = "weekly-digest:" + user.getId() + ":" + weekStart;
            if (notificationRepository.existsByDedupeKey(dedupeKey)) {
                continue;
            }

            List<Opportunity> matched = recentOpportunities.stream()
                    .filter(opportunity -> matchesStudent(student, opportunity))
                    .sorted(Comparator.comparing(Opportunity::getCreatedAt).reversed())
                    .limit(5)
                    .toList();

            List<Opportunity> payload = matched.isEmpty()
                    ? recentOpportunities.stream().limit(5).toList()
                    : matched;

            String titles = payload.stream().map(Opportunity::getTitle).reduce((left, right) -> left + ", " + right).orElse("co hoi moi");

            Notification notification = new Notification();
            notification.setUser(user);
            notification.setType(NotificationType.WEEKLY_DIGEST);
            notification.setTitle("Weekly digest co hoi moi");
            notification.setMessage("Tuan nay co " + payload.size() + " co hoi dang chu y: " + titles + ".");
            notification.setActionUrl("/explore");
            notification.setDedupeKey(dedupeKey);
            notificationRepository.save(notification);

            mailService.send(emailTemplateService.weeklyDigest(user.getEmail(), user.getFullName(), payload));
        }
    }

    private void sendDeadlineReminderForDays(int daysLeft) {
        Instant now = Instant.now();
        Instant from = now.plus(daysLeft, ChronoUnit.DAYS).minus(12, ChronoUnit.HOURS);
        Instant to = now.plus(daysLeft, ChronoUnit.DAYS).plus(12, ChronoUnit.HOURS);

        List<Opportunity> opportunities = opportunityRepository.findByStatusAndDeadlineAtBetween(
                OpportunityStatus.APPROVED,
                from,
                to
        );

        for (Opportunity opportunity : opportunities) {
            Map<UUID, User> recipients = collectReminderRecipients(opportunity.getId());
            for (User recipient : recipients.values()) {
                String dedupeKey = "deadline:" + recipient.getId() + ":" + opportunity.getId() + ":" + daysLeft;
                if (notificationRepository.existsByDedupeKey(dedupeKey)) {
                    continue;
                }

                Notification notification = new Notification();
                notification.setUser(recipient);
                notification.setType(NotificationType.DEADLINE_REMINDER);
                notification.setTitle("Sap het han dang ky");
                notification.setMessage("Co hoi \"" + opportunity.getTitle() + "\" se het han sau " + daysLeft + " ngay.");
                notification.setActionUrl("/opportunities/" + opportunity.getId());
                notification.setDedupeKey(dedupeKey);
                notificationRepository.save(notification);

                mailService.send(emailTemplateService.deadlineReminder(
                        recipient.getEmail(),
                        recipient.getFullName(),
                        opportunity,
                        daysLeft
                ));
            }
        }
    }

    private Map<UUID, User> collectReminderRecipients(UUID opportunityId) {
        Map<UUID, User> recipients = new LinkedHashMap<>();

        for (Bookmark bookmark : bookmarkRepository.findAllByOpportunityId(opportunityId)) {
            User user = bookmark.getStudent().getUser();
            if (user.getStatus() == UserStatus.ACTIVE) {
                recipients.put(user.getId(), user);
            }
        }

        for (Application application : applicationRepository.findAllByOpportunityId(opportunityId)) {
            User user = application.getStudent().getUser();
            if (user.getStatus() == UserStatus.ACTIVE) {
                recipients.put(user.getId(), user);
            }
        }

        return recipients;
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
                trimToEmpty(opportunity.getTitle()),
                trimToEmpty(opportunity.getDescription()),
                trimToEmpty(opportunity.getRequirements()),
                trimToEmpty(opportunity.getCategory().getName()),
                trimToEmpty(opportunity.getCategory().getSlug()),
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

    private User findUser(UUID userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
    }

    private String trimToEmpty(String value) {
        return value == null ? "" : value.trim();
    }
}
