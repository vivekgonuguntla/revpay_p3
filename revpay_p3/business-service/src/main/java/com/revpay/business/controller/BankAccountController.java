package com.revpay.business.controller;

import com.revpay.business.dto.BankAccountRequest;
import com.revpay.business.dto.BankAccountResponse;
import com.revpay.business.service.BankAccountService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/business/payment-methods/bank-accounts")
public class BankAccountController {
    private final BankAccountService bankAccountService;

    public BankAccountController(BankAccountService bankAccountService) {
        this.bankAccountService = bankAccountService;
    }

    @PostMapping
    public ResponseEntity<BankAccountResponse> addBankAccount(
            @RequestHeader("X-User-Id") Long userId,
            @Valid @RequestBody BankAccountRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(bankAccountService.addBankAccount(userId, request));
    }

    @GetMapping
    public ResponseEntity<List<BankAccountResponse>> getBankAccountsByUserId(@RequestHeader("X-User-Id") Long userId) {
        return ResponseEntity.ok(bankAccountService.getBankAccountsByUserId(userId));
    }

    @DeleteMapping("/{accountId}")
    public ResponseEntity<Map<String, String>> deleteBankAccount(
            @PathVariable Long accountId,
            @RequestHeader("X-User-Id") Long userId) {
        bankAccountService.deleteBankAccount(accountId, userId);
        return ResponseEntity.ok(Map.of("message", "Bank account deleted successfully"));
    }

    @PutMapping("/{accountId}/default")
    public ResponseEntity<BankAccountResponse> setDefaultAccount(
            @PathVariable Long accountId,
            @RequestHeader("X-User-Id") Long userId) {
        return ResponseEntity.ok(bankAccountService.setDefaultAccount(accountId, userId));
    }
}
