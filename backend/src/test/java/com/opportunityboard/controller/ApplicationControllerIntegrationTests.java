package com.opportunityboard.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.opportunityboard.common.enums.UserRole;
import com.opportunityboard.infrastructure.storage.StorageService;
import com.opportunityboard.repository.NotificationRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.mockito.Mockito.when;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class ApplicationControllerIntegrationTests {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private TestAuthHelper testAuthHelper;

    @MockBean
    private StorageService storageService;

    @Test
    void studentAppliesWithPdfResumeAndCoverLetter() throws Exception {
        String organizationToken = registerAndGetToken("multipart-org@example.com", UserRole.ORGANIZATION);
        String adminToken = registerAndGetToken("multipart-admin@example.com", UserRole.ADMIN);
        String studentToken = registerAndGetToken("multipart-student@example.com", UserRole.STUDENT);
        String opportunityId = createOpportunity(organizationToken, "Java Backend Internship", Instant.now().plusSeconds(604800));
        approve(adminToken, opportunityId);
        when(storageService.uploadResume(org.mockito.ArgumentMatchers.any()))
                .thenReturn("https://cdn.example.com/resume.pdf");

        MockMultipartFile resume = new MockMultipartFile(
                "resume",
                "student-cv.pdf",
                MediaType.APPLICATION_PDF_VALUE,
                "%PDF-1.7 test document".getBytes(java.nio.charset.StandardCharsets.UTF_8)
        );

        mockMvc.perform(multipart("/api/opportunities/{id}/apply", opportunityId)
                        .file(resume)
                        .param("coverLetter", "Tôi mong muốn được đồng hành cùng tổ chức.")
                        .header("Authorization", "Bearer " + studentToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("APPLIED"))
                .andExpect(jsonPath("$.coverLetter").value("Tôi mong muốn được đồng hành cùng tổ chức."))
                .andExpect(jsonPath("$.resumeFileName").value("student-cv.pdf"))
                .andExpect(jsonPath("$.resumeFileUrl").value("https://cdn.example.com/resume.pdf"));
    }

    @Test
    void multipartApplicationRejectsNonPdfResume() throws Exception {
        String organizationToken = registerAndGetToken("invalid-resume-org@example.com", UserRole.ORGANIZATION);
        String adminToken = registerAndGetToken("invalid-resume-admin@example.com", UserRole.ADMIN);
        String studentToken = registerAndGetToken("invalid-resume-student@example.com", UserRole.STUDENT);
        String opportunityId = createOpportunity(organizationToken, "Invalid Resume Internship", Instant.now().plusSeconds(604800));
        approve(adminToken, opportunityId);

        MockMultipartFile resume = new MockMultipartFile(
                "resume",
                "resume.txt",
                MediaType.TEXT_PLAIN_VALUE,
                "not a pdf".getBytes(java.nio.charset.StandardCharsets.UTF_8)
        );

        mockMvc.perform(multipart("/api/opportunities/{id}/apply", opportunityId)
                        .file(resume)
                        .param("coverLetter", "Message")
                        .header("Authorization", "Bearer " + studentToken))
                .andExpect(status().isBadRequest());
    }

    @Test
    void studentAppliesOnceAndTracksStatusUpdateNotification() throws Exception {
        String organizationToken = registerAndGetToken("apply-org@example.com", UserRole.ORGANIZATION);
        String adminToken = registerAndGetToken("apply-admin@example.com", UserRole.ADMIN);
        StudentAuth student = registerStudent("apply-student@example.com");
        String opportunityId = createOpportunity(organizationToken, "Frontend Internship", Instant.now().plusSeconds(604800));
        approve(adminToken, opportunityId);

        String applicationId = apply(student.token(), opportunityId);

        mockMvc.perform(post("/api/opportunities/{id}/apply", opportunityId)
                        .header("Authorization", "Bearer " + student.token())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of("coverLetter", "Applying again."))))
                .andExpect(status().isConflict());

        mockMvc.perform(get("/api/applications/me")
                        .header("Authorization", "Bearer " + student.token()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(applicationId))
                .andExpect(jsonPath("$.content[0].status").value("APPLIED"));

        mockMvc.perform(patch("/api/applications/{id}/status", applicationId)
                        .header("Authorization", "Bearer " + organizationToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of("status", "REVIEWING"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("REVIEWING"));

        mockMvc.perform(get("/api/applications/{id}", applicationId)
                        .header("Authorization", "Bearer " + student.token()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("REVIEWING"));

        assertThat(notificationRepository.countByUserIdAndReadAtIsNull(student.userId())).isEqualTo(1);

        String notificationResponse = mockMvc.perform(get("/api/notifications/me")
                        .header("Authorization", "Bearer " + student.token()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].type").value("APPLICATION_STATUS"))
                .andReturn()
                .getResponse()
                .getContentAsString();

        String notificationId = objectMapper.readTree(notificationResponse).get("content").get(0).get("id").asText();

        mockMvc.perform(patch("/api/notifications/{id}/read", notificationId)
                        .header("Authorization", "Bearer " + student.token()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.read").value(true));
    }

    @Test
    void studentCannotApplyExpiredOpportunity() throws Exception {
        String organizationToken = registerAndGetToken("expired-apply-org@example.com", UserRole.ORGANIZATION);
        String adminToken = registerAndGetToken("expired-apply-admin@example.com", UserRole.ADMIN);
        String studentToken = registerAndGetToken("expired-apply-student@example.com", UserRole.STUDENT);
        String opportunityId = createOpportunity(organizationToken, "Expired Internship", Instant.now().minusSeconds(3600));
        approve(adminToken, opportunityId);

        mockMvc.perform(post("/api/opportunities/{id}/apply", opportunityId)
                        .header("Authorization", "Bearer " + studentToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of("coverLetter", "Too late."))))
                .andExpect(status().isNotFound());
    }

    @Test
    void organizationCannotAccessAnotherOrganizationsApplication() throws Exception {
        String ownerToken = registerAndGetToken("application-owner-org@example.com", UserRole.ORGANIZATION);
        String otherToken = registerAndGetToken("application-other-org@example.com", UserRole.ORGANIZATION);
        String adminToken = registerAndGetToken("application-owner-admin@example.com", UserRole.ADMIN);
        String studentToken = registerAndGetToken("application-owner-student@example.com", UserRole.STUDENT);
        String opportunityId = createOpportunity(ownerToken, "Owner Internship", Instant.now().plusSeconds(604800));
        approve(adminToken, opportunityId);
        String applicationId = apply(studentToken, opportunityId);

        mockMvc.perform(get("/api/applications/{id}", applicationId)
                        .header("Authorization", "Bearer " + otherToken))
                .andExpect(status().isForbidden());

        mockMvc.perform(patch("/api/applications/{id}/status", applicationId)
                        .header("Authorization", "Bearer " + otherToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of("status", "ACCEPTED"))))
                .andExpect(status().isForbidden());
    }

    @Test
    void organizationListsOnlyOwnApplications() throws Exception {
        String ownerToken = registerAndGetToken("list-owner-org@example.com", UserRole.ORGANIZATION);
        String otherToken = registerAndGetToken("list-other-org@example.com", UserRole.ORGANIZATION);
        String adminToken = registerAndGetToken("list-admin@example.com", UserRole.ADMIN);
        String studentToken = registerAndGetToken("list-student@example.com", UserRole.STUDENT);
        String ownerOpportunityId = createOpportunity(ownerToken, "Owner Opportunity", Instant.now().plusSeconds(604800));
        String otherOpportunityId = createOpportunity(otherToken, "Other Opportunity", Instant.now().plusSeconds(604800));
        approve(adminToken, ownerOpportunityId);
        approve(adminToken, otherOpportunityId);
        String ownerApplicationId = apply(studentToken, ownerOpportunityId);
        apply(studentToken, otherOpportunityId);

        mockMvc.perform(get("/api/organizations/me/applications")
                        .header("Authorization", "Bearer " + ownerToken)
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(1))
                .andExpect(jsonPath("$.content[0].id").value(ownerApplicationId));
    }

    private String apply(String studentToken, String opportunityId) throws Exception {
        String response = mockMvc.perform(post("/api/opportunities/{id}/apply", opportunityId)
                        .header("Authorization", "Bearer " + studentToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of("coverLetter", "I would like to apply."))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("APPLIED"))
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
                                "description", "A practical opportunity for student applications.",
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

    private StudentAuth registerStudent(String email) throws Exception {
        String response = register(email, UserRole.STUDENT);
        JsonNode json = objectMapper.readTree(response);
        return new StudentAuth(json.get("accessToken").asText(), java.util.UUID.fromString(json.get("user").get("id").asText()));
    }

    private String registerAndGetToken(String email, UserRole role) throws Exception {
        if (role == UserRole.ADMIN) {
            return testAuthHelper.createAdminToken(email, "Application Admin");
        }

        return objectMapper.readTree(register(email, role)).get("accessToken").asText();
    }

    private String register(String email, UserRole role) throws Exception {
        Map<String, String> body = new HashMap<>();
        body.put("email", email);
        body.put("password", "password123");
        body.put("fullName", role == UserRole.ADMIN ? "Application Admin" : "Application User");
        body.put("role", role.name());
        if (role == UserRole.ORGANIZATION) {
            body.put("organizationName", email.substring(0, email.indexOf("@")));
        }

        return mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
    }

    private record StudentAuth(String token, java.util.UUID userId) {
    }
}
