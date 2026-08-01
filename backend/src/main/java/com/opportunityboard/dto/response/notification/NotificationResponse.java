package com.opportunityboard.dto.response.notification;

import com.opportunityboard.common.enums.NotificationType;

import java.time.Instant;
import java.util.UUID;

public record NotificationResponse(
        UUID id,
        NotificationType type,
        String title,
        String message,
        String actionUrl,
        boolean read,
        Instant readAt,
        Instant createdAt
) {
}
