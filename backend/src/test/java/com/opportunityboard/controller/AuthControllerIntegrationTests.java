package com.opportunityboard.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.opportunityboard.common.enums.UserRole;
import com.opportunityboard.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AuthControllerIntegrationTests {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    void studentCanRegisterLoginAndReadCurrentUser() throws Exception {
        String password = "password123";
        String email = "phase3-student@example.com";

        String registerBody = objectMapper.writeValueAsString(Map.of(
                "email", email,
                "password", password,
                "fullName", "Phase Three Student",
                "role", UserRole.STUDENT.name(),
                "university", "FPT University",
                "major", "Software Engineering"
        ));

        String registerResponse = mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(registerBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").isNotEmpty())
                .andExpect(jsonPath("$.tokenType").value("Bearer"))
                .andExpect(jsonPath("$.user.email").value(email))
                .andExpect(jsonPath("$.user.role").value("STUDENT"))
                .andReturn()
                .getResponse()
                .getContentAsString();

        String token = objectMapper.readTree(registerResponse).get("accessToken").asText();

        assertThat(userRepository.findByEmail(email))
                .hasValueSatisfying(user -> assertThat(passwordEncoder.matches(password, user.getPasswordHash())).isTrue());

        mockMvc.perform(get("/api/auth/me")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value(email))
                .andExpect(jsonPath("$.role").value("STUDENT"));

        String loginBody = objectMapper.writeValueAsString(Map.of(
                "email", email,
                "password", password
        ));

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").isNotEmpty());
    }

    @Test
    void roleGuardsRejectLowerPrivilegeTokens() throws Exception {
        String studentToken = registerAndGetToken(
                "guard-student@example.com",
                "Guard Student",
                UserRole.STUDENT,
                null
        );
        String organizationToken = registerAndGetToken(
                "guard-org@example.com",
                "Guard Org Owner",
                UserRole.ORGANIZATION,
                "Guard Org"
        );

        mockMvc.perform(get("/api/admin/ping")
                        .header("Authorization", "Bearer " + studentToken))
                .andExpect(status().isForbidden());

        mockMvc.perform(get("/api/admin/ping")
                        .header("Authorization", "Bearer " + organizationToken))
                .andExpect(status().isForbidden());

        mockMvc.perform(get("/api/organizations/ping")
                        .header("Authorization", "Bearer " + studentToken))
                .andExpect(status().isForbidden());
    }

    private String registerAndGetToken(
            String email,
            String fullName,
            UserRole role,
            String organizationName
    ) throws Exception {
        Map<String, String> body = new java.util.HashMap<>();
        body.put("email", email);
        body.put("password", "password123");
        body.put("fullName", fullName);
        body.put("role", role.name());
        if (organizationName != null) {
            body.put("organizationName", organizationName);
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
