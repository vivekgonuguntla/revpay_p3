package com.revpay.card.controller;

import com.revpay.card.dto.AddCardRequest;
import com.revpay.card.dto.CardResponse;
import com.revpay.card.service.CardService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/cards")
public class CardController {

    private final CardService cardService;

    public CardController(CardService cardService) {
        this.cardService = cardService;
    }

    @PostMapping
    public ResponseEntity<CardResponse> addCard(
            @RequestHeader("X-User-Id") Long userId,
            @Valid @RequestBody AddCardRequest request) {
        CardResponse response = cardService.addCard(userId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<List<CardResponse>> getCards(
            @RequestHeader("X-User-Id") Long userId) {
        List<CardResponse> cards = cardService.getCards(userId);
        return ResponseEntity.ok(cards);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deleteCard(
            @RequestHeader("X-User-Id") Long userId,
            @PathVariable Long id) {
        cardService.deleteCard(userId, id);
        return ResponseEntity.ok(Map.of("message", "Card deleted successfully"));
    }

    @PatchMapping("/{id}/default")
    public ResponseEntity<CardResponse> setDefaultCard(
            @RequestHeader("X-User-Id") Long userId,
            @PathVariable Long id) {
        CardResponse response = cardService.setDefaultCard(userId, id);
        return ResponseEntity.ok(response);
    }
}
