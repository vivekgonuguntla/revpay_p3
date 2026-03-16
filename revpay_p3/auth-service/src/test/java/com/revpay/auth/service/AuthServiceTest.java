package com.revpay.auth.service;

import com.revpay.auth.dto.AuthRequest;
import com.revpay.auth.dto.AuthResponse;
import com.revpay.auth.dto.RegisterRequest;
import com.revpay.auth.dto.SecurityQuestionDto;
import com.revpay.auth.entity.Role;
import com.revpay.auth.entity.User;
import com.revpay.auth.exception.DuplicateEmailException;
import com.revpay.auth.exception.InvalidCredentialsException;
import com.revpay.auth.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @Mock
    private AuthenticationManager authenticationManager;

    @InjectMocks
    private AuthService authService;

    private RegisterRequest registerRequest;
    private User user;

    @BeforeEach
    void setUp() {
        registerRequest = RegisterRequest.builder()
                .fullName("Alice Doe")
                .username("alice")
                .email("alice@example.com")
                .password("secret")
                .phoneNumber("9999999999")
                .role(Role.BUSINESS)
                .securityQuestions(List.of(new SecurityQuestionDto("Pet?", "Milo")))
                .build();

        user = User.builder()
                .id(1L)
                .fullName("Alice Doe")
                .username("alice")
                .email("alice@example.com")
                .password("encoded-secret")
                .phoneNumber("9999999999")
                .role(Role.BUSINESS)
                .enabled(true)
                .failedLoginAttempts(0)
                .build();
    }

    @Test
    void registerShouldCreateUserAndReturnToken() {
        when(userRepository.existsByUsername(registerRequest.getUsername())).thenReturn(false);
        when(userRepository.existsByEmail(registerRequest.getEmail())).thenReturn(false);
        when(passwordEncoder.encode(registerRequest.getPassword())).thenReturn("encoded-secret");
        when(userRepository.save(any(User.class))).thenReturn(user);
        when(jwtService.generateToken(user, 1L, Role.BUSINESS.name())).thenReturn("jwt-token");

        AuthResponse response = authService.register(registerRequest);

        assertEquals("jwt-token", response.getToken());
        assertEquals(1L, response.getUserId());
        assertEquals("alice", response.getUsername());
        assertEquals("alice@example.com", response.getEmail());
        assertEquals("BUSINESS", response.getRole());

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());
        User savedUser = userCaptor.getValue();
        assertEquals("encoded-secret", savedUser.getPassword());
        assertTrue(savedUser.isEnabled());
        assertNotNull(savedUser.getSecurityQuestions());
        assertEquals(1, savedUser.getSecurityQuestions().size());
        assertSame(savedUser, savedUser.getSecurityQuestions().get(0).getUser());
    }

    @Test
    void registerShouldThrowWhenEmailAlreadyExists() {
        when(userRepository.existsByUsername(registerRequest.getUsername())).thenReturn(false);
        when(userRepository.existsByEmail(registerRequest.getEmail())).thenReturn(true);

        DuplicateEmailException exception = assertThrows(
                DuplicateEmailException.class,
                () -> authService.register(registerRequest)
        );

        assertEquals("Email already exists", exception.getMessage());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void loginShouldAuthenticateResetFailuresAndReturnToken() {
        AuthRequest authRequest = AuthRequest.builder()
                .email("alice@example.com")
                .password("secret")
                .build();
        user.setFailedLoginAttempts(3);

        when(userRepository.findByEmail(authRequest.getEmail())).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(jwtService.generateToken(user, 1L, Role.BUSINESS.name())).thenReturn("jwt-token");

        AuthResponse response = authService.login(authRequest);

        verify(authenticationManager).authenticate(
                argThat(authentication ->
                        authentication instanceof UsernamePasswordAuthenticationToken
                                && authRequest.getEmail().equals(authentication.getPrincipal())
                                && authRequest.getPassword().equals(authentication.getCredentials()))
        );
        assertEquals(0, user.getFailedLoginAttempts());
        assertEquals(null, user.getLockoutUntil());
        assertEquals("jwt-token", response.getToken());
        assertEquals("alice", response.getUsername());
    }

    @Test
    void loginShouldThrowInvalidCredentialsWhenAuthenticationFails() {
        AuthRequest authRequest = AuthRequest.builder()
                .email("alice@example.com")
                .password("wrong-password")
                .build();

        when(userRepository.findByEmail(authRequest.getEmail())).thenReturn(Optional.of(user));
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("Bad credentials"));
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        InvalidCredentialsException exception = assertThrows(
                InvalidCredentialsException.class,
                () -> authService.login(authRequest)
        );

        assertEquals("Invalid email or password", exception.getMessage());
        assertEquals(1, user.getFailedLoginAttempts());
        verify(userRepository).save(user);
    }
}
