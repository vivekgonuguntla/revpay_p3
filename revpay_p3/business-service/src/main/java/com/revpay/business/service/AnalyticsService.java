package com.revpay.business.service;

import com.revpay.business.dto.BusinessAnalyticsResponse;
import com.revpay.business.entity.Invoice;
import com.revpay.business.entity.InvoiceStatus;
import com.revpay.business.repository.InvoiceRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class AnalyticsService {
    private final InvoiceRepository invoiceRepository;

    public AnalyticsService(InvoiceRepository invoiceRepository) {
        this.invoiceRepository = invoiceRepository;
    }

    public BusinessAnalyticsResponse getAnalytics(Long userId, LocalDate from, LocalDate to) {
        List<Invoice> allInvoices = invoiceRepository.findByUserIdOrderByCreatedAtDesc(userId).stream()
                .filter(invoice -> isWithinRange(invoice.getCreatedAt() == null ? null : invoice.getCreatedAt().toLocalDate(), from, to))
                .collect(Collectors.toList());

        List<Invoice> paidInvoices = allInvoices.stream()
                .filter(invoice -> invoice.getStatus() == InvoiceStatus.PAID)
                .collect(Collectors.toList());

        List<Invoice> outstandingInvoices = allInvoices.stream()
                .filter(invoice -> invoice.getStatus() == InvoiceStatus.SENT || invoice.getStatus() == InvoiceStatus.OVERDUE)
                .collect(Collectors.toList());

        BigDecimal totalRevenue = paidInvoices.stream()
                .map(Invoice::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalOutstanding = outstandingInvoices.stream()
                .map(Invoice::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        List<BusinessAnalyticsResponse.PaymentTrend> paymentTrends = paidInvoices.stream()
                .collect(Collectors.groupingBy(
                        invoice -> invoice.getPaidAt() == null ? invoice.getCreatedAt().toLocalDate() : invoice.getPaidAt().toLocalDate(),
                        Collectors.mapping(Invoice::getAmount, Collectors.reducing(BigDecimal.ZERO, BigDecimal::add))))
                .entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .map(entry -> BusinessAnalyticsResponse.PaymentTrend.builder()
                        .date(entry.getKey())
                        .amount(entry.getValue())
                        .build())
                .collect(Collectors.toList());

        List<BusinessAnalyticsResponse.TopCustomer> topCustomers = paidInvoices.stream()
                .collect(Collectors.groupingBy(
                        invoice -> invoice.getCustomerName() == null || invoice.getCustomerName().isBlank() ? "Unknown Customer" : invoice.getCustomerName(),
                        Collectors.mapping(Invoice::getAmount, Collectors.reducing(BigDecimal.ZERO, BigDecimal::add))))
                .entrySet().stream()
                .sorted(Map.Entry.<String, BigDecimal>comparingByValue(Comparator.reverseOrder()))
                .limit(5)
                .map(entry -> BusinessAnalyticsResponse.TopCustomer.builder()
                        .customer(entry.getKey())
                        .totalPaid(entry.getValue())
                        .build())
                .collect(Collectors.toList());

        return BusinessAnalyticsResponse.builder()
                .totalRevenue(totalRevenue)
                .transactionSummary(BusinessAnalyticsResponse.TransactionSummary.builder()
                        .totalTransactions((long) paidInvoices.size())
                        .totalReceived(totalRevenue)
                        .totalSent(BigDecimal.ZERO)
                        .build())
                .outstandingInvoices(BusinessAnalyticsResponse.OutstandingInvoices.builder()
                        .count((long) outstandingInvoices.size())
                        .totalAmount(totalOutstanding)
                        .build())
                .paymentTrends(paymentTrends)
                .topCustomers(topCustomers)
                .build();
    }

    private boolean isWithinRange(LocalDate date, LocalDate from, LocalDate to) {
        if (date == null) {
            return false;
        }
        if (from != null && date.isBefore(from)) {
            return false;
        }
        return to == null || !date.isAfter(to);
    }
}
