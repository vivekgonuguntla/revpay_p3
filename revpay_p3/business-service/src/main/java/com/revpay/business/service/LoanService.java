package com.revpay.business.service;

import com.revpay.business.client.NotificationServiceClient;
import com.revpay.business.dto.LoanApplicationRequest;
import com.revpay.business.dto.LoanRepaymentResponse;
import com.revpay.business.dto.LoanResponse;
import com.revpay.business.entity.BusinessLoan;
import com.revpay.business.entity.LoanRepayment;
import com.revpay.business.entity.LoanStatus;
import com.revpay.business.entity.RepaymentStatus;
import com.revpay.business.exception.BusinessException;
import com.revpay.business.exception.ResourceNotFoundException;
import com.revpay.business.repository.BusinessLoanRepository;
import com.revpay.business.repository.LoanRepaymentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class LoanService {
    private final BusinessLoanRepository businessLoanRepository;
    private final LoanRepaymentRepository loanRepaymentRepository;
    private final NotificationServiceClient notificationServiceClient;

    public LoanService(BusinessLoanRepository businessLoanRepository, LoanRepaymentRepository loanRepaymentRepository,
                       NotificationServiceClient notificationServiceClient) {
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
                .status(LoanStatus.APPROVED)
                .termMonths(request.getTermMonths())
                .remainingBalance(request.getLoanAmount())
                .build();

        BusinessLoan saved = businessLoanRepository.save(loan);
        createRepaymentSchedule(saved);

        try {
            NotificationServiceClient.NotificationRequest notification = new NotificationServiceClient.NotificationRequest();
            notification.userId = userId;
            notification.category = "ALERTS";
            notification.title = "Loan Approved";
            notification.message = "Your loan has been approved and repayments are ready.";
            notification.type = "LOAN_APPROVED";
            notification.amount = request.getLoanAmount();
            notification.eventStatus = LoanStatus.APPROVED.name();
            notification.navigationTarget = "/business";
            notification.eventTime = saved.getAppliedAt();
            notificationServiceClient.sendNotification(notification);
        } catch (Exception ignored) {
        }

        return mapToResponse(saved);
    }

    public List<LoanResponse> getLoansByUserId(Long userId) {
        return businessLoanRepository.findByUserIdOrderByAppliedAtDesc(userId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public LoanResponse payRepayment(Long userId, Long loanId, Long repaymentId) {
        BusinessLoan loan = businessLoanRepository.findById(loanId)
                .orElseThrow(() -> new ResourceNotFoundException("Loan not found: " + loanId));

        if (!loan.getUserId().equals(userId)) {
            throw new ResourceNotFoundException("Loan not found: " + loanId);
        }

        LoanRepayment repayment = loanRepaymentRepository.findByIdAndLoanId(repaymentId, loanId)
                .orElseThrow(() -> new ResourceNotFoundException("Repayment not found: " + repaymentId));

        if (repayment.getStatus() == RepaymentStatus.PAID) {
            throw new BusinessException("Repayment already paid");
        }

        repayment.setStatus(RepaymentStatus.PAID);
        repayment.setPaidDate(LocalDateTime.now());
        loanRepaymentRepository.save(repayment);

        BigDecimal remainingBalance = loan.getRemainingBalance().subtract(repayment.getAmount());
        loan.setRemainingBalance(remainingBalance.compareTo(BigDecimal.ZERO) < 0 ? BigDecimal.ZERO : remainingBalance);
        businessLoanRepository.save(loan);

        try {
            NotificationServiceClient.NotificationRequest notification = new NotificationServiceClient.NotificationRequest();
            notification.userId = userId;
            notification.category = "ALERTS";
            notification.title = "Loan Repayment Paid";
            notification.message = "Loan repayment completed successfully.";
            notification.type = "LOAN_REPAYMENT_PAID";
            notification.amount = repayment.getAmount();
            notification.eventStatus = "PAID";
            notification.navigationTarget = "/business";
            notification.eventTime = repayment.getPaidDate();
            notificationServiceClient.sendNotification(notification);
        } catch (Exception ignored) {
        }

        return mapToResponse(loan);
    }

    @Transactional
    public LoanResponse approveLoan(Long loanId) {
        BusinessLoan loan = businessLoanRepository.findById(loanId)
                .orElseThrow(() -> new ResourceNotFoundException("Loan not found: " + loanId));

        if (loan.getStatus() != LoanStatus.SUBMITTED && loan.getStatus() != LoanStatus.ADDITIONAL_DOCUMENTS_REQUIRED) {
            throw new BusinessException("Loan cannot be approved in current status");
        }

        loan.setStatus(LoanStatus.APPROVED);
        BusinessLoan saved = businessLoanRepository.save(loan);
        createRepaymentSchedule(saved);

        try {
            NotificationServiceClient.NotificationRequest notification = new NotificationServiceClient.NotificationRequest();
            notification.userId = loan.getUserId();
            notification.category = "ALERTS";
            notification.title = "Loan Approved";
            notification.message = "Your loan application has been approved.";
            notification.type = "LOAN_APPROVED";
            notification.amount = loan.getLoanAmount();
            notification.eventStatus = LoanStatus.APPROVED.name();
            notification.navigationTarget = "/business";
            notification.eventTime = java.time.LocalDateTime.now();
            notificationServiceClient.sendNotification(notification);
        } catch (Exception ignored) {
        }

        return mapToResponse(saved);
    }

    @Transactional
    public LoanResponse rejectLoan(Long loanId) {
        BusinessLoan loan = businessLoanRepository.findById(loanId)
                .orElseThrow(() -> new ResourceNotFoundException("Loan not found: " + loanId));

        loan.setStatus(LoanStatus.REJECTED);
        BusinessLoan saved = businessLoanRepository.save(loan);

        try {
            NotificationServiceClient.NotificationRequest notification = new NotificationServiceClient.NotificationRequest();
            notification.userId = loan.getUserId();
            notification.category = "ALERTS";
            notification.title = "Loan Rejected";
            notification.message = "Your loan application has been rejected.";
            notification.type = "LOAN_REJECTED";
            notification.amount = loan.getLoanAmount();
            notification.eventStatus = LoanStatus.REJECTED.name();
            notification.navigationTarget = "/business";
            notification.eventTime = java.time.LocalDateTime.now();
            notificationServiceClient.sendNotification(notification);
        } catch (Exception ignored) {
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
        List<LoanRepayment> repaymentEntities = loanRepaymentRepository.findByLoanOrderByDueDateAsc(loan);
        List<LoanRepaymentResponse> repayments = new ArrayList<>();

        for (int i = 0; i < repaymentEntities.size(); i++) {
            LoanRepayment repayment = repaymentEntities.get(i);
            repayments.add(LoanRepaymentResponse.builder()
                    .id(repayment.getId())
                    .installmentNumber(i + 1)
                    .dueDate(repayment.getDueDate())
                    .amount(repayment.getAmount())
                    .status(repayment.getStatus())
                    .paidAt(repayment.getPaidDate())
                    .build());
        }

        repayments.sort(Comparator.comparing(LoanRepaymentResponse::getInstallmentNumber));

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
