package com.revpay.auth.controller;

import com.revpay.auth.dto.PinResetRequest;
import com.revpay.auth.dto.PinSetupRequest;
import com.revpay.auth.dto.PinVerifyRequest;
import com.revpay.auth.service.PinService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/security")
public class SecurityController {

    private static final Logger log = LoggerFactory.getLogger(SecurityController.class);

    private final PinService pinService;

    public SecurityController(PinService pinService) {
        this.pinService = pinService;
    }

    @PostMapping("/pin/setup")
    public ResponseEntity<Map<String, String>> setupPin(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody @Valid PinSetupRequest request) {
        log.info("PIN setup request for user: {}", userDetails.getUsername());
        pinService.setupPin(userDetails.getUsername(), request);
        return ResponseEntity.ok(Map.of("message", "Transaction PIN set successfully"));
    }

    @PostMapping("/pin/verify")
    public ResponseEntity<Map<String, Object>> verifyPin(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody @Valid PinVerifyRequest request) {
        log.info("PIN verify request for user: {}", userDetails.getUsername());
        boolean isValid = pinService.verifyPin(userDetails.getUsername(), request);

        if (isValid) {
            return ResponseEntity.ok(Map.of("valid", true));
        } else {
            return ResponseEntity.status(401)
                    .body(Map.of("valid", false, "message", "Incorrect PIN"));
        }
    }

    @PostMapping("/pin/reset")
    public ResponseEntity<Map<String, String>> resetPin(@RequestBody @Valid PinResetRequest request) {
        log.info("PIN reset request for email: {}", request.getEmail());
        pinService.resetPin(request);
        return ResponseEntity.ok(Map.of("message", "PIN reset successfully"));
    }
}
