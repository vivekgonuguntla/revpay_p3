package com.revpay.business.service;

import com.revpay.business.client.NotificationServiceClient;
import com.revpay.business.client.WalletServiceClient;
import com.revpay.business.dto.*;
import com.revpay.business.entity.Invoice;
import com.revpay.business.entity.InvoiceItem;
import com.revpay.business.entity.InvoiceStatus;
import com.revpay.business.repository.InvoiceRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class InvoiceService {
    private final InvoiceRepository invoiceRepository;
    private final NotificationServiceClient notificationServiceClient;
    private final WalletServiceClient walletServiceClient;

    public InvoiceService(InvoiceRepository invoiceRepository, NotificationServiceClient notificationServiceClient, WalletServiceClient walletServiceClient) {
        this.invoiceRepository = invoiceRepository;
        this.notificationServiceClient = notificationServiceClient;
        this.walletServiceClient = walletServiceClient;
    }

    @Transactional
    public InvoiceResponse createInvoice(Long userId, InvoiceRequest request) {
        // Calculate total amount
        BigDecimal totalAmount = request.getItems().stream()
                .map(item -> item.getUnitPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Create invoice
        Invoice invoice = Invoice.builder()
                .userId(userId)
                .invoiceNumber(generateInvoiceNumber())
                .customerName(request.getCustomerName())
                .customerEmail(request.getCustomerEmail())
                .customerPhone(request.getCustomerPhone())
                .amount(totalAmount)
                .currency(request.getCurrency())
                .dueDate(request.getDueDate())
                .description(request.getDescription())
                .paymentTerms(request.getPaymentTerms())
                .status(InvoiceStatus.SENT)
                .build();

        // Create invoice items
        List<InvoiceItem> items = request.getItems().stream()
                .map(itemReq -> InvoiceItem.builder()
                        .invoice(invoice)
                        .itemName(itemReq.getItemName())
                        .quantity(itemReq.getQuantity())
                        .unitPrice(itemReq.getUnitPrice())
                        .build())
                .collect(Collectors.toList());

        invoice.setItems(items);
        Invoice saved = invoiceRepository.save(invoice);

        // Send notification
        try {
            NotificationServiceClient.NotificationRequest notification = new NotificationServiceClient.NotificationRequest();
            notification.userId = userId;
            notification.title = "Invoice Created";
            notification.message = "Invoice " + saved.getInvoiceNumber() + " has been created for " + saved.getAmount() + " " + saved.getCurrency();
            notification.type = "INVOICE_CREATED";
            notification.category = "BUSINESS";
            notificationServiceClient.sendNotification(notification);
        } catch (Exception e) {
            // Log error
        }

        return mapToResponse(saved);
    }

    public List<InvoiceResponse> getInvoicesByUserId(Long userId) {
        return invoiceRepository.findByUserIdOrderByCreatedAtDesc(userId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public InvoiceResponse getInvoiceByNumber(String invoiceNumber) {
        Invoice invoice = invoiceRepository.findByInvoiceNumber(invoiceNumber)
                .orElseThrow(() -> new RuntimeException("Invoice not found: " + invoiceNumber));
        return mapToResponse(invoice);
    }

    @Transactional
    public InvoiceResponse payInvoice(String invoiceNumber, Long payerUserId) {
        Invoice invoice = invoiceRepository.findByInvoiceNumber(invoiceNumber)
                .orElseThrow(() -> new RuntimeException("Invoice not found: " + invoiceNumber));

        if (invoice.getStatus() == InvoiceStatus.PAID) {
            throw new RuntimeException("Invoice already paid");
        }

        // Debit from payer
        try {
            WalletServiceClient.WalletTransactionRequest debitRequest = new WalletServiceClient.WalletTransactionRequest();
            debitRequest.userId = payerUserId;
            debitRequest.amount = invoice.getAmount();
            debitRequest.currency = invoice.getCurrency();
            debitRequest.description = "Payment for invoice " + invoiceNumber;
            walletServiceClient.debitWallet(debitRequest);

            // Credit to business
            WalletServiceClient.WalletTransactionRequest creditRequest = new WalletServiceClient.WalletTransactionRequest();
            creditRequest.userId = invoice.getUserId();
            creditRequest.amount = invoice.getAmount();
            creditRequest.currency = invoice.getCurrency();
            creditRequest.description = "Payment received for invoice " + invoiceNumber;
            walletServiceClient.creditWallet(creditRequest);

        } catch (Exception e) {
            throw new RuntimeException("Payment failed: " + e.getMessage());
        }

        // Mark as paid
        invoice.setStatus(InvoiceStatus.PAID);
        invoice.setPaidAt(LocalDateTime.now());
        Invoice saved = invoiceRepository.save(invoice);

        // Send notification
        try {
            NotificationServiceClient.NotificationRequest notification = new NotificationServiceClient.NotificationRequest();
            notification.userId = invoice.getUserId();
            notification.title = "Invoice Paid";
            notification.message = "Invoice " + invoiceNumber + " has been paid.";
            notification.type = "INVOICE_PAID";
            notification.category = "BUSINESS";
            notificationServiceClient.sendNotification(notification);
        } catch (Exception e) {
            // Log error
        }

        return mapToResponse(saved);
    }

    @Transactional
    public void checkOverdueInvoices() {
        List<Invoice> overdueInvoices = invoiceRepository.findByStatusAndDueDateBefore(
                InvoiceStatus.SENT, LocalDate.now());

        for (Invoice invoice : overdueInvoices) {
            invoice.setStatus(InvoiceStatus.OVERDUE);
            invoiceRepository.save(invoice);

            // Send notification
            try {
                NotificationServiceClient.NotificationRequest notification = new NotificationServiceClient.NotificationRequest();
                notification.userId = invoice.getUserId();
                notification.title = "Invoice Overdue";
                notification.message = "Invoice " + invoice.getInvoiceNumber() + " is overdue.";
                notification.type = "INVOICE_OVERDUE";
                notification.category = "BUSINESS";
                notificationServiceClient.sendNotification(notification);
            } catch (Exception e) {
                // Log error
            }
        }
    }

    private String generateInvoiceNumber() {
        return "INV-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    private InvoiceResponse mapToResponse(Invoice invoice) {
        List<InvoiceItemResponse> items = invoice.getItems() != null ? invoice.getItems().stream()
                .map(item -> InvoiceItemResponse.builder()
                        .id(item.getId())
                        .itemName(item.getItemName())
                        .quantity(item.getQuantity())
                        .unitPrice(item.getUnitPrice())
                        .total(item.getUnitPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                        .build())
                .collect(Collectors.toList()) : List.of();

        return InvoiceResponse.builder()
                .id(invoice.getId())
                .invoiceNumber(invoice.getInvoiceNumber())
                .customerName(invoice.getCustomerName())
                .customerEmail(invoice.getCustomerEmail())
                .customerPhone(invoice.getCustomerPhone())
                .amount(invoice.getAmount())
                .currency(invoice.getCurrency())
                .dueDate(invoice.getDueDate())
                .description(invoice.getDescription())
                .paymentTerms(invoice.getPaymentTerms())
                .status(invoice.getStatus())
                .createdAt(invoice.getCreatedAt())
                .paidAt(invoice.getPaidAt())
                .items(items)
                .build();
    }
}
