package com.revpay.business.controller;

import com.revpay.business.dto.InvoiceRequest;
import com.revpay.business.dto.InvoiceResponse;
import com.revpay.business.dto.InvoicePaymentRequest;
import com.revpay.business.entity.InvoiceStatus;
import com.revpay.business.service.InvoiceService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/business/invoices")
public class InvoiceController {
    private final InvoiceService invoiceService;

    public InvoiceController(InvoiceService invoiceService) {
        this.invoiceService = invoiceService;
    }

    @PostMapping
    public ResponseEntity<InvoiceResponse> createInvoice(
            @RequestHeader("X-User-Id") Long userId,
            @Valid @RequestBody InvoiceRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(invoiceService.createInvoice(userId, request));
    }

    @GetMapping
    public ResponseEntity<List<InvoiceResponse>> getInvoicesByUserId(
            @RequestHeader("X-User-Id") Long userId,
            @RequestParam(required = false) InvoiceStatus status) {
        return ResponseEntity.ok(invoiceService.getInvoicesByUserId(userId, status));
    }

    @GetMapping("/lookup")
    public ResponseEntity<List<InvoiceResponse>> lookupInvoices(
            @RequestParam String type,
            @RequestParam String value) {
        return ResponseEntity.ok(invoiceService.lookupInvoices(type, value));
    }

    @PostMapping("/pay")
    public ResponseEntity<InvoiceResponse> payInvoice(
            @RequestHeader("X-User-Id") Long payerUserId,
            @Valid @RequestBody InvoicePaymentRequest request) {
        return ResponseEntity.ok(invoiceService.payInvoice(request, payerUserId));
    }
}
