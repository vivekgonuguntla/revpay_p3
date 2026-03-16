package com.revpay.business.service;

import com.revpay.business.dto.BusinessAnalyticsResponse;
import com.revpay.business.entity.Invoice;
import com.revpay.business.entity.InvoiceStatus;
import com.revpay.business.repository.InvoiceRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AnalyticsServiceTest {

    @Mock
    private InvoiceRepository invoiceRepository;

    @InjectMocks
    private AnalyticsService analyticsService;

    @Test
    void getAnalyticsBuildsRevenueOutstandingTrendsAndTopCustomers() {
        Invoice paidA = Invoice.builder()
                .id(1L)
                .userId(8L)
                .customerName("Alice")
                .amount(new BigDecimal("100"))
                .status(InvoiceStatus.PAID)
                .createdAt(LocalDateTime.of(2026, 3, 1, 10, 0))
                .paidAt(LocalDateTime.of(2026, 3, 2, 10, 0))
                .build();
        Invoice paidB = Invoice.builder()
                .id(2L)
                .userId(8L)
                .customerName("Bob")
                .amount(new BigDecimal("250"))
                .status(InvoiceStatus.PAID)
                .createdAt(LocalDateTime.of(2026, 3, 3, 10, 0))
                .paidAt(LocalDateTime.of(2026, 3, 4, 10, 0))
                .build();
        Invoice overdue = Invoice.builder()
                .id(3L)
                .userId(8L)
                .customerName("Charlie")
                .amount(new BigDecimal("80"))
                .status(InvoiceStatus.OVERDUE)
                .createdAt(LocalDateTime.of(2026, 3, 5, 10, 0))
                .build();

        when(invoiceRepository.findByUserIdOrderByCreatedAtDesc(8L)).thenReturn(List.of(paidA, paidB, overdue));

        BusinessAnalyticsResponse response =
                analyticsService.getAnalytics(8L, LocalDate.of(2026, 3, 1), LocalDate.of(2026, 3, 31));

        assertEquals(new BigDecimal("350"), response.getTotalRevenue());
        assertEquals(2L, response.getTransactionSummary().getTotalTransactions());
        assertEquals(1L, response.getOutstandingInvoices().getCount());
        assertEquals(new BigDecimal("80"), response.getOutstandingInvoices().getTotalAmount());
        assertEquals(2, response.getPaymentTrends().size());
        assertEquals("Bob", response.getTopCustomers().get(0).getCustomer());
    }

    @Test
    void getAnalyticsExcludesInvoicesOutsideDateRange() {
        Invoice oldPaid = Invoice.builder()
                .userId(8L)
                .customerName("Alice")
                .amount(new BigDecimal("100"))
                .status(InvoiceStatus.PAID)
                .createdAt(LocalDateTime.of(2026, 2, 28, 10, 0))
                .paidAt(LocalDateTime.of(2026, 2, 28, 10, 0))
                .build();

        when(invoiceRepository.findByUserIdOrderByCreatedAtDesc(8L)).thenReturn(List.of(oldPaid));

        BusinessAnalyticsResponse response =
                analyticsService.getAnalytics(8L, LocalDate.of(2026, 3, 1), LocalDate.of(2026, 3, 31));

        assertEquals(BigDecimal.ZERO, response.getTotalRevenue());
        assertEquals(0L, response.getTransactionSummary().getTotalTransactions());
    }
}
