package com.opportunityboard.service.auth;

import com.opportunityboard.common.enums.UserRole;
import com.opportunityboard.common.enums.UserStatus;
import com.opportunityboard.dto.request.auth.LoginRequest;
import com.opportunityboard.dto.request.auth.RegisterRequest;
import com.opportunityboard.dto.response.auth.RegisterResponse;
import com.opportunityboard.entity.OrganizationProfile;
import com.opportunityboard.entity.StudentProfile;
import com.opportunityboard.entity.User;
import com.opportunityboard.infrastructure.mail.MailService;
import com.opportunityboard.infrastructure.mail.MailMessage;
import com.opportunityboard.infrastructure.template.EmailTemplateService;
import com.opportunityboard.repository.OrganizationProfileRepository;
import com.opportunityboard.repository.StudentProfileRepository;
import com.opportunityboard.repository.UserRepository;
import com.opportunityboard.security.CustomUserDetails;
import com.opportunityboard.security.JwtService;
import com.opportunityboard.service.auth.impl.AuthServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {
    @Mock
    private UserRepository userRepository;

    @Mock
    private StudentProfileRepository studentProfileRepository;

    @Mock
    private OrganizationProfileRepository organizationProfileRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtService jwtService;

    @Mock
    private MailService mailService;

    @Mock
    private EmailTemplateService emailTemplateService;

    private AuthServiceImpl authService;

    private UUID userId;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        authService = new AuthServiceImpl(
                userRepository,
                studentProfileRepository,
                organizationProfileRepository,
                passwordEncoder,
                authenticationManager,
                jwtService,
                mailService,
                emailTemplateService,
                false
        );
    }

    @Test
    void registerStudentCreatesProfileAndReturnsAuthResponse() {
        RegisterRequest request = new RegisterRequest(
                " Student@Example.com ",
                "password123",
                " Student Name ",
                UserRole.STUDENT,
                null,
                "FPT University",
                "Software Engineering"
        );
        when(userRepository.existsByEmail("student@example.com")).thenReturn(false);
        when(passwordEncoder.encode("password123")).thenReturn("encoded-password");
        when(jwtService.generateToken(any(CustomUserDetails.class))).thenReturn("jwt-token");
        when(jwtService.getExpirationMs()).thenReturn(3600_000L);
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User user = invocation.getArgument(0);
            user.setId(userId);
            return user;
        });
        when(userRepository.findById(userId)).thenAnswer(invocation -> {
            User user = new User();
            user.setId(userId);
            user.setEmail("student@example.com");
            user.setFullName("Student Name");
            user.setRole(UserRole.STUDENT);
            user.setStatus(UserStatus.ACTIVE);
            return Optional.of(user);
        });

        RegisterResponse response = authService.register(request);

        ArgumentCaptor<StudentProfile> profileCaptor = ArgumentCaptor.forClass(StudentProfile.class);
        verify(studentProfileRepository).save(profileCaptor.capture());
        StudentProfile profile = profileCaptor.getValue();
        assertThat(profile.getUniversity()).isEqualTo("FPT University");
        assertThat(profile.getMajor()).isEqualTo("Software Engineering");
        verify(organizationProfileRepository, never()).save(any(OrganizationProfile.class));

        assertThat(response.accessToken()).isEqualTo("jwt-token");
        assertThat(response.verificationRequired()).isFalse();
        assertThat(response.expiresIn()).isEqualTo(3600);
        assertThat(response.user().email()).isEqualTo("student@example.com");
        assertThat(response.user().role()).isEqualTo(UserRole.STUDENT);
    }

    @Test
    void registerRejectsAdminSelfRegistration() {
        RegisterRequest request = new RegisterRequest(
                "admin@example.com",
                "password123",
                "Admin User",
                UserRole.ADMIN,
                null,
                null,
                null
        );

        assertThatThrownBy(() -> authService.register(request))
                .isInstanceOf(ResponseStatusException.class)
                .satisfies(exception -> {
                    ResponseStatusException responseStatusException = (ResponseStatusException) exception;
                    assertThat(responseStatusException.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
                    assertThat(responseStatusException.getReason()).isEqualTo("Admin accounts cannot be self-registered");
                });
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void registerWithEmailVerificationSavesPendingUserAndSendsVerificationMail() {
        authService = new AuthServiceImpl(
                userRepository,
                studentProfileRepository,
                organizationProfileRepository,
                passwordEncoder,
                authenticationManager,
                jwtService,
                mailService,
                emailTemplateService,
                true
        );
        RegisterRequest request = new RegisterRequest(
                "student@example.com",
                "password123",
                "Student Name",
                UserRole.STUDENT,
                null,
                "FPT University",
                null
        );
        MailMessage message = new MailMessage(
                "student@example.com",
                "Verify email",
                "<p>Verify</p>",
                "Verify"
        );
        when(userRepository.existsByEmail("student@example.com")).thenReturn(false);
        when(passwordEncoder.encode("password123")).thenReturn("encoded-password");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User user = invocation.getArgument(0);
            user.setId(userId);
            return user;
        });
        when(emailTemplateService.emailVerification(any(), any(), any())).thenReturn(message);

        RegisterResponse response = authService.register(request);

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());
        User savedUser = userCaptor.getValue();
        assertThat(savedUser.getStatus()).isEqualTo(UserStatus.PENDING_VERIFICATION);
        assertThat(savedUser.getEmailVerificationToken()).isNotBlank();
        assertThat(savedUser.getEmailVerificationTokenExpiresAt()).isNotNull();
        verify(emailTemplateService).emailVerification(
                "student@example.com",
                "Student Name",
                savedUser.getEmailVerificationToken()
        );
        verify(mailService).send(message);
        assertThat(response.accessToken()).isNull();
        assertThat(response.verificationRequired()).isTrue();
    }

    @Test
    void loginRejectsInactiveUserAfterAuthentication() {
        User user = new User();
        user.setId(userId);
        user.setEmail("inactive@example.com");
        user.setFullName("Inactive User");
        user.setRole(UserRole.STUDENT);
        user.setStatus(UserStatus.LOCKED);
        when(userRepository.findByEmail("inactive@example.com")).thenReturn(Optional.of(user));

        assertThatThrownBy(() -> authService.login(new LoginRequest("inactive@example.com", "password123")))
                .isInstanceOf(ResponseStatusException.class)
                .satisfies(exception -> {
                    ResponseStatusException responseStatusException = (ResponseStatusException) exception;
                    assertThat(responseStatusException.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
                    assertThat(responseStatusException.getReason()).isEqualTo("User account is not active");
                });
    }

    @Test
    void loginRejectsPendingVerificationUserWhenSecurityProviderDisablesAccount() {
        User user = new User();
        user.setId(userId);
        user.setEmail("student@example.com");
        user.setFullName("Student User");
        user.setRole(UserRole.STUDENT);
        user.setStatus(UserStatus.PENDING_VERIFICATION);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new DisabledException("User is disabled"));
        when(userRepository.findByEmail("student@example.com")).thenReturn(Optional.of(user));

        assertThatThrownBy(() -> authService.login(new LoginRequest("student@example.com", "password123")))
                .isInstanceOf(ResponseStatusException.class)
                .satisfies(exception -> {
                    ResponseStatusException responseStatusException = (ResponseStatusException) exception;
                    assertThat(responseStatusException.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
                    assertThat(responseStatusException.getReason()).isEqualTo("Please verify your email before logging in");
                });
    }

    @Test
    void verifyEmailActivatesUserAndClearsToken() {
        User user = new User();
        user.setId(userId);
        user.setEmail("student@example.com");
        user.setFullName("Student User");
        user.setRole(UserRole.STUDENT);
        user.setStatus(UserStatus.PENDING_VERIFICATION);
        user.setEmailVerificationToken("valid-token");
        user.setEmailVerificationTokenExpiresAt(java.time.Instant.now().plusSeconds(3600));
        when(userRepository.findByEmailVerificationToken("valid-token")).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        var response = authService.verifyEmail("valid-token");

        assertThat(response.status()).isEqualTo(UserStatus.ACTIVE);
        assertThat(user.getEmailVerifiedAt()).isNotNull();
        assertThat(user.getEmailVerificationToken()).isNull();
        assertThat(user.getEmailVerificationTokenExpiresAt()).isNull();
    }

    @Test
    void loginTranslatesBadCredentialsToUnauthorized() {
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("Bad credentials"));

        assertThatThrownBy(() -> authService.login(new LoginRequest("student@example.com", "wrong-password")))
                .isInstanceOf(ResponseStatusException.class)
                .satisfies(exception -> {
                    ResponseStatusException responseStatusException = (ResponseStatusException) exception;
                    assertThat(responseStatusException.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
                    assertThat(responseStatusException.getReason()).isEqualTo("Invalid email or password");
                });
    }
}
