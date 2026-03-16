package com.revpay.auth.service;

import com.revpay.auth.dto.*;
import com.revpay.auth.entity.Role;
import com.revpay.auth.entity.SecurityQuestion;
import com.revpay.auth.entity.User;
import com.revpay.auth.exception.AccountLockedException;
import com.revpay.auth.exception.DuplicateEmailException;
import com.revpay.auth.exception.InvalidCredentialsException;
import com.revpay.auth.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AuthService implements UserDetailsService {

    private static final Logger log = LoggerFactory.getLogger(AuthService.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    private static final int MAX_LOGIN_ATTEMPTS = 5;
    private static final int LOCKOUT_DURATION_MINUTES = 15;

    public AuthService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       JwtService jwtService,
                       @Lazy AuthenticationManager authenticationManager) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
    }

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        log.info("Attempting to register user: {}", request.getEmail());

        if (userRepository.existsByUsername(request.getUsername())) {
            throw new DuplicateEmailException("Username already exists");
        }

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateEmailException("Email already exists");
        }

        Role role = request.getRole() != null ? request.getRole() : Role.PERSONAL;

        User user = User.builder()
                .fullName(request.getFullName())
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .phoneNumber(request.getPhoneNumber())
                .role(role)
                .enabled(true)
                .failedLoginAttempts(0)
                .build();

        if (request.getSecurityQuestions() != null && !request.getSecurityQuestions().isEmpty()) {
            List<SecurityQuestion> questions = request.getSecurityQuestions().stream()
                    .map(q -> SecurityQuestion.builder()
                            .user(user)
                            .question(q.getQuestion())
                            .answer(q.getAnswer())
                            .build())
                    .collect(Collectors.toList());
            user.setSecurityQuestions(questions);
        }

        User savedUser = userRepository.save(user);
        log.info("User registered successfully: {}", savedUser.getEmail());

        String token = jwtService.generateToken(savedUser, savedUser.getId(), savedUser.getRole().name());
        return AuthResponse.builder()
                .token(token)
                .userId(savedUser.getId())
                .username(savedUser.getUsername())
                .email(savedUser.getEmail())
                .role(savedUser.getRole().name())
                .build();
    }

    @Transactional
    public AuthResponse login(AuthRequest request) {
        log.info("Attempting to login user: {}", request.getEmail());

        User user = userRepository.findByEmail(request.getEmail())
                .or(() -> userRepository.findByUsername(request.getEmail()))
                .orElseThrow(() -> new InvalidCredentialsException("Invalid email or password"));

        // Check if account is locked
        if (user.getLockoutUntil() != null && user.getLockoutUntil().isAfter(LocalDateTime.now())) {
            throw new AccountLockedException(
                    "Account is locked until " + user.getLockoutUntil() +
                    ". Please try again later.");
        }

        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getEmail(),
                            request.getPassword()
                    )
            );

            // Reset failed attempts on successful login
            user.setFailedLoginAttempts(0);
            user.setLockoutUntil(null);
            userRepository.save(user);

            log.info("User logged in successfully: {}", user.getEmail());

            String token = jwtService.generateToken(user, user.getId(), user.getRole().name());
            return AuthResponse.builder()
                    .token(token)
                    .userId(user.getId())
                    .username(user.getUsername())
                    .email(user.getEmail())
                    .role(user.getRole().name())
                    .build();

        } catch (org.springframework.security.core.AuthenticationException e) {
            // Increment failed attempts
            int attempts = user.getFailedLoginAttempts() + 1;
            user.setFailedLoginAttempts(attempts);

            if (attempts >= MAX_LOGIN_ATTEMPTS) {
                user.setLockoutUntil(LocalDateTime.now().plusMinutes(LOCKOUT_DURATION_MINUTES));
                userRepository.save(user);
                log.warn("Account locked for user: {} due to {} failed attempts", user.getEmail(), attempts);
                throw new AccountLockedException(
                        "Account locked due to too many failed attempts. Please try again after " +
                        LOCKOUT_DURATION_MINUTES + " minutes.");
            }

            userRepository.save(user);
            log.warn("Failed login attempt {} for user: {}", attempts, request.getEmail());
            throw new InvalidCredentialsException("Invalid email or password");
        }
    }

    public List<String> getRecoveryQuestions(String email) {
        log.info("Retrieving recovery questions for: {}", email);
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        return user.getSecurityQuestions().stream()
                .map(SecurityQuestion::getQuestion)
                .collect(Collectors.toList());
    }

    @Transactional
    public void resetPassword(ResetPasswordRequest request) {
        log.info("Resetting password for user: {}", request.getEmail());
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        // Verify security answers
        if (user.getSecurityQuestions().size() != request.getAnswers().size()) {
            throw new InvalidCredentialsException("Question/Answer count mismatch");
        }

        for (int i = 0; i < user.getSecurityQuestions().size(); i++) {
            String storedAnswer = user.getSecurityQuestions().get(i).getAnswer();
            SecurityQuestionDto providedAnswer = request.getAnswers().get(i);
            if (!storedAnswer.equalsIgnoreCase(providedAnswer.getAnswer())) {
                throw new InvalidCredentialsException("Incorrect answers to security questions");
            }
        }

        // Reset password and unlock account
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        user.setFailedLoginAttempts(0);
        user.setLockoutUntil(null);
        userRepository.save(user);

        log.info("Password reset successfully for user: {}", request.getEmail());
    }

    public UserValidationResponse validateToken(String token) {
        try {
            String username = jwtService.extractUsername(token);
            User user = userRepository.findByUsername(username)
                    .or(() -> userRepository.findByEmail(username))
                    .orElseThrow(() -> new UsernameNotFoundException("User not found"));

            if (jwtService.isTokenValid(token, user)) {
                return UserValidationResponse.builder()
                        .valid(true)
                        .userId(user.getId())
                        .username(user.getUsername())
                        .email(user.getEmail())
                        .role(user.getRole().name())
                        .build();
            }
        } catch (Exception e) {
            log.error("Token validation failed", e);
        }

        return UserValidationResponse.builder()
                .valid(false)
                .build();
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByEmail(username)
                .or(() -> userRepository.findByUsername(username))
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
    }
}
