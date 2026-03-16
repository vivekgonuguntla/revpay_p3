package com.revpay.business.service;

import com.revpay.business.client.NotificationServiceClient;
import com.revpay.business.client.AuthServiceClient;
import com.revpay.business.dto.InvoiceItemResponse;
import com.revpay.business.dto.InvoicePaymentRequest;
import com.revpay.business.dto.InvoiceRequest;
import com.revpay.business.dto.InvoiceResponse;
import com.revpay.business.entity.Invoice;
import com.revpay.business.entity.InvoiceItem;
import com.revpay.business.entity.InvoiceStatus;
import com.revpay.business.exception.BusinessException;
import com.revpay.business.exception.ResourceNotFoundException;
import com.revpay.business.repository.InvoiceRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class InvoiceService {
    private final InvoiceRepository invoiceRepository;
    private final NotificationServiceClient notificationServiceClient;
    private final AuthServiceClient authServiceClient;

    public InvoiceService(InvoiceRepository invoiceRepository,
                          NotificationServiceClient notificationServiceClient,
                          AuthServiceClient authServiceClient) {
        this.invoiceRepository = invoiceRepository;
        this.notificationServiceClient = notificationServiceClient;
        this.authServiceClient = authServiceClient;
    }

    @Transactional
    public InvoiceResponse createInvoice(Long userId, InvoiceRequest request) {
        BigDecimal totalAmount = request.getItems().stream()
                .map(item -> item.getUnitPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        Invoice invoice = Invoice.builder()
                .userId(userId)
                .invoiceNumber(generateInvoiceNumber())
                .customerName(request.getCustomerName())
                .customerEmail(request.getCustomerEmail())
                .customerPhone(request.getCustomerPhone())
                .customerId(request.getCustomerId())
                .amount(totalAmount)
                .currency(request.getCurrency())
                .dueDate(request.getDueDate())
                .description(request.getDescription())
                .paymentTerms(request.getPaymentTerms())
                .status(InvoiceStatus.SENT)
                .build();

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

        try {
            NotificationServiceClient.NotificationRequest notification = new NotificationServiceClient.NotificationRequest();
            notification.userId = userId;
            notification.category = "ALERTS";
            notification.title = "Invoice Created";
            notification.message = "Invoice " + saved.getInvoiceNumber() + " has been created.";
            notification.type = "INVOICE_CREATED";
            notification.amount = saved.getAmount();
            notification.counterparty = saved.getCustomerName();
            notification.eventStatus = saved.getStatus().name();
            notification.navigationTarget = "/business/invoices/" + saved.getId();
            notification.eventTime = saved.getCreatedAt();
            notificationServiceClient.sendNotification(notification);
        } catch (Exception ignored) {
        }

        notifyCustomer(saved);

        return mapToResponse(saved);
    }

    public List<InvoiceResponse> getInvoicesByUserId(Long userId, InvoiceStatus status) {
        checkOverdueInvoices();
        List<Invoice> invoices = status == null
                ? invoiceRepository.findByUserIdOrderByCreatedAtDesc(userId)
                : invoiceRepository.findByUserIdAndStatusOrderByCreatedAtDesc(userId, status);
        return invoices.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public List<InvoiceResponse> lookupInvoices(String type, String value) {
        String lookupType = type == null ? "" : type.trim().toUpperCase(Locale.ROOT);
        String lookupValue = value == null ? "" : value.trim();

        if (lookupValue.isBlank()) {
            throw new BusinessException("Lookup value is required");
        }

        checkOverdueInvoices();

        List<Invoice> invoices = switch (lookupType) {
            case "INVOICE_NUMBER" -> invoiceRepository.findByInvoiceNumberContainingIgnoreCase(lookupValue);
            case "PHONE" -> invoiceRepository.findByCustomerPhoneContainingIgnoreCase(lookupValue);
            case "EMAIL" -> invoiceRepository.findByCustomerEmailContainingIgnoreCase(lookupValue);
            case "CUSTOMER_ID" -> invoiceRepository.findByCustomerIdContainingIgnoreCase(lookupValue);
            default -> throw new BusinessException("Unsupported lookup type: " + type);
        };

        return invoices.stream()
                .sorted(Comparator.comparing(Invoice::getCreatedAt, Comparator.nullsLast(Comparator.reverseOrder())))
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public InvoiceResponse payInvoice(InvoicePaymentRequest request, Long payerUserId) {
        Invoice invoice = resolveInvoiceForPayment(request);

        if (invoice.getStatus() == InvoiceStatus.PAID) {
            throw new BusinessException("Invoice already paid");
        }

        invoice.setStatus(InvoiceStatus.PAID);
        invoice.setPaidAt(LocalDateTime.now());
        Invoice saved = invoiceRepository.save(invoice);

        try {
            NotificationServiceClient.NotificationRequest notification = new NotificationServiceClient.NotificationRequest();
            notification.userId = invoice.getUserId();
            notification.category = "ALERTS";
            notification.title = "Invoice Paid";
            notification.message = "Invoice " + invoice.getInvoiceNumber() + " has been paid.";
            notification.type = "INVOICE_PAID";
            notification.amount = invoice.getAmount();
            notification.counterparty = invoice.getCustomerName();
            notification.eventStatus = "PAID";
            notification.navigationTarget = "/business/invoices/" + invoice.getId();
            notification.eventTime = saved.getPaidAt();
            notificationServiceClient.sendNotification(notification);
        } catch (Exception ignored) {
        }

        return mapToResponse(saved);
    }

    @Transactional
    public void checkOverdueInvoices() {
        List<Invoice> overdueInvoices = invoiceRepository.findByStatusAndDueDateBefore(InvoiceStatus.SENT, LocalDate.now());
        for (Invoice invoice : overdueInvoices) {
            invoice.setStatus(InvoiceStatus.OVERDUE);
            Invoice saved = invoiceRepository.save(invoice);
            try {
                NotificationServiceClient.NotificationRequest notification = new NotificationServiceClient.NotificationRequest();
                notification.userId = saved.getUserId();
                notification.category = "ALERTS";
                notification.title = "Invoice Overdue";
                notification.message = "Invoice " + saved.getInvoiceNumber() + " is overdue.";
                notification.type = "INVOICE_OVERDUE";
                notification.amount = saved.getAmount();
                notification.counterparty = saved.getCustomerName();
                notification.eventStatus = "OVERDUE";
                notification.navigationTarget = "/business/invoices/" + saved.getId();
                notification.eventTime = java.time.LocalDateTime.now();
                notificationServiceClient.sendNotification(notification);
            } catch (Exception ignored) {
            }
        }
    }

    private String generateInvoiceNumber() {
        return "INV-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase(Locale.ROOT);
    }

    private InvoiceResponse mapToResponse(Invoice invoice) {
        List<InvoiceItemResponse> items = invoice.getItems() == null ? List.of() : invoice.getItems().stream()
                .map(item -> InvoiceItemResponse.builder()
                        .id(item.getId())
                        .itemName(item.getItemName())
                        .quantity(item.getQuantity())
                        .unitPrice(item.getUnitPrice())
                        .total(item.getUnitPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                        .build())
                .collect(Collectors.toList());

        return InvoiceResponse.builder()
                .id(invoice.getId())
                .invoiceNumber(invoice.getInvoiceNumber())
                .customerName(invoice.getCustomerName())
                .customerEmail(invoice.getCustomerEmail())
                .customerPhone(invoice.getCustomerPhone())
                .customerId(invoice.getCustomerId())
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

    private Invoice resolveInvoiceForPayment(InvoicePaymentRequest request) {
        List<InvoiceResponse> matches = lookupInvoices(request.getLookupType(), request.getLookupValue());
        if (matches.isEmpty()) {
            throw new ResourceNotFoundException("Invoice not found");
        }

        InvoiceResponse selected = matches.stream()
                .filter(match -> match.getStatus() != InvoiceStatus.PAID)
                .max(Comparator.comparing(InvoiceResponse::getCreatedAt, Comparator.nullsLast(Comparator.naturalOrder())))
                .orElse(matches.get(0));

        return invoiceRepository.findById(selected.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Invoice not found"));
    }

    private void notifyCustomer(Invoice invoice) {
        Long customerUserId = resolveCustomerUserId(invoice);
        if (customerUserId == null) {
            return;
        }

        try {
            NotificationServiceClient.NotificationRequest notification = new NotificationServiceClient.NotificationRequest();
            notification.userId = customerUserId;
            notification.category = "ALERTS";
            notification.title = "New Invoice Received";
            notification.message = "You received invoice " + invoice.getInvoiceNumber() + " for " + invoice.getAmount() + " " + invoice.getCurrency() + ".";
            notification.type = "INVOICE_RECEIVED";
            notification.amount = invoice.getAmount();
            notification.counterparty = invoice.getCustomerName();
            notification.eventStatus = invoice.getStatus().name();
            notification.navigationTarget = "/business/invoices/" + invoice.getId();
            notification.eventTime = invoice.getCreatedAt();
            notificationServiceClient.sendNotification(notification);
        } catch (Exception ignored) {
        }
    }

    private Long resolveCustomerUserId(Invoice invoice) {
        if (invoice.getCustomerEmail() != null && !invoice.getCustomerEmail().isBlank()) {
            try {
                Map<String, Object> user = authServiceClient.getUserByEmail(invoice.getCustomerEmail());
                Object id = user.get("id");
                if (id instanceof Number number) {
                    return number.longValue();
                }
            } catch (Exception ignored) {
            }
        }

        if (invoice.getCustomerId() != null && !invoice.getCustomerId().isBlank()) {
            try {
                Long numericId = Long.valueOf(invoice.getCustomerId().trim());
                Map<String, Object> user = authServiceClient.getUserById(numericId);
                Object id = user.get("id");
                if (id instanceof Number number) {
                    return number.longValue();
                }
            } catch (Exception ignored) {
            }
        }

        return null;
    }
}
