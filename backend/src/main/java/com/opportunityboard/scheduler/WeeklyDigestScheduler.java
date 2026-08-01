package com.opportunityboard.scheduler;

import com.opportunityboard.service.notification.NotificationService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class WeeklyDigestScheduler {
    private final NotificationService notificationService;

    public WeeklyDigestScheduler(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @Scheduled(cron = "${app.scheduler.weekly-digest-cron:0 0 9 * * MON}")
    public void run() {
        notificationService.sendWeeklyDigests();
    }
}
