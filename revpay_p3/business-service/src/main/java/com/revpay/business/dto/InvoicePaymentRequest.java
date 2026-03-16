package com.revpay.business.dto;

import jakarta.validation.constraints.NotBlank;

public class InvoicePaymentRequest {
    @NotBlank
    private String lookupType;

    @NotBlank
    private String lookupValue;

    public String getLookupType() {
        return lookupType;
    }

    public void setLookupType(String lookupType) {
        this.lookupType = lookupType;
    }

    public String getLookupValue() {
        return lookupValue;
    }

    public void setLookupValue(String lookupValue) {
        this.lookupValue = lookupValue;
    }
}
