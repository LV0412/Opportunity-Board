package com.opportunityboard.controller;

import com.opportunityboard.common.dto.PageResponse;
import com.opportunityboard.dto.response.notification.NotificationResponse;
import com.opportunityboard.security.CustomUserDetails;
import com.opportunityboard.service.notification.NotificationService;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {
    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @GetMapping("/me")
    public PageResponse<NotificationResponse> listMine(
            @AuthenticationPrincipal CustomUserDetails currentUser,
            Pageable pageable
    ) {
        return PageResponse.from(notificationService.listMine(currentUser, pageable));
    }

    @GetMapping("/me/unread-count")
    public Map<String, Long> unreadCount(@AuthenticationPrincipal CustomUserDetails currentUser) {
        return Map.of("count", notificationService.countUnread(currentUser));
    }

    @PatchMapping("/{id}/read")
    public NotificationResponse markAsRead(
            @AuthenticationPrincipal CustomUserDetails currentUser,
            @PathVariable UUID id
    ) {
        return notificationService.markAsRead(currentUser, id);
    }

    @PostMapping("/me/read-all")
    public void markAllAsRead(@AuthenticationPrincipal CustomUserDetails currentUser) {
        notificationService.markAllAsRead(currentUser);
    }
}
