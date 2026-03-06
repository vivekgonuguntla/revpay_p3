package com.revpay.business.dto;

import jakarta.validation.constraints.NotBlank;

public class BusinessVerificationRequest {
    @NotBlank
    private String businessName;

    @NotBlank
    private String businessType;

    private String taxId;

    private String businessAddress;

    private String verificationDocsPath;

    public String getBusinessName() {
        return businessName;
    }

    public void setBusinessName(String businessName) {
        this.businessName = businessName;
    }

    public String getBusinessType() {
        return businessType;
    }

    public void setBusinessType(String businessType) {
        this.businessType = businessType;
    }

    public String getTaxId() {
        return taxId;
    }

    public void setTaxId(String taxId) {
        this.taxId = taxId;
    }

    public String getBusinessAddress() {
        return businessAddress;
    }

    public void setBusinessAddress(String businessAddress) {
        this.businessAddress = businessAddress;
    }

    public String getVerificationDocsPath() {
        return verificationDocsPath;
    }

    public void setVerificationDocsPath(String verificationDocsPath) {
        this.verificationDocsPath = verificationDocsPath;
    }
}
