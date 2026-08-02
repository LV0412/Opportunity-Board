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

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.everyItem;
import static org.hamcrest.Matchers.not;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class OpportunitySearchIntegrationTests {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private TestAuthHelper testAuthHelper;

    @Test
    void searchesWithoutOptionalFilters() throws Exception {
        mockMvc.perform(get("/api/opportunities/search")
                        .param("sort", "newest")
                        .param("page", "0")
                        .param("size", "12"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray());
    }

    @Test
    void searchesByPopularityWithoutOptionalFilters() throws Exception {
        mockMvc.perform(get("/api/opportunities/search")
                        .param("sort", "popular")
                        .param("page", "0")
                        .param("size", "12"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray());
    }

    @Test
    void searchesApprovedOpportunitiesWithCombinedFilters() throws Exception {
        String organizationToken = registerAndGetToken("search-org@example.com", UserRole.ORGANIZATION);
        String adminToken = registerAndGetToken("search-admin@example.com", UserRole.ADMIN);

        String matchingId = createOpportunity(
                organizationToken,
                "React Frontend Internship",
                "Build product dashboards for students.",
                "React and TypeScript fundamentals.",
                "Da Nang",
                "internship",
                List.of("React", "Paid"),
                Instant.now().plusSeconds(604800)
        );
        approve(adminToken, matchingId);

        String otherId = createOpportunity(
                organizationToken,
                "Marketing Scholarship",
                "Scholarship for student communities.",
                "Writing portfolio.",
                "Ho Chi Minh City",
                "scholarship",
                List.of("International"),
                Instant.now().plusSeconds(604800)
        );
        approve(adminToken, otherId);

        createOpportunity(
                organizationToken,
                "React Pending Program",
                "This should not be public yet.",
                "React",
                "Da Nang",
                "internship",
                List.of("React"),
                Instant.now().plusSeconds(604800)
        );

        mockMvc.perform(get("/api/opportunities/search")
                        .param("query", "react")
                        .param("categorySlug", "internship")
                        .param("location", "Da Nang")
                        .param("skill", "React")
                        .param("sort", "deadline")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(1))
                .andExpect(jsonPath("$.content[0].id").value(matchingId))
                .andExpect(jsonPath("$.content[0].title").value("React Frontend Internship"))
                .andExpect(jsonPath("$.content[*].id", everyItem(not(otherId))));
    }

    @Test
    void searchDoesNotReturnExpiredOpportunities() throws Exception {
        String organizationToken = registerAndGetToken("expired-search-org@example.com", UserRole.ORGANIZATION);
        String adminToken = registerAndGetToken("expired-search-admin@example.com", UserRole.ADMIN);

        String expiredId = createOpportunity(
                organizationToken,
                "Expired Hackathon",
                "Past event.",
                "Java",
                "Remote",
                "hackathon",
                List.of("Remote"),
                Instant.now().minusSeconds(3600)
        );
        approve(adminToken, expiredId);

        mockMvc.perform(get("/api/opportunities/search")
                        .param("query", "Expired Hackathon")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(0));
    }

    @Test
    void searchMatchesOrganizationName() throws Exception {
        String organizationToken = registerAndGetToken("northstar-labs@example.com", UserRole.ORGANIZATION);
        String adminToken = registerAndGetToken("org-name-search-admin@example.com", UserRole.ADMIN);
        String opportunityId = createOpportunity(
                organizationToken,
                "Backend Fellowship",
                "Build APIs.",
                "Java",
                "Remote",
                "internship",
                List.of("Java"),
                Instant.now().plusSeconds(604800)
        );
        approve(adminToken, opportunityId);

        mockMvc.perform(get("/api/opportunities/search")
                        .param("query", "northstar")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].organizationName", containsString("northstar")));
    }

    private void approve(String adminToken, String opportunityId) throws Exception {
        mockMvc.perform(post("/api/admin/opportunities/{id}/approve", opportunityId)
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk());
    }

    private String createOpportunity(
            String token,
            String title,
            String description,
            String requirements,
            String location,
            String categorySlug,
            List<String> tags,
            Instant deadlineAt
    ) throws Exception {
        String response = mockMvc.perform(post("/api/opportunities")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of(
                                "title", title,
                                "description", description,
                                "requirements", requirements,
                                "location", location,
                                "remote", "Remote".equalsIgnoreCase(location),
                                "applyUrl", "https://example.com/apply",
                                "deadlineAt", deadlineAt.toString(),
                                "categorySlug", categorySlug,
                                "tags", tags
                        ))))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        return objectMapper.readTree(response).get("id").asText();
    }

    private String registerAndGetToken(String email, UserRole role) throws Exception {
        if (role == UserRole.ADMIN) {
            return testAuthHelper.createAdminToken(email, "Search Admin");
        }

        Map<String, String> body = new HashMap<>();
        body.put("email", email);
        body.put("password", "password123");
        body.put("fullName", role == UserRole.ADMIN ? "Search Admin" : "Search Org Owner");
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
