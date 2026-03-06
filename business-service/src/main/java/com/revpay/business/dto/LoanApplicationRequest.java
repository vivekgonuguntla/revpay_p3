package com.revpay.business.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public class LoanApplicationRequest {
    @NotNull
    private BigDecimal loanAmount;

    @NotBlank
    private String purpose;

    @NotBlank
    private String financialDetails;

    private String supportingDocsPath;

    @NotNull
    @Min(1)
    private Integer termMonths;

    public BigDecimal getLoanAmount() {
        return loanAmount;
    }

    public void setLoanAmount(BigDecimal loanAmount) {
        this.loanAmount = loanAmount;
    }

    public String getPurpose() {
        return purpose;
    }

    public void setPurpose(String purpose) {
        this.purpose = purpose;
    }

    public String getFinancialDetails() {
        return financialDetails;
    }

    public void setFinancialDetails(String financialDetails) {
        this.financialDetails = financialDetails;
    }

    public String getSupportingDocsPath() {
        return supportingDocsPath;
    }

    public void setSupportingDocsPath(String supportingDocsPath) {
        this.supportingDocsPath = supportingDocsPath;
    }

    public Integer getTermMonths() {
        return termMonths;
    }

    public void setTermMonths(Integer termMonths) {
        this.termMonths = termMonths;
    }
}
