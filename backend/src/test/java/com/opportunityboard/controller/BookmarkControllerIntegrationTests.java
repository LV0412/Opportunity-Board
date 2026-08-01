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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class BookmarkControllerIntegrationTests {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private TestAuthHelper testAuthHelper;

    @Test
    void studentCanSaveOnceAndUnsaveOpportunity() throws Exception {
        String organizationToken = registerAndGetToken("bookmark-org@example.com", UserRole.ORGANIZATION);
        String adminToken = registerAndGetToken("bookmark-admin@example.com", UserRole.ADMIN);
        String studentToken = registerAndGetToken("bookmark-student@example.com", UserRole.STUDENT);
        String opportunityId = createOpportunity(organizationToken, "Saved Backend Internship", Instant.now().plusSeconds(604800));
        approve(adminToken, opportunityId);

        mockMvc.perform(post("/api/opportunities/{id}/bookmark", opportunityId)
                        .header("Authorization", "Bearer " + studentToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.bookmarkCount").value(1));

        mockMvc.perform(post("/api/opportunities/{id}/bookmark", opportunityId)
                        .header("Authorization", "Bearer " + studentToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.bookmarkCount").value(1));

        mockMvc.perform(get("/api/bookmarks/me")
                        .header("Authorization", "Bearer " + studentToken)
                        .param("sort", "deadline"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(1))
                .andExpect(jsonPath("$.content[0].opportunity.id").value(opportunityId));

        mockMvc.perform(delete("/api/opportunities/{id}/bookmark", opportunityId)
                        .header("Authorization", "Bearer " + studentToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.bookmarkCount").value(0));
    }

    @Test
    void savedListCanSortByNearestDeadline() throws Exception {
        String organizationToken = registerAndGetToken("deadline-bookmark-org@example.com", UserRole.ORGANIZATION);
        String adminToken = registerAndGetToken("deadline-bookmark-admin@example.com", UserRole.ADMIN);
        String studentToken = registerAndGetToken("deadline-bookmark-student@example.com", UserRole.STUDENT);

        String laterId = createOpportunity(organizationToken, "Later Scholarship", Instant.now().plusSeconds(604800));
        String soonerId = createOpportunity(organizationToken, "Sooner Hackathon", Instant.now().plusSeconds(86400));
        approve(adminToken, laterId);
        approve(adminToken, soonerId);

        mockMvc.perform(post("/api/opportunities/{id}/bookmark", laterId)
                        .header("Authorization", "Bearer " + studentToken))
                .andExpect(status().isOk());
        mockMvc.perform(post("/api/opportunities/{id}/bookmark", soonerId)
                        .header("Authorization", "Bearer " + studentToken))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/bookmarks/me")
                        .header("Authorization", "Bearer " + studentToken)
                        .param("sort", "deadline")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].opportunity.id").value(soonerId))
                .andExpect(jsonPath("$.content[1].opportunity.id").value(laterId));
    }

    @Test
    void organizationCannotBookmarkOpportunity() throws Exception {
        String organizationToken = registerAndGetToken("role-bookmark-org@example.com", UserRole.ORGANIZATION);
        String adminToken = registerAndGetToken("role-bookmark-admin@example.com", UserRole.ADMIN);
        String opportunityId = createOpportunity(organizationToken, "Role Protected Internship", Instant.now().plusSeconds(604800));
        approve(adminToken, opportunityId);

        mockMvc.perform(post("/api/opportunities/{id}/bookmark", opportunityId)
                        .header("Authorization", "Bearer " + organizationToken))
                .andExpect(status().isForbidden());
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
                                "description", "A bookmarkable opportunity for students.",
                                "requirements", "Basic programming knowledge.",
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
        if (role == UserRole.ADMIN) {
            return testAuthHelper.createAdminToken(email, "Bookmark Admin");
        }

        Map<String, String> body = new HashMap<>();
        body.put("email", email);
        body.put("password", "password123");
        body.put("fullName", role == UserRole.ADMIN ? "Bookmark Admin" : "Bookmark User");
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
