package com.opportunityboard.scheduler;

import com.opportunityboard.service.notification.NotificationService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class DeadlineReminderScheduler {
    private final NotificationService notificationService;

    public DeadlineReminderScheduler(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @Scheduled(cron = "${app.scheduler.deadline-reminder-cron:0 0 8 * * *}")
    public void run() {
        notificationService.sendDeadlineReminders();
    }
}
