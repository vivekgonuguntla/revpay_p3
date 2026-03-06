package com.revpay.wallet.controller;

import com.revpay.wallet.dto.WalletOperationRequest;
import com.revpay.wallet.dto.WalletResponse;
import com.revpay.wallet.service.WalletService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/wallet")
@RequiredArgsConstructor
public class WalletController {

    private final WalletService walletService;

    @GetMapping("/balance")
    public ResponseEntity<WalletResponse> getBalance(@RequestHeader("X-User-Id") Long userId) {
        return ResponseEntity.ok(walletService.getBalance(userId));
    }

    @PostMapping("/add-funds")
    public ResponseEntity<WalletResponse> addFunds(
            @RequestHeader("X-User-Id") Long userId,
            @RequestHeader("Authorization") String token,
            @Valid @RequestBody WalletOperationRequest request) {
        return ResponseEntity.ok(walletService.addFunds(userId, request, token));
    }

    @PostMapping("/withdraw")
    public ResponseEntity<WalletResponse> withdraw(
            @RequestHeader("X-User-Id") Long userId,
            @Valid @RequestBody WalletOperationRequest request) {
        return ResponseEntity.ok(walletService.withdraw(userId, request));
    }
}
