package com.revpay.business.service;

import com.revpay.business.dto.BusinessAnalyticsResponse;
import com.revpay.business.entity.InvoiceStatus;
import com.revpay.business.entity.LoanStatus;
import com.revpay.business.repository.BusinessLoanRepository;
import com.revpay.business.repository.InvoiceRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class AnalyticsService {
    private final InvoiceRepository invoiceRepository;
    private final BusinessLoanRepository businessLoanRepository;

    public AnalyticsService(InvoiceRepository invoiceRepository, BusinessLoanRepository businessLoanRepository) {
        this.invoiceRepository = invoiceRepository;
        this.businessLoanRepository = businessLoanRepository;
    }

    public BusinessAnalyticsResponse getAnalytics(Long userId) {
        // Invoice analytics
        Long totalInvoices = invoiceRepository.countByUserId(userId);
        Long paidInvoices = invoiceRepository.countByUserIdAndStatus(userId, InvoiceStatus.PAID);
        Long pendingInvoices = invoiceRepository.countByUserIdAndStatus(userId, InvoiceStatus.SENT);
        Long overdueInvoices = invoiceRepository.countByUserIdAndStatus(userId, InvoiceStatus.OVERDUE);

        BigDecimal totalRevenue = invoiceRepository.findByUserIdAndStatusOrderByCreatedAtDesc(userId, InvoiceStatus.PAID)
                .stream()
                .map(invoice -> invoice.getAmount())
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal pendingRevenue = invoiceRepository.findByUserIdAndStatusOrderByCreatedAtDesc(userId, InvoiceStatus.SENT)
                .stream()
                .map(invoice -> invoice.getAmount())
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal overdueRevenue = invoiceRepository.findByUserIdAndStatusOrderByCreatedAtDesc(userId, InvoiceStatus.OVERDUE)
                .stream()
                .map(invoice -> invoice.getAmount())
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Loan analytics
        Long totalLoans = businessLoanRepository.countByUserId(userId);
        Long activeLoans = businessLoanRepository.countByUserIdAndStatus(userId, LoanStatus.APPROVED);

        BigDecimal totalLoanAmount = businessLoanRepository.findByUserIdOrderByAppliedAtDesc(userId)
                .stream()
                .map(loan -> loan.getLoanAmount())
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalOutstanding = businessLoanRepository.findByUserIdOrderByAppliedAtDesc(userId)
                .stream()
                .filter(loan -> loan.getStatus() == LoanStatus.APPROVED)
                .map(loan -> loan.getRemainingBalance())
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return BusinessAnalyticsResponse.builder()
                .totalInvoices(totalInvoices)
                .paidInvoices(paidInvoices)
                .pendingInvoices(pendingInvoices)
                .overdueInvoices(overdueInvoices)
                .totalRevenue(totalRevenue)
                .pendingRevenue(pendingRevenue)
                .overdueRevenue(overdueRevenue)
                .totalLoans(totalLoans)
                .activeLoans(activeLoans)
                .totalLoanAmount(totalLoanAmount)
                .totalOutstanding(totalOutstanding)
                .build();
    }
}
