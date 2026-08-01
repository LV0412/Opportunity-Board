package com.opportunityboard.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.opportunityboard.common.enums.NotificationType;
import com.opportunityboard.common.enums.UserRole;
import com.opportunityboard.repository.NotificationRepository;
import com.opportunityboard.service.notification.NotificationService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class NotificationSchedulerIntegrationTests {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private NotificationRepository notificationRepository;

    @Test
    void deadlineReminderIsNotSentTwiceForSameUserAndOpportunity() throws Exception {
        String organizationToken = registerAndGetToken("reminder-org@example.com", UserRole.ORGANIZATION);
        String adminToken = registerAndGetToken("reminder-admin@example.com", UserRole.ADMIN);
        String studentToken = registerAndGetToken("reminder-student@example.com", UserRole.STUDENT);
        String opportunityId = createOpportunity(organizationToken, "Deadline Reminder Opportunity", Instant.now().plusSeconds(3L * 24 * 3600));
        approve(adminToken, opportunityId);

        mockMvc.perform(post("/api/opportunities/{id}/bookmark", opportunityId)
                        .header("Authorization", "Bearer " + studentToken))
                .andExpect(status().isOk());

        notificationService.sendDeadlineReminders();
        notificationService.sendDeadlineReminders();

        String studentId = currentUserId(studentToken);
        long reminders = notificationRepository.findByUserIdOrderByCreatedAtDesc(
                        java.util.UUID.fromString(studentId),
                        org.springframework.data.domain.Pageable.ofSize(20))
                .stream()
                .filter(item -> item.getType() == NotificationType.DEADLINE_REMINDER)
                .count();

        assertThat(reminders).isEqualTo(1);
    }

    @Test
    void weeklyDigestIsCreatedOncePerStudentForSameWeek() throws Exception {
        String organizationToken = registerAndGetToken("digest-org@example.com", UserRole.ORGANIZATION);
        String adminToken = registerAndGetToken("digest-admin@example.com", UserRole.ADMIN);
        String studentToken = registerAndGetToken("digest-student@example.com", UserRole.STUDENT);
        updateStudentProfile(studentToken, "Software Engineering", "Hackathons, startups", List.of("Java"));

        String opportunityId = createOpportunity(
                organizationToken,
                "Java Startup Internship",
                Instant.now().plusSeconds(10L * 24 * 3600)
        );
        approve(adminToken, opportunityId);

        notificationService.sendWeeklyDigests();
        notificationService.sendWeeklyDigests();

        String studentId = currentUserId(studentToken);
        long digests = notificationRepository.findByUserIdOrderByCreatedAtDesc(
                        java.util.UUID.fromString(studentId),
                        org.springframework.data.domain.Pageable.ofSize(20))
                .stream()
                .filter(item -> item.getType() == NotificationType.WEEKLY_DIGEST)
                .count();

        assertThat(digests).isEqualTo(1);
    }

    private void updateStudentProfile(String studentToken, String major, String interests, List<String> skills) throws Exception {
        mockMvc.perform(patch("/api/students/me")
                        .header("Authorization", "Bearer " + studentToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of(
                                "major", major,
                                "interests", interests,
                                "skills", skills
                        ))))
                .andExpect(status().isOk());
    }

    private String currentUserId(String token) throws Exception {
        String response = mockMvc.perform(get("/api/auth/me")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        return objectMapper.readTree(response).get("id").asText();
    }

    private void approve(String adminToken, String opportunityId) throws Exception {
        mockMvc.perform(post("/api/admin/opportunities/{id}/approve", opportunityId)
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk());
    }

    private String createOpportunity(String token, String title, Instant deadlineAt) throws Exception {
        String response = mockMvc.perform(post("/api/opportunities")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of(
                                "title", title,
                                "description", "Scheduler test opportunity.",
                                "requirements", "Java, startup mindset.",
                                "location", "Ho Chi Minh City",
                                "remote", true,
                                "applyUrl", "https://example.com/apply",
                                "deadlineAt", deadlineAt.toString(),
                                "categorySlug", "internship",
                                "tags", List.of("Paid")
                        ))))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        return objectMapper.readTree(response).get("id").asText();
    }

    private String registerAndGetToken(String email, UserRole role) throws Exception {
        Map<String, String> body = new HashMap<>();
        body.put("email", email);
        body.put("password", "password123");
        body.put("fullName", role == UserRole.ADMIN ? "Notification Admin" : "Notification User");
        body.put("role", role.name());
        if (role == UserRole.ORGANIZATION) {
            body.put("organizationName", email.substring(0, email.indexOf("@")));
        }

        String response = mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        JsonNode json = objectMapper.readTree(response);
        return json.get("accessToken").asText();
    }
}
