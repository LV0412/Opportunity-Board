package com.opportunityboard.dto.response.auth;

public record RegisterResponse(
        String accessToken,
        String tokenType,
        long expiresIn,
        UserResponse user,
        boolean verificationRequired,
        String message
) {
}
