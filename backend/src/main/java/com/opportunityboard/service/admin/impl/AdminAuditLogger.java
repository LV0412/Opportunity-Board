package com.opportunityboard.service.admin.impl;

import com.opportunityboard.entity.AdminAuditLog;
import com.opportunityboard.entity.User;
import com.opportunityboard.repository.AdminAuditLogRepository;
import com.opportunityboard.repository.UserRepository;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class AdminAuditLogger {
    private final AdminAuditLogRepository adminAuditLogRepository;
    private final UserRepository userRepository;

    public AdminAuditLogger(AdminAuditLogRepository adminAuditLogRepository, UserRepository userRepository) {
        this.adminAuditLogRepository = adminAuditLogRepository;
        this.userRepository = userRepository;
    }

    public void log(UUID adminId, String action, String targetType, UUID targetId, String details) {
        User admin = userRepository.findById(adminId).orElse(null);
        if (admin == null) {
            return;
        }

        AdminAuditLog log = new AdminAuditLog();
        log.setAdmin(admin);
        log.setAction(action);
        log.setTargetType(targetType);
        log.setTargetId(targetId);
        log.setDetails(details);
        adminAuditLogRepository.save(log);
    }
}
