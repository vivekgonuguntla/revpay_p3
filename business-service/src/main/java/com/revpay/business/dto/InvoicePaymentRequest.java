package com.revpay.business.dto;

import jakarta.validation.constraints.NotBlank;

public class InvoicePaymentRequest {
    @NotBlank
    private String invoiceNumber;

    private String paymentMethod;

    public String getInvoiceNumber() {
        return invoiceNumber;
    }

    public void setInvoiceNumber(String invoiceNumber) {
        this.invoiceNumber = invoiceNumber;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }
}
