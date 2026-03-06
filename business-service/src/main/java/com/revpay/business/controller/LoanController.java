package com.revpay.business.controller;

import com.revpay.business.dto.LoanApplicationRequest;
import com.revpay.business.dto.LoanResponse;
import com.revpay.business.service.LoanService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/business/loans")
public class LoanController {
    private final LoanService loanService;

    public LoanController(LoanService loanService) {
        this.loanService = loanService;
    }

    @PostMapping("/user/{userId}")
    public ResponseEntity<LoanResponse> applyForLoan(
            @PathVariable Long userId,
            @Valid @RequestBody LoanApplicationRequest request) {
        return ResponseEntity.ok(loanService.applyForLoan(userId, request));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<LoanResponse>> getLoansByUserId(@PathVariable Long userId) {
        return ResponseEntity.ok(loanService.getLoansByUserId(userId));
    }

    @GetMapping("/{loanId}")
    public ResponseEntity<LoanResponse> getLoanById(@PathVariable Long loanId) {
        return ResponseEntity.ok(loanService.getLoanById(loanId));
    }

    @PutMapping("/{loanId}/approve")
    public ResponseEntity<LoanResponse> approveLoan(@PathVariable Long loanId) {
        return ResponseEntity.ok(loanService.approveLoan(loanId));
    }

    @PutMapping("/{loanId}/reject")
    public ResponseEntity<LoanResponse> rejectLoan(@PathVariable Long loanId) {
        return ResponseEntity.ok(loanService.rejectLoan(loanId));
    }
}
