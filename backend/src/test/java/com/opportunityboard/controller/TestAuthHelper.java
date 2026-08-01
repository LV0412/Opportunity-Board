package com.opportunityboard.controller;

import com.opportunityboard.common.enums.UserRole;
import com.opportunityboard.common.enums.UserStatus;
import com.opportunityboard.entity.User;
import com.opportunityboard.repository.UserRepository;
import com.opportunityboard.security.CustomUserDetails;
import com.opportunityboard.security.JwtService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
class TestAuthHelper {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    TestAuthHelper(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            JwtService jwtService
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    String createAdminToken(String email, String fullName) {
        User user = userRepository.findByEmail(email).orElseGet(() -> {
            User nextUser = new User();
            nextUser.setEmail(email);
            nextUser.setPasswordHash(passwordEncoder.encode("password123"));
            nextUser.setFullName(fullName);
            nextUser.setRole(UserRole.ADMIN);
            nextUser.setStatus(UserStatus.ACTIVE);
            nextUser.setEmailVerifiedAt(java.time.Instant.now());
            return userRepository.save(nextUser);
        });

        return jwtService.generateToken(new CustomUserDetails(user));
    }
}
