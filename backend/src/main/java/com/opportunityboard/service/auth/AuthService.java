package com.opportunityboard.service.auth;

import com.opportunityboard.dto.request.auth.LoginRequest;
import com.opportunityboard.dto.request.auth.RegisterRequest;
import com.opportunityboard.dto.response.auth.AuthResponse;
import com.opportunityboard.dto.response.auth.UserResponse;
import com.opportunityboard.security.CustomUserDetails;

public interface AuthService {
    AuthResponse register(RegisterRequest request);

    AuthResponse login(LoginRequest request);

    AuthResponse refreshToken(CustomUserDetails currentUser);

    UserResponse getCurrentUser(CustomUserDetails currentUser);
}
