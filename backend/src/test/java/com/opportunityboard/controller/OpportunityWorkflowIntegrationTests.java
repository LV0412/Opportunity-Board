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
import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class OpportunityWorkflowIntegrationTests {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private TestAuthHelper testAuthHelper;

    @Test
    void organizationCreatesPendingOpportunityAndAdminApprovesForPublicView() throws Exception {
        String organizationToken = registerAndGetToken("workflow-org@example.com", UserRole.ORGANIZATION);
        String adminToken = registerAndGetToken("workflow-admin@example.com", UserRole.ADMIN);

        String opportunityId = createOpportunity(organizationToken, "Backend Internship");

        mockMvc.perform(get("/api/opportunities/{id}", opportunityId))
                .andExpect(status().isNotFound());

        mockMvc.perform(get("/api/admin/opportunities/pending")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].status").value("PENDING"));

        mockMvc.perform(post("/api/admin/opportunities/{id}/approve", opportunityId)
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("APPROVED"));

        mockMvc.perform(get("/api/opportunities/{id}", opportunityId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Backend Internship"))
                .andExpect(jsonPath("$.status").value("APPROVED"));
    }

    @Test
    void adminRejectsOpportunityWithReasonAndRejectedOpportunityIsNotPublic() throws Exception {
        String organizationToken = registerAndGetToken("reject-org@example.com", UserRole.ORGANIZATION);
        String adminToken = registerAndGetToken("reject-admin@example.com", UserRole.ADMIN);
        String opportunityId = createOpportunity(organizationToken, "Unclear Scholarship");

        mockMvc.perform(post("/api/admin/opportunities/{id}/reject", opportunityId)
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of("reason", "Missing eligibility details"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("REJECTED"))
                .andExpect(jsonPath("$.latestReviewNote").value("Missing eligibility details"));

        mockMvc.perform(get("/api/opportunities/{id}", opportunityId))
                .andExpect(status().isNotFound());
    }

    @Test
    void organizationCannotEditOrCloseAnotherOrganizationsOpportunity() throws Exception {
        String ownerToken = registerAndGetToken("owner-org@example.com", UserRole.ORGANIZATION);
        String otherToken = registerAndGetToken("other-org@example.com", UserRole.ORGANIZATION);
        String opportunityId = createOpportunity(ownerToken, "Owner Only Opportunity");

        mockMvc.perform(patch("/api/opportunities/{id}", opportunityId)
                        .header("Authorization", "Bearer " + otherToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(opportunityPayload("Hijacked title")))
                .andExpect(status().isForbidden());

        mockMvc.perform(delete("/api/opportunities/{id}", opportunityId)
                        .header("Authorization", "Bearer " + otherToken))
                .andExpect(status().isForbidden());
    }

    private String createOpportunity(String token, String title) throws Exception {
        String response = mockMvc.perform(post("/api/opportunities")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(opportunityPayload(title)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("PENDING"))
                .andReturn()
                .getResponse()
                .getContentAsString();

        JsonNode json = objectMapper.readTree(response);
        return json.get("id").asText();
    }

    private String opportunityPayload(String title) throws Exception {
        return objectMapper.writeValueAsString(Map.of(
                "title", title,
                "description", "A practical opportunity for students.",
                "requirements", "Basic programming knowledge.",
                "location", "Ho Chi Minh City",
                "remote", true,
                "applyUrl", "https://example.com/apply",
                "deadlineAt", Instant.now().plusSeconds(86400).toString(),
                "categorySlug", "internship",
                "tags", java.util.List.of("Remote", "Paid")
        ));
    }

    private String registerAndGetToken(String email, UserRole role) throws Exception {
        if (role == UserRole.ADMIN) {
            return testAuthHelper.createAdminToken(email, "Workflow Admin");
        }

        Map<String, String> body = new java.util.HashMap<>();
        body.put("email", email);
        body.put("password", "password123");
        body.put("fullName", role == UserRole.ADMIN ? "Workflow Admin" : "Workflow Org Owner");
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

        return objectMapper.readTree(response).get("accessToken").asText();
    }
}
