package com.opportunityboard.service.auth.impl;

import com.opportunityboard.common.enums.UserRole;
import com.opportunityboard.common.enums.UserStatus;
import com.opportunityboard.dto.request.auth.LoginRequest;
import com.opportunityboard.dto.request.auth.RegisterRequest;
import com.opportunityboard.dto.response.auth.AuthResponse;
import com.opportunityboard.dto.response.auth.RegisterResponse;
import com.opportunityboard.dto.response.auth.UserResponse;
import com.opportunityboard.entity.OrganizationProfile;
import com.opportunityboard.entity.StudentProfile;
import com.opportunityboard.entity.User;
import com.opportunityboard.infrastructure.mail.MailService;
import com.opportunityboard.infrastructure.template.EmailTemplateService;
import com.opportunityboard.repository.OrganizationProfileRepository;
import com.opportunityboard.repository.StudentProfileRepository;
import com.opportunityboard.repository.UserRepository;
import com.opportunityboard.security.CustomUserDetails;
import com.opportunityboard.security.JwtService;
import com.opportunityboard.service.auth.AuthService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

@Service
public class AuthServiceImpl implements AuthService {
    private final UserRepository userRepository;
    private final StudentProfileRepository studentProfileRepository;
    private final OrganizationProfileRepository organizationProfileRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final MailService mailService;
    private final EmailTemplateService emailTemplateService;
    private final boolean emailVerificationRequired;

    public AuthServiceImpl(
            UserRepository userRepository,
            StudentProfileRepository studentProfileRepository,
            OrganizationProfileRepository organizationProfileRepository,
            PasswordEncoder passwordEncoder,
            AuthenticationManager authenticationManager,
            JwtService jwtService,
            MailService mailService,
            EmailTemplateService emailTemplateService,
            @Value("${app.auth.email-verification-required:true}") boolean emailVerificationRequired
    ) {
        this.userRepository = userRepository;
        this.studentProfileRepository = studentProfileRepository;
        this.organizationProfileRepository = organizationProfileRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.mailService = mailService;
        this.emailTemplateService = emailTemplateService;
        this.emailVerificationRequired = emailVerificationRequired;
    }

    @Override
    @Transactional
    public RegisterResponse register(RegisterRequest request) {
        if (request.role() == UserRole.ADMIN) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Admin accounts cannot be self-registered");
        }

        String normalizedEmail = request.email().trim().toLowerCase();
        if (userRepository.existsByEmail(normalizedEmail)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email already exists");
        }

        User user = new User();
        user.setEmail(normalizedEmail);
        user.setPasswordHash(passwordEncoder.encode(request.password()));
        user.setFullName(request.fullName().trim());
        user.setRole(request.role());
        if (emailVerificationRequired) {
            user.setStatus(UserStatus.PENDING_VERIFICATION);
            user.setEmailVerificationToken(generateVerificationToken());
            user.setEmailVerificationTokenExpiresAt(Instant.now().plus(24, ChronoUnit.HOURS));
        } else {
            user.setStatus(UserStatus.ACTIVE);
            user.setEmailVerifiedAt(Instant.now());
        }
        user = userRepository.save(user);

        createProfileForRole(user, request);

        if (emailVerificationRequired) {
            mailService.send(emailTemplateService.emailVerification(
                    user.getEmail(),
                    user.getFullName(),
                    user.getEmailVerificationToken()
            ));
            return new RegisterResponse(
                    null,
                    "Bearer",
                    0,
                    toUserResponse(user),
                    true,
                    "Registration successful. Please verify your email before logging in."
            );
        }

        AuthResponse authResponse = buildAuthResponse(new CustomUserDetails(user));
        return new RegisterResponse(
                authResponse.accessToken(),
                authResponse.tokenType(),
                authResponse.expiresIn(),
                authResponse.user(),
                false,
                "Registration successful."
        );
    }

    @Override
    public AuthResponse login(LoginRequest request) {
        String normalizedEmail = request.email().trim().toLowerCase();
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                    normalizedEmail,
                    request.password()
            ));
        } catch (BadCredentialsException exception) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid email or password");
        } catch (AuthenticationException exception) {
            userRepository.findByEmail(normalizedEmail).ifPresent(this::assertUserCanLogin);
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid email or password");
        }

        User user = userRepository.findByEmail(normalizedEmail)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid email or password"));
        assertUserCanLogin(user);
        return buildAuthResponse(new CustomUserDetails(user));
    }

    private void assertUserCanLogin(User user) {
        if (user.getStatus() == UserStatus.PENDING_VERIFICATION) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Please verify your email before logging in");
        }
        if (user.getStatus() != UserStatus.ACTIVE) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "User account is not active");
        }
    }

    @Override
    @Transactional
    public UserResponse verifyEmail(String token) {
        if (token == null || token.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Verification token is required");
        }

        User user = userRepository.findByEmailVerificationToken(token.trim())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid verification token"));
        if (user.getEmailVerificationTokenExpiresAt() == null
                || user.getEmailVerificationTokenExpiresAt().isBefore(Instant.now())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Verification token has expired");
        }

        user.setStatus(UserStatus.ACTIVE);
        user.setEmailVerifiedAt(Instant.now());
        user.setEmailVerificationToken(null);
        user.setEmailVerificationTokenExpiresAt(null);
        return toUserResponse(userRepository.save(user));
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

    private String generateVerificationToken() {
        return UUID.randomUUID().toString().replace("-", "") + UUID.randomUUID().toString().replace("-", "");
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
