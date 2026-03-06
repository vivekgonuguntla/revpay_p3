package com.revpay.auth.controller;

import com.revpay.auth.dto.*;
import com.revpay.auth.service.AuthService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private static final Logger log = LoggerFactory.getLogger(AuthController.class);

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@RequestBody @Valid RegisterRequest request) {
        log.info("Register request received for email: {}", request.getEmail());
        AuthResponse response = authService.register(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody @Valid AuthRequest request) {
        log.info("Login request received for email: {}", request.getEmail());
        AuthResponse response = authService.login(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/recovery/questions")
    public ResponseEntity<List<String>> getRecoveryQuestions(@RequestParam String email) {
        log.info("Recovery questions requested for email: {}", email);
        List<String> questions = authService.getRecoveryQuestions(email);
        return ResponseEntity.ok(questions);
    }

    @PostMapping("/recovery/reset")
    public ResponseEntity<Map<String, String>> resetPassword(@RequestBody @Valid ResetPasswordRequest request) {
        log.info("Password reset request received for email: {}", request.getEmail());
        authService.resetPassword(request);
        return ResponseEntity.ok(Map.of("message", "Password reset successfully"));
    }

    @GetMapping("/validate")
    public ResponseEntity<UserValidationResponse> validateToken(
            @RequestHeader("Authorization") String authHeader) {
        log.info("Token validation request received");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.ok(UserValidationResponse.builder().valid(false).build());
        }

        String token = authHeader.substring(7);
        UserValidationResponse response = authService.validateToken(token);
        return ResponseEntity.ok(response);
    }
}
