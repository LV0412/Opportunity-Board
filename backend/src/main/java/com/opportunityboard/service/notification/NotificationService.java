package com.opportunityboard.service.notification;

import com.opportunityboard.common.enums.ApplicationStatus;
import com.opportunityboard.dto.response.notification.NotificationResponse;
import com.opportunityboard.entity.Opportunity;
import com.opportunityboard.security.CustomUserDetails;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface NotificationService {
    Page<NotificationResponse> listMine(CustomUserDetails currentUser, Pageable pageable);

    long countUnread(CustomUserDetails currentUser);

    NotificationResponse markAsRead(CustomUserDetails currentUser, UUID notificationId);

    void markAllAsRead(CustomUserDetails currentUser);

    void notifyApplicationStatusChanged(UUID recipientUserId, String opportunityTitle, ApplicationStatus status);

    void notifyOpportunityReviewed(Opportunity opportunity, boolean approved, String note);

    void sendDeadlineReminders();

    void sendWeeklyDigests();
}
