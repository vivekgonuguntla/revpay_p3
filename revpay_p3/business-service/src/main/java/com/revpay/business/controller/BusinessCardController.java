package com.revpay.business.controller;

import com.revpay.business.client.CardServiceClient;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/business/payment-methods/cards")
public class BusinessCardController {
    private final CardServiceClient cardServiceClient;

    public BusinessCardController(CardServiceClient cardServiceClient) {
        this.cardServiceClient = cardServiceClient;
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> addCard(
            @RequestHeader("X-User-Id") Long userId,
            @RequestHeader("X-User-Email") String email,
            @RequestHeader("X-User-Role") String role,
            @RequestBody Map<String, Object> request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(cardServiceClient.addCard(userId, email, role, request));
    }

    @DeleteMapping("/{cardId}")
    public ResponseEntity<Map<String, String>> deleteCard(
            @RequestHeader("X-User-Id") Long userId,
            @RequestHeader("X-User-Email") String email,
            @RequestHeader("X-User-Role") String role,
            @PathVariable Long cardId) {
        return ResponseEntity.ok(cardServiceClient.deleteCard(userId, email, role, cardId));
    }
}