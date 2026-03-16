package com.revpay.business.service;

import com.revpay.business.client.AuthServiceClient;
import com.revpay.business.client.NotificationServiceClient;
import com.revpay.business.dto.InvoiceItemRequest;
import com.revpay.business.dto.InvoicePaymentRequest;
import com.revpay.business.dto.InvoiceRequest;
import com.revpay.business.dto.InvoiceResponse;
import com.revpay.business.entity.Invoice;
import com.revpay.business.entity.InvoiceItem;
import com.revpay.business.entity.InvoiceStatus;
import com.revpay.business.exception.BusinessException;
import com.revpay.business.exception.ResourceNotFoundException;
import com.revpay.business.repository.InvoiceRepository;
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
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class InvoiceServiceTest {

    @Mock
    private InvoiceRepository invoiceRepository;

    @Mock
    private NotificationServiceClient notificationServiceClient;

    @Mock
    private AuthServiceClient authServiceClient;

    @InjectMocks
    private InvoiceService invoiceService;

    @Test
    void createInvoiceCalculatesTotalAndCreatesCustomerNotificationWhenCustomerResolved() {
        InvoiceRequest request = new InvoiceRequest();
        request.setCustomerName("Alice");
        request.setCustomerEmail("alice@example.com");
        request.setCurrency("USD");
        request.setDueDate(LocalDate.now().plusDays(10));
        request.setItems(List.of(
                item("Plan A", 2, "100"),
                item("Plan B", 1, "50")
        ));

        when(authServiceClient.getUserByEmail("alice@example.com")).thenReturn(Map.of("id", 99L));
        when(invoiceRepository.save(any(Invoice.class))).thenAnswer(invocation -> {
            Invoice invoice = invocation.getArgument(0);
            invoice.setId(15L);
            invoice.setCreatedAt(LocalDateTime.now());
            return invoice;
        });

        InvoiceResponse response = invoiceService.createInvoice(8L, request);

        ArgumentCaptor<Invoice> captor = ArgumentCaptor.forClass(Invoice.class);
        verify(invoiceRepository).save(captor.capture());
        Invoice saved = captor.getValue();
        assertEquals(new BigDecimal("250"), saved.getAmount());
        assertEquals(2, saved.getItems().size());
        assertEquals(15L, response.getId());
        verify(notificationServiceClient, atLeastOnce())
                .sendNotification(any(NotificationServiceClient.NotificationRequest.class));
    }

    @Test
    void lookupInvoicesThrowsForBlankLookupValue() {
        BusinessException exception = assertThrows(BusinessException.class,
                () -> invoiceService.lookupInvoices("EMAIL", " "));

        assertEquals("Lookup value is required", exception.getMessage());
    }

    @Test
    void payInvoiceMarksLatestUnpaidMatchAsPaid() {
        Invoice sentInvoice = Invoice.builder()
                .id(3L)
                .userId(8L)
                .invoiceNumber("INV-1")
                .customerEmail("alice@example.com")
                .customerName("Alice")
                .amount(new BigDecimal("200"))
                .currency("USD")
                .status(InvoiceStatus.SENT)
                .createdAt(LocalDateTime.of(2026, 3, 10, 10, 0))
                .dueDate(LocalDate.now().plusDays(5))
                .items(List.of())
                .build();

        when(invoiceRepository.findByStatusAndDueDateBefore(eq(InvoiceStatus.SENT), any(LocalDate.class))).thenReturn(List.of());
        when(invoiceRepository.findByCustomerEmailContainingIgnoreCase("alice@example.com")).thenReturn(List.of(sentInvoice));
        when(invoiceRepository.findById(3L)).thenReturn(Optional.of(sentInvoice));
        when(invoiceRepository.save(sentInvoice)).thenReturn(sentInvoice);

        InvoicePaymentRequest request = new InvoicePaymentRequest();
        request.setLookupType("EMAIL");
        request.setLookupValue("alice@example.com");

        InvoiceResponse response = invoiceService.payInvoice(request, 22L);

        assertEquals(InvoiceStatus.PAID, response.getStatus());
        assertNotNull(response.getPaidAt());
    }

    @Test
    void payInvoiceThrowsWhenNoInvoiceMatchesLookup() {
        when(invoiceRepository.findByStatusAndDueDateBefore(eq(InvoiceStatus.SENT), any(LocalDate.class))).thenReturn(List.of());
        when(invoiceRepository.findByCustomerPhoneContainingIgnoreCase("9999")).thenReturn(List.of());

        InvoicePaymentRequest request = new InvoicePaymentRequest();
        request.setLookupType("PHONE");
        request.setLookupValue("9999");

        assertThrows(ResourceNotFoundException.class, () -> invoiceService.payInvoice(request, 22L));
    }

    @Test
    void checkOverdueInvoicesMarksSentInvoicesAsOverdue() {
        Invoice overdueInvoice = Invoice.builder()
                .id(9L)
                .userId(8L)
                .invoiceNumber("INV-9")
                .customerName("Alice")
                .amount(new BigDecimal("80"))
                .status(InvoiceStatus.SENT)
                .dueDate(LocalDate.now().minusDays(1))
                .build();

        when(invoiceRepository.findByStatusAndDueDateBefore(eq(InvoiceStatus.SENT), any(LocalDate.class)))
                .thenReturn(List.of(overdueInvoice));
        when(invoiceRepository.save(overdueInvoice)).thenReturn(overdueInvoice);

        invoiceService.checkOverdueInvoices();

        assertEquals(InvoiceStatus.OVERDUE, overdueInvoice.getStatus());
        verify(invoiceRepository).save(overdueInvoice);
    }

    private InvoiceItemRequest item(String name, int quantity, String unitPrice) {
        InvoiceItemRequest request = new InvoiceItemRequest();
        request.setItemName(name);
        request.setQuantity(quantity);
        request.setUnitPrice(new BigDecimal(unitPrice));
        return request;
    }
}
