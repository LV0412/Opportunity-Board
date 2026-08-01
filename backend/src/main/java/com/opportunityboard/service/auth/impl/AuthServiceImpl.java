package com.opportunityboard.service.auth.impl;

import com.opportunityboard.common.enums.UserRole;
import com.opportunityboard.common.enums.UserStatus;
import com.opportunityboard.dto.request.auth.LoginRequest;
import com.opportunityboard.dto.request.auth.RegisterRequest;
import com.opportunityboard.dto.response.auth.AuthResponse;
import com.opportunityboard.dto.response.auth.UserResponse;
import com.opportunityboard.entity.OrganizationProfile;
import com.opportunityboard.entity.StudentProfile;
import com.opportunityboard.entity.User;
import com.opportunityboard.repository.OrganizationProfileRepository;
import com.opportunityboard.repository.StudentProfileRepository;
import com.opportunityboard.repository.UserRepository;
import com.opportunityboard.security.CustomUserDetails;
import com.opportunityboard.security.JwtService;
import com.opportunityboard.service.auth.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class AuthServiceImpl implements AuthService {
    private final UserRepository userRepository;
    private final StudentProfileRepository studentProfileRepository;
    private final OrganizationProfileRepository organizationProfileRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public AuthServiceImpl(
            UserRepository userRepository,
            StudentProfileRepository studentProfileRepository,
            OrganizationProfileRepository organizationProfileRepository,
            PasswordEncoder passwordEncoder,
            AuthenticationManager authenticationManager,
            JwtService jwtService
    ) {
        this.userRepository = userRepository;
        this.studentProfileRepository = studentProfileRepository;
        this.organizationProfileRepository = organizationProfileRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }

    @Override
    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email already exists");
        }

        User user = new User();
        user.setEmail(request.email().trim().toLowerCase());
        user.setPasswordHash(passwordEncoder.encode(request.password()));
        user.setFullName(request.fullName().trim());
        user.setRole(request.role());
        user.setStatus(UserStatus.ACTIVE);
        user = userRepository.save(user);

        createProfileForRole(user, request);

        return buildAuthResponse(new CustomUserDetails(user));
    }

    @Override
    public AuthResponse login(LoginRequest request) {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                    request.email().trim().toLowerCase(),
                    request.password()
            ));
        } catch (BadCredentialsException exception) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid email or password");
        }

        User user = userRepository.findByEmail(request.email().trim().toLowerCase())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid email or password"));
        if (user.getStatus() != UserStatus.ACTIVE) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "User account is not active");
        }

        return buildAuthResponse(new CustomUserDetails(user));
    }

    @Override
    public AuthResponse refreshToken(CustomUserDetails currentUser) {
        return buildAuthResponse(currentUser);
    }

    @Override
    public UserResponse getCurrentUser(CustomUserDetails currentUser) {
        return userRepository.findById(currentUser.getId())
                .map(this::toUserResponse)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found"));
    }

    private void createProfileForRole(User user, RegisterRequest request) {
        if (user.getRole() == UserRole.STUDENT) {
            StudentProfile profile = new StudentProfile();
            profile.setUser(user);
            profile.setUniversity(request.university());
            profile.setMajor(request.major());
            studentProfileRepository.save(profile);
            return;
        }

        if (user.getRole() == UserRole.ORGANIZATION) {
            OrganizationProfile profile = new OrganizationProfile();
            profile.setUser(user);
            profile.setOrganizationName(resolveOrganizationName(request));
            organizationProfileRepository.save(profile);
        }
    }

    private String resolveOrganizationName(RegisterRequest request) {
        if (request.organizationName() != null && !request.organizationName().isBlank()) {
            return request.organizationName().trim();
        }
        return request.fullName().trim();
    }

    private AuthResponse buildAuthResponse(CustomUserDetails userDetails) {
        UserResponse user = userRepository.findById(userDetails.getId())
                .map(this::toUserResponse)
                .orElseGet(() -> new UserResponse(
                        userDetails.getId(),
                        userDetails.getUsername(),
                        userDetails.getFullName(),
                        extractRole(userDetails),
                        userDetails.getStatus()
                ));
        return new AuthResponse(
                jwtService.generateToken(userDetails),
                "Bearer",
                jwtService.getExpirationMs() / 1000,
                user
        );
    }

    private UserResponse toUserResponse(User user) {
        return new UserResponse(
                user.getId(),
                user.getEmail(),
                user.getFullName(),
                user.getRole(),
                user.getStatus()
        );
    }

    private UserRole extractRole(CustomUserDetails userDetails) {
        String authority = userDetails.getAuthorities().stream()
                .findFirst()
                .map(item -> item.getAuthority().replace("ROLE_", ""))
                .orElse(UserRole.STUDENT.name());
        return UserRole.valueOf(authority);
    }
}
