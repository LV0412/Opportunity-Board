package com.opportunityboard.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.opportunityboard.common.enums.UserRole;
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

import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class DashboardControllerIntegrationTests {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private TestAuthHelper testAuthHelper;

    @Test
    void studentDashboardReturnsStatsRecommendationsAndDeadline() throws Exception {
        String organizationToken = registerAndGetToken("student-dashboard-org@example.com", UserRole.ORGANIZATION);
        String adminToken = registerAndGetToken("student-dashboard-admin@example.com", UserRole.ADMIN);
        String studentToken = registerAndGetToken("student-dashboard-student@example.com", UserRole.STUDENT);
        updateStudentProfile(studentToken, "Software Engineering", "hackathons startups", List.of("Java"));

        String opportunityId = createOpportunity(
                organizationToken,
                "Java Hackathon Internship",
                Instant.parse("2026-08-04T09:00:00Z")
        );
        approve(adminToken, opportunityId);

        mockMvc.perform(post("/api/opportunities/{id}/bookmark", opportunityId)
                        .header("Authorization", "Bearer " + studentToken))
                .andExpect(status().isOk());

        mockMvc.perform(post("/api/opportunities/{id}/apply", opportunityId)
                        .header("Authorization", "Bearer " + studentToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of("coverLetter", "Ready to join."))))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/dashboard/student")
                        .header("Authorization", "Bearer " + studentToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.savedCount").value(1))
                .andExpect(jsonPath("$.applicationCount").value(1))
                .andExpect(jsonPath("$.recommendedOpportunities.length()").value(greaterThanOrEqualTo(1)))
                .andExpect(jsonPath("$.recommendedOpportunities[*].title").value(hasItem("Java Hackathon Internship")))
                .andExpect(jsonPath("$.nearestDeadline.opportunityId").value(opportunityId))
                .andExpect(jsonPath("$.recentApplications.length()").value(1));
    }

    @Test
    void organizationDashboardReturnsPerformanceMetricsIncludingViews() throws Exception {
        String organizationToken = registerAndGetToken("organization-dashboard-org@example.com", UserRole.ORGANIZATION);
        String adminToken = registerAndGetToken("organization-dashboard-admin@example.com", UserRole.ADMIN);
        String studentToken = registerAndGetToken("organization-dashboard-student@example.com", UserRole.STUDENT);

        String opportunityId = createOpportunity(
                organizationToken,
                "Viewed Opportunity",
                Instant.parse("2026-08-12T09:00:00Z")
        );
        approve(adminToken, opportunityId);

        mockMvc.perform(get("/api/opportunities/{id}", opportunityId)).andExpect(status().isOk());
        mockMvc.perform(get("/api/opportunities/{id}", opportunityId)).andExpect(status().isOk());

        mockMvc.perform(post("/api/opportunities/{id}/bookmark", opportunityId)
                        .header("Authorization", "Bearer " + studentToken))
                .andExpect(status().isOk());

        mockMvc.perform(post("/api/opportunities/{id}/apply", opportunityId)
                        .header("Authorization", "Bearer " + studentToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of("coverLetter", "Application for viewed opportunity."))))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/dashboard/organization")
                        .header("Authorization", "Bearer " + organizationToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalOpportunities").value(1))
                .andExpect(jsonPath("$.approvedOpportunities").value(1))
                .andExpect(jsonPath("$.totalViews").value(2))
                .andExpect(jsonPath("$.totalBookmarks").value(1))
                .andExpect(jsonPath("$.totalApplications").value(1))
                .andExpect(jsonPath("$.recentOpportunities[0].viewCount").value(2));
    }

    @Test
    void adminDashboardReturnsModerationCounts() throws Exception {
        String organizationToken = registerAndGetToken("admin-dashboard-org@example.com", UserRole.ORGANIZATION);
        String adminToken = registerAndGetToken("admin-dashboard-admin@example.com", UserRole.ADMIN);
        String studentToken = registerAndGetToken("admin-dashboard-student@example.com", UserRole.STUDENT);

        String pendingOpportunityId = createOpportunity(
                organizationToken,
                "Pending Opportunity",
                Instant.parse("2026-08-20T09:00:00Z")
        );
        String approvedOpportunityId = createOpportunity(
                organizationToken,
                "Approved Opportunity For Report",
                Instant.parse("2026-08-21T09:00:00Z")
        );
        approve(adminToken, approvedOpportunityId);

        mockMvc.perform(post("/api/opportunities/{id}/reports", approvedOpportunityId)
                        .header("Authorization", "Bearer " + studentToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of(
                                "reason", "Spam",
                                "description", "Need review"
                        ))))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/dashboard/admin")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.pendingOpportunities").value(greaterThanOrEqualTo(1)))
                .andExpect(jsonPath("$.pendingReports").value(greaterThanOrEqualTo(1)))
                .andExpect(jsonPath("$.totalUsers").value(greaterThanOrEqualTo(3)))
                .andExpect(jsonPath("$.totalStudents").value(greaterThanOrEqualTo(1)))
                .andExpect(jsonPath("$.totalOrganizations").value(greaterThanOrEqualTo(1)))
                .andExpect(jsonPath("$.recentPendingOpportunities[*].id").value(hasItem(pendingOpportunityId)))
                .andExpect(jsonPath("$.recentReports[*].opportunityId").value(hasItem(approvedOpportunityId)));
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
                                "description", "Dashboard integration testing opportunity.",
                                "requirements", "Java, startup, teamwork.",
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
        if (role == UserRole.ADMIN) {
            return testAuthHelper.createAdminToken(email, "Dashboard Admin");
        }

        Map<String, String> body = new HashMap<>();
        body.put("email", email);
        body.put("password", "password123");
        body.put("fullName", role == UserRole.ADMIN ? "Dashboard Admin" : "Dashboard User");
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
