package com.opportunityboard.service.auth;

import com.opportunityboard.dto.request.auth.LoginRequest;
import com.opportunityboard.dto.request.auth.RegisterRequest;
import com.opportunityboard.dto.response.auth.AuthResponse;
import com.opportunityboard.dto.response.auth.RegisterResponse;
import com.opportunityboard.dto.response.auth.UserResponse;
import com.opportunityboard.security.CustomUserDetails;

public interface AuthService {
    RegisterResponse register(RegisterRequest request);

    AuthResponse login(LoginRequest request);

    UserResponse verifyEmail(String token);

    AuthResponse refreshToken(CustomUserDetails currentUser);

    UserResponse getCurrentUser(CustomUserDetails currentUser);
}
