package com.opportunityboard.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.opportunityboard.common.enums.UserRole;
import com.opportunityboard.infrastructure.storage.StorageService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class ProfileControllerIntegrationTests {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private StorageService storageService;

    @Test
    void studentCanUpdateProfileAndUploadPdfResume() throws Exception {
        String token = registerAndGetToken("profile-student@example.com", UserRole.STUDENT);

        mockMvc.perform(patch("/api/students/me")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of(
                                "university", "FPT University",
                                "major", "Software Engineering",
                                "graduationYear", 2027,
                                "location", "Da Nang",
                                "bio", "Backend-focused student",
                                "interests", "Startups, hackathons",
                                "skills", java.util.List.of("Java", "Spring Boot", "React")
                        ))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.university").value("FPT University"))
                .andExpect(jsonPath("$.skills[0]").exists());

        when(storageService.uploadResume(any())).thenReturn("https://res.cloudinary.com/demo/resume.pdf");
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "resume.pdf",
                MediaType.APPLICATION_PDF_VALUE,
                "fake pdf".getBytes()
        );

        mockMvc.perform(multipart("/api/students/me/resume")
                        .file(file)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.fileName").value("resume.pdf"))
                .andExpect(jsonPath("$.fileUrl").value("https://res.cloudinary.com/demo/resume.pdf"))
                .andExpect(jsonPath("$.primaryResume").value(true));
    }

    @Test
    void organizationCanUpdateProfileAndUploadLogo() throws Exception {
        String token = registerAndGetToken("profile-org@example.com", UserRole.ORGANIZATION);

        mockMvc.perform(patch("/api/organizations/me")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of(
                                "organizationName", "Opportunity Labs",
                                "industry", "Education Technology",
                                "websiteUrl", "https://opportunity-labs.example",
                                "description", "We publish student opportunities."
                        ))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.organizationName").value("Opportunity Labs"))
                .andExpect(jsonPath("$.websiteUrl").value("https://opportunity-labs.example"));

        when(storageService.uploadLogo(any())).thenReturn("https://res.cloudinary.com/demo/logo.png");
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "logo.png",
                MediaType.IMAGE_PNG_VALUE,
                "fake image".getBytes()
        );

        mockMvc.perform(multipart("/api/organizations/me/logo")
                        .file(file)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.logoUrl").value("https://res.cloudinary.com/demo/logo.png"));
    }

    @Test
    void uploadRejectsInvalidFilesAndWrongRoles() throws Exception {
        String studentToken = registerAndGetToken("invalid-file-student@example.com", UserRole.STUDENT);
        String organizationToken = registerAndGetToken("wrong-role-org@example.com", UserRole.ORGANIZATION);

        MockMultipartFile textFile = new MockMultipartFile(
                "file",
                "resume.txt",
                MediaType.TEXT_PLAIN_VALUE,
                "not a pdf".getBytes()
        );

        mockMvc.perform(multipart("/api/students/me/resume")
                        .file(textFile)
                        .header("Authorization", "Bearer " + studentToken))
                .andExpect(status().isBadRequest());

        mockMvc.perform(get("/api/students/me")
                        .header("Authorization", "Bearer " + organizationToken))
                .andExpect(status().isForbidden());
    }

    private String registerAndGetToken(String email, UserRole role) throws Exception {
        Map<String, String> body = new java.util.HashMap<>();
        body.put("email", email);
        body.put("password", "password123");
        body.put("fullName", role == UserRole.STUDENT ? "Profile Student" : "Profile Org Owner");
        body.put("role", role.name());
        if (role == UserRole.ORGANIZATION) {
            body.put("organizationName", "Profile Org");
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
