package com.revpay.business.service;

import com.revpay.business.client.NotificationServiceClient;
import com.revpay.business.dto.LoanApplicationRequest;
import com.revpay.business.dto.LoanRepaymentResponse;
import com.revpay.business.dto.LoanResponse;
import com.revpay.business.entity.BusinessLoan;
import com.revpay.business.entity.LoanRepayment;
import com.revpay.business.entity.LoanStatus;
import com.revpay.business.entity.RepaymentStatus;
import com.revpay.business.repository.BusinessLoanRepository;
import com.revpay.business.repository.LoanRepaymentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class LoanService {
    private final BusinessLoanRepository businessLoanRepository;
    private final LoanRepaymentRepository loanRepaymentRepository;
    private final NotificationServiceClient notificationServiceClient;

    public LoanService(BusinessLoanRepository businessLoanRepository, LoanRepaymentRepository loanRepaymentRepository, NotificationServiceClient notificationServiceClient) {
        this.businessLoanRepository = businessLoanRepository;
        this.loanRepaymentRepository = loanRepaymentRepository;
        this.notificationServiceClient = notificationServiceClient;
    }

    @Transactional
    public LoanResponse applyForLoan(Long userId, LoanApplicationRequest request) {
        BusinessLoan loan = BusinessLoan.builder()
                .userId(userId)
                .loanAmount(request.getLoanAmount())
                .purpose(request.getPurpose())
                .financialDetails(request.getFinancialDetails())
                .supportingDocsPath(request.getSupportingDocsPath())
                .status(LoanStatus.SUBMITTED)
                .termMonths(request.getTermMonths())
                .remainingBalance(request.getLoanAmount())
                .build();

        BusinessLoan saved = businessLoanRepository.save(loan);

        // Send notification
        try {
            NotificationServiceClient.NotificationRequest notification = new NotificationServiceClient.NotificationRequest();
            notification.userId = userId;
            notification.title = "Loan Application Submitted";
            notification.message = "Your loan application for " + request.getLoanAmount() + " has been submitted.";
            notification.type = "LOAN_APPLICATION";
            notification.category = "BUSINESS";
            notificationServiceClient.sendNotification(notification);
        } catch (Exception e) {
            // Log error
        }

        return mapToResponse(saved);
    }

    public List<LoanResponse> getLoansByUserId(Long userId) {
        return businessLoanRepository.findByUserIdOrderByAppliedAtDesc(userId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public LoanResponse getLoanById(Long loanId) {
        BusinessLoan loan = businessLoanRepository.findById(loanId)
                .orElseThrow(() -> new RuntimeException("Loan not found: " + loanId));
        return mapToResponse(loan);
    }

    @Transactional
    public LoanResponse approveLoan(Long loanId) {
        BusinessLoan loan = businessLoanRepository.findById(loanId)
                .orElseThrow(() -> new RuntimeException("Loan not found: " + loanId));

        if (loan.getStatus() != LoanStatus.SUBMITTED && loan.getStatus() != LoanStatus.ADDITIONAL_DOCUMENTS_REQUIRED) {
            throw new RuntimeException("Loan cannot be approved in current status");
        }

        loan.setStatus(LoanStatus.APPROVED);
        BusinessLoan saved = businessLoanRepository.save(loan);

        // Create repayment schedule
        createRepaymentSchedule(saved);

        // Send notification
        try {
            NotificationServiceClient.NotificationRequest notification = new NotificationServiceClient.NotificationRequest();
            notification.userId = loan.getUserId();
            notification.title = "Loan Approved";
            notification.message = "Your loan application has been approved!";
            notification.type = "LOAN_APPROVED";
            notification.category = "BUSINESS";
            notificationServiceClient.sendNotification(notification);
        } catch (Exception e) {
            // Log error
        }

        return mapToResponse(saved);
    }

    @Transactional
    public LoanResponse rejectLoan(Long loanId) {
        BusinessLoan loan = businessLoanRepository.findById(loanId)
                .orElseThrow(() -> new RuntimeException("Loan not found: " + loanId));

        loan.setStatus(LoanStatus.REJECTED);
        BusinessLoan saved = businessLoanRepository.save(loan);

        // Send notification
        try {
            NotificationServiceClient.NotificationRequest notification = new NotificationServiceClient.NotificationRequest();
            notification.userId = loan.getUserId();
            notification.title = "Loan Rejected";
            notification.message = "Your loan application has been rejected.";
            notification.type = "LOAN_REJECTED";
            notification.category = "BUSINESS";
            notificationServiceClient.sendNotification(notification);
        } catch (Exception e) {
            // Log error
        }

        return mapToResponse(saved);
    }

    private void createRepaymentSchedule(BusinessLoan loan) {
        BigDecimal monthlyAmount = loan.getLoanAmount()
                .divide(BigDecimal.valueOf(loan.getTermMonths()), 2, RoundingMode.HALF_UP);

        List<LoanRepayment> repayments = new ArrayList<>();
        LocalDate nextDueDate = LocalDate.now().plusMonths(1);

        for (int i = 0; i < loan.getTermMonths(); i++) {
            LoanRepayment repayment = LoanRepayment.builder()
                    .loan(loan)
                    .dueDate(nextDueDate)
                    .amount(monthlyAmount)
                    .status(RepaymentStatus.PENDING)
                    .build();
            repayments.add(repayment);
            nextDueDate = nextDueDate.plusMonths(1);
        }

        loanRepaymentRepository.saveAll(repayments);
    }

    private LoanResponse mapToResponse(BusinessLoan loan) {
        List<LoanRepaymentResponse> repayments = loan.getRepayments() != null ? loan.getRepayments().stream()
                .map(repayment -> LoanRepaymentResponse.builder()
                        .id(repayment.getId())
                        .loanId(loan.getId())
                        .dueDate(repayment.getDueDate())
                        .amount(repayment.getAmount())
                        .status(repayment.getStatus())
                        .paidDate(repayment.getPaidDate())
                        .build())
                .collect(Collectors.toList()) : List.of();

        return LoanResponse.builder()
                .id(loan.getId())
                .loanAmount(loan.getLoanAmount())
                .purpose(loan.getPurpose())
                .financialDetails(loan.getFinancialDetails())
                .supportingDocsPath(loan.getSupportingDocsPath())
                .status(loan.getStatus())
                .termMonths(loan.getTermMonths())
                .remainingBalance(loan.getRemainingBalance())
                .appliedAt(loan.getAppliedAt())
                .repayments(repayments)
                .build();
    }
}
