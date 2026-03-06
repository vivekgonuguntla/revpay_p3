package com.revpay.wallet.controller;

import com.revpay.wallet.dto.SendMoneyRequest;
import com.revpay.wallet.dto.TransactionHistoryResponse;
import com.revpay.wallet.dto.TransactionResponse;
import com.revpay.wallet.service.TransactionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/transactions")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;

    @GetMapping
    public ResponseEntity<TransactionHistoryResponse> getHistory(@RequestHeader("X-User-Id") Long userId) {
        return ResponseEntity.ok(transactionService.getTransactionHistory(userId));
    }

    @PostMapping("/send")
    public ResponseEntity<TransactionResponse> sendMoney(
            @RequestHeader("X-User-Id") Long userId,
            @RequestHeader("Authorization") String token,
            @Valid @RequestBody SendMoneyRequest request) {
        return ResponseEntity.ok(transactionService.sendMoney(userId, request, token));
    }
}
