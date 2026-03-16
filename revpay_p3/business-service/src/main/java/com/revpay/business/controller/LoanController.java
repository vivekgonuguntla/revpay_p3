package com.revpay.business.controller; 
import com.revpay.business.dto.LoanApplicationRequest;
import com.revpay.business.dto.LoanResponse;
import com.revpay.business.service.LoanService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/business/loans")
public class LoanController { 
    private final LoanService loanService;

    public LoanController(LoanService loanService) {
        this.loanService = loanService;
    }

    @PostMapping
    public ResponseEntity<LoanResponse> applyForLoan(
            @RequestHeader("X-User-Id") Long userId,
            @Valid @RequestBody LoanApplicationRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(loanService.applyForLoan(userId, request));
    }
    

    @GetMapping
    public ResponseEntity<List<LoanResponse>> getLoansByUserId(@RequestHeader("X-User-Id") Long userId) {
        return ResponseEntity.ok(loanService.getLoansByUserId(userId));
    }

    @PostMapping("/{loanId}/repayments/{repaymentId}/pay")
    public ResponseEntity<LoanResponse> payRepayment(
            @RequestHeader("X-User-Id") Long userId,
            @PathVariable Long loanId,
            @PathVariable Long repaymentId) {
        return ResponseEntity.ok(loanService.payRepayment(userId, loanId, repaymentId));
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
