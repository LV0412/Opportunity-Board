package com.opportunityboard.controller;

import com.opportunityboard.dto.request.auth.LoginRequest;
import com.opportunityboard.dto.request.auth.RegisterRequest;
import com.opportunityboard.dto.response.auth.AuthResponse;
import com.opportunityboard.dto.response.auth.RegisterResponse;
import com.opportunityboard.dto.response.auth.UserResponse;
import com.opportunityboard.security.CustomUserDetails;
import com.opportunityboard.service.auth.AuthService;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public RegisterResponse register(@Valid @RequestBody RegisterRequest request) {
        return authService.register(request);
    }

    @GetMapping("/verify-email")
    public UserResponse verifyEmail(@RequestParam String token) {
        return authService.verifyEmail(token);
    }

    @PostMapping("/login")
    public AuthResponse login(@Valid @RequestBody LoginRequest request) {
        return authService.login(request);
    }

    @GetMapping("/me")
    public UserResponse me(@AuthenticationPrincipal CustomUserDetails currentUser) {
        return authService.getCurrentUser(currentUser);
    }

    @PostMapping("/refresh-token")
    public AuthResponse refreshToken(@AuthenticationPrincipal CustomUserDetails currentUser) {
        return authService.refreshToken(currentUser);
    }
}
