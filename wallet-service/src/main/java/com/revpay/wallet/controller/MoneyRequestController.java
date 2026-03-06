package com.revpay.wallet.controller;

import com.revpay.wallet.dto.CreateMoneyRequestDto;
import com.revpay.wallet.dto.MoneyRequestResponse;
import com.revpay.wallet.dto.RespondToRequestDto;
import com.revpay.wallet.service.MoneyRequestService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/requests")
@RequiredArgsConstructor
public class MoneyRequestController {

    private final MoneyRequestService moneyRequestService;

    @PostMapping("/create")
    public ResponseEntity<MoneyRequestResponse> createRequest(
            @RequestHeader("X-User-Id") Long userId,
            @Valid @RequestBody CreateMoneyRequestDto dto) {
        return ResponseEntity.ok(moneyRequestService.createRequest(userId, dto));
    }

    @GetMapping
    public ResponseEntity<List<MoneyRequestResponse>> getRequests(@RequestHeader("X-User-Id") Long userId) {
        return ResponseEntity.ok(moneyRequestService.getRequests(userId));
    }

    @PostMapping("/{id}/respond")
    public ResponseEntity<Map<String, String>> respondToRequest(
            @RequestHeader("X-User-Id") Long userId,
            @RequestHeader("Authorization") String token,
            @PathVariable Long id,
            @Valid @RequestBody RespondToRequestDto dto) {
        moneyRequestService.respondToRequest(userId, id, dto, token);
        String message = dto.getAccept() ? "Request accepted" : "Request declined";
        return ResponseEntity.ok(Map.of("message", message));
    }
}
