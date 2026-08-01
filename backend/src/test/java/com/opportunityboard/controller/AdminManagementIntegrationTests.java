package com.opportunityboard.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.opportunityboard.common.enums.UserRole;
import com.opportunityboard.repository.AdminAuditLogRepository;
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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AdminManagementIntegrationTests {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private AdminAuditLogRepository adminAuditLogRepository;

    @Test
    void studentCanReportAndAdminCanResolveReport() throws Exception {
        String organizationToken = registerAndGetToken("report-org@example.com", UserRole.ORGANIZATION);
        String adminToken = registerAndGetToken("report-admin@example.com", UserRole.ADMIN);
        String studentToken = registerAndGetToken("report-student@example.com", UserRole.STUDENT);
        String opportunityId = createOpportunity(organizationToken, "Reported Opportunity", Instant.now().plusSeconds(86400));
        approve(adminToken, opportunityId);

        String reportId = mockMvc.perform(post("/api/opportunities/{id}/reports", opportunityId)
                        .header("Authorization", "Bearer " + studentToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of(
                                "reason", "Spam content",
                                "description", "Thông tin không đáng tin."
                        ))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("PENDING"))
                .andReturn()
                .getResponse()
                .getContentAsString();

        String parsedReportId = objectMapper.readTree(reportId).get("id").asText();

        mockMvc.perform(get("/api/admin/reports")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(parsedReportId));

        mockMvc.perform(patch("/api/admin/reports/{id}/status", parsedReportId)
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of(
                                "status", "RESOLVED",
                                "note", "Checked and handled"
                        ))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("RESOLVED"));

        assertThat(adminAuditLogRepository.count()).isGreaterThan(0);
    }

    @Test
    void lockedUserCannotUseOldToken() throws Exception {
        String adminToken = registerAndGetToken("lock-admin@example.com", UserRole.ADMIN);
        String studentToken = registerAndGetToken("lock-student@example.com", UserRole.STUDENT);
        String studentId = currentUserId(studentToken);

        mockMvc.perform(patch("/api/admin/users/{id}/status", studentId)
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of("status", "LOCKED"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("LOCKED"));

        mockMvc.perform(get("/api/auth/me")
                        .header("Authorization", "Bearer " + studentToken))
                .andExpect(status().isForbidden());
    }

    @Test
    void adminCanManageCategoriesAndTags() throws Exception {
        String adminToken = registerAndGetToken("taxonomy-admin@example.com", UserRole.ADMIN);

        String categoryResponse = mockMvc.perform(post("/api/admin/categories")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of(
                                "name", "Fellowship",
                                "slug", "fellowship",
                                "description", "Fellowship programs"
                        ))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.slug").value("fellowship"))
                .andReturn()
                .getResponse()
                .getContentAsString();
        String categoryId = objectMapper.readTree(categoryResponse).get("id").asText();

        mockMvc.perform(patch("/api/admin/categories/{id}", categoryId)
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of(
                                "name", "Global Fellowship",
                                "slug", "global-fellowship",
                                "description", "Updated"
                        ))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.slug").value("global-fellowship"));

        String tagResponse = mockMvc.perform(post("/api/admin/tags")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of(
                                "name", "Onsite",
                                "slug", "onsite"
                        ))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.slug").value("onsite"))
                .andReturn()
                .getResponse()
                .getContentAsString();
        String tagId = objectMapper.readTree(tagResponse).get("id").asText();

        mockMvc.perform(get("/api/taxonomy/categories"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[*].slug").isArray());

        mockMvc.perform(get("/api/taxonomy/tags"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[*].slug").isArray());

        mockMvc.perform(patch("/api/admin/tags/{id}", tagId)
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of(
                                "name", "Onsite Only",
                                "slug", "onsite-only"
                        ))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.slug").value("onsite-only"));
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
                                "description", "Opportunity for admin management testing.",
                                "requirements", "Basic knowledge.",
                                "location", "Ho Chi Minh City",
                                "remote", false,
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
        body.put("fullName", role == UserRole.ADMIN ? "Admin User" : "Regular User");
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
