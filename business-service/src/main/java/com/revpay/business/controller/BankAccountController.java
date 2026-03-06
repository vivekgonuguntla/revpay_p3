package com.revpay.business.controller;

import com.revpay.business.dto.BankAccountRequest;
import com.revpay.business.dto.BankAccountResponse;
import com.revpay.business.service.BankAccountService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/business/bank-accounts")
public class BankAccountController {
    private final BankAccountService bankAccountService;

    public BankAccountController(BankAccountService bankAccountService) {
        this.bankAccountService = bankAccountService;
    }

    @PostMapping("/user/{userId}")
    public ResponseEntity<BankAccountResponse> addBankAccount(
            @PathVariable Long userId,
            @Valid @RequestBody BankAccountRequest request) {
        return ResponseEntity.ok(bankAccountService.addBankAccount(userId, request));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<BankAccountResponse>> getBankAccountsByUserId(@PathVariable Long userId) {
        return ResponseEntity.ok(bankAccountService.getBankAccountsByUserId(userId));
    }

    @DeleteMapping("/{accountId}/user/{userId}")
    public ResponseEntity<Void> deleteBankAccount(
            @PathVariable Long accountId,
            @PathVariable Long userId) {
        bankAccountService.deleteBankAccount(accountId, userId);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{accountId}/user/{userId}/default")
    public ResponseEntity<BankAccountResponse> setDefaultAccount(
            @PathVariable Long accountId,
            @PathVariable Long userId) {
        return ResponseEntity.ok(bankAccountService.setDefaultAccount(accountId, userId));
    }
}
