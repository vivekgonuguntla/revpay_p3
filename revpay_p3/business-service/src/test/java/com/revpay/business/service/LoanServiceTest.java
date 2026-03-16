package com.revpay.business.service;

import com.revpay.business.client.NotificationServiceClient;
import com.revpay.business.dto.LoanApplicationRequest;
import com.revpay.business.dto.LoanResponse;
import com.revpay.business.entity.BusinessLoan;
import com.revpay.business.entity.LoanRepayment;
import com.revpay.business.entity.LoanStatus;
import com.revpay.business.entity.RepaymentStatus;
import com.revpay.business.exception.BusinessException;
import com.revpay.business.exception.ResourceNotFoundException;
import com.revpay.business.repository.BusinessLoanRepository;
import com.revpay.business.repository.LoanRepaymentRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LoanServiceTest {

    @Mock
    private BusinessLoanRepository businessLoanRepository;

    @Mock
    private LoanRepaymentRepository loanRepaymentRepository;

    @Mock
    private NotificationServiceClient notificationServiceClient;

    @InjectMocks
    private LoanService loanService;

    @Test
    void applyForLoanCreatesApprovedLoanAndRepaymentSchedule() {
        LoanApplicationRequest request = new LoanApplicationRequest();
        request.setLoanAmount(new BigDecimal("12000"));
        request.setPurpose("Inventory");
        request.setFinancialDetails("Stable revenue");
        request.setSupportingDocsPath("/docs/loan.pdf");
        request.setTermMonths(3);

        BusinessLoan persistedLoan = BusinessLoan.builder()
                .id(5L)
                .userId(7L)
                .loanAmount(new BigDecimal("12000"))
                .purpose("Inventory")
                .financialDetails("Stable revenue")
                .supportingDocsPath("/docs/loan.pdf")
                .status(LoanStatus.APPROVED)
                .termMonths(3)
                .remainingBalance(new BigDecimal("12000"))
                .appliedAt(LocalDateTime.now())
                .build();

        when(businessLoanRepository.save(any(BusinessLoan.class))).thenReturn(persistedLoan);
        when(loanRepaymentRepository.findByLoanOrderByDueDateAsc(persistedLoan)).thenReturn(List.of());

        LoanResponse response = loanService.applyForLoan(7L, request);

        ArgumentCaptor<List<LoanRepayment>> repaymentsCaptor = ArgumentCaptor.forClass(List.class);
        verify(loanRepaymentRepository).saveAll(repaymentsCaptor.capture());
        assertEquals(3, repaymentsCaptor.getValue().size());
        assertEquals(new BigDecimal("4000.00"), repaymentsCaptor.getValue().get(0).getAmount());
        assertEquals(LoanStatus.APPROVED, response.getStatus());
        verify(notificationServiceClient).sendNotification(any(NotificationServiceClient.NotificationRequest.class));
    }

    @Test
    void payRepaymentMarksRepaymentPaidAndUpdatesBalance() {
        BusinessLoan loan = BusinessLoan.builder()
                .id(5L)
                .userId(7L)
                .loanAmount(new BigDecimal("12000"))
                .status(LoanStatus.APPROVED)
                .termMonths(3)
                .remainingBalance(new BigDecimal("12000"))
                .appliedAt(LocalDateTime.now())
                .build();
        LoanRepayment repayment = LoanRepayment.builder()
                .id(2L)
                .loan(loan)
                .dueDate(LocalDate.now().plusMonths(1))
                .amount(new BigDecimal("4000"))
                .status(RepaymentStatus.PENDING)
                .build();

        when(businessLoanRepository.findById(5L)).thenReturn(Optional.of(loan));
        when(loanRepaymentRepository.findByIdAndLoanId(2L, 5L)).thenReturn(Optional.of(repayment));
        when(loanRepaymentRepository.save(repayment)).thenReturn(repayment);
        when(businessLoanRepository.save(loan)).thenReturn(loan);
        when(loanRepaymentRepository.findByLoanOrderByDueDateAsc(loan)).thenReturn(List.of(repayment));

        LoanResponse response = loanService.payRepayment(7L, 5L, 2L);

        assertEquals(RepaymentStatus.PAID, repayment.getStatus());
        assertNotNull(repayment.getPaidDate());
        assertEquals(new BigDecimal("8000"), response.getRemainingBalance());
    }

    @Test
    void payRepaymentThrowsForWrongOwner() {
        BusinessLoan loan = BusinessLoan.builder()
                .id(5L)
                .userId(10L)
                .build();

        when(businessLoanRepository.findById(5L)).thenReturn(Optional.of(loan));

        assertThrows(ResourceNotFoundException.class, () -> loanService.payRepayment(7L, 5L, 2L));
    }

    @Test
    void payRepaymentThrowsWhenAlreadyPaid() {
        BusinessLoan loan = BusinessLoan.builder()
                .id(5L)
                .userId(7L)
                .build();
        LoanRepayment repayment = LoanRepayment.builder()
                .id(2L)
                .loan(loan)
                .amount(new BigDecimal("4000"))
                .status(RepaymentStatus.PAID)
                .build();

        when(businessLoanRepository.findById(5L)).thenReturn(Optional.of(loan));
        when(loanRepaymentRepository.findByIdAndLoanId(2L, 5L)).thenReturn(Optional.of(repayment));

        assertThrows(BusinessException.class, () -> loanService.payRepayment(7L, 5L, 2L));
    }

    @Test
    void approveLoanCreatesRepaymentsForSubmittedLoan() {
        BusinessLoan loan = BusinessLoan.builder()
                .id(5L)
                .userId(7L)
                .loanAmount(new BigDecimal("12000"))
                .status(LoanStatus.SUBMITTED)
                .termMonths(3)
                .remainingBalance(new BigDecimal("12000"))
                .build();

        when(businessLoanRepository.findById(5L)).thenReturn(Optional.of(loan));
        when(businessLoanRepository.save(loan)).thenReturn(loan);
        when(loanRepaymentRepository.findByLoanOrderByDueDateAsc(loan)).thenReturn(List.of());

        LoanResponse response = loanService.approveLoan(5L);

        verify(loanRepaymentRepository).saveAll(any(List.class));
        assertEquals(LoanStatus.APPROVED, response.getStatus());
    }

    @Test
    void approveLoanRejectsInvalidStatus() {
        BusinessLoan loan = BusinessLoan.builder()
                .id(5L)
                .status(LoanStatus.REJECTED)
                .build();

        when(businessLoanRepository.findById(5L)).thenReturn(Optional.of(loan));

        assertThrows(BusinessException.class, () -> loanService.approveLoan(5L));
    }
}
