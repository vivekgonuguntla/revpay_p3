package com.revpay.business.controller;

import com.revpay.business.dto.InvoiceRequest;
import com.revpay.business.dto.InvoiceResponse;
import com.revpay.business.service.InvoiceService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/business/invoices")
public class InvoiceController {
    private final InvoiceService invoiceService;

    public InvoiceController(InvoiceService invoiceService) {
        this.invoiceService = invoiceService;
    }

    @PostMapping("/user/{userId}")
    public ResponseEntity<InvoiceResponse> createInvoice(
            @PathVariable Long userId,
            @Valid @RequestBody InvoiceRequest request) {
        return ResponseEntity.ok(invoiceService.createInvoice(userId, request));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<InvoiceResponse>> getInvoicesByUserId(@PathVariable Long userId) {
        return ResponseEntity.ok(invoiceService.getInvoicesByUserId(userId));
    }

    @GetMapping("/number/{invoiceNumber}")
    public ResponseEntity<InvoiceResponse> getInvoiceByNumber(@PathVariable String invoiceNumber) {
        return ResponseEntity.ok(invoiceService.getInvoiceByNumber(invoiceNumber));
    }

    @PostMapping("/{invoiceNumber}/pay")
    public ResponseEntity<InvoiceResponse> payInvoice(
            @PathVariable String invoiceNumber,
            @RequestParam Long payerUserId) {
        return ResponseEntity.ok(invoiceService.payInvoice(invoiceNumber, payerUserId));
    }
}
