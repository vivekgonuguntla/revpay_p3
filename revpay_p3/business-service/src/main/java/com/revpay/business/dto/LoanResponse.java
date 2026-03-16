package com.revpay.business.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.revpay.business.entity.LoanStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class LoanResponse {
    private Long id;
    private BigDecimal loanAmount;
    private String purpose;
    private String financialDetails;
    private String supportingDocsPath;
    private LoanStatus status;
    private Integer termMonths;
    private BigDecimal remainingBalance;
    private LocalDateTime appliedAt;
    private List<LoanRepaymentResponse> repayments;

    public LoanResponse() {
    }

    public LoanResponse(Long id, BigDecimal loanAmount, String purpose, String financialDetails, String supportingDocsPath, LoanStatus status, Integer termMonths, BigDecimal remainingBalance, LocalDateTime appliedAt, List<LoanRepaymentResponse> repayments) {
        this.id = id;
        this.loanAmount = loanAmount;
        this.purpose = purpose;
        this.financialDetails = financialDetails;
        this.supportingDocsPath = supportingDocsPath;
        this.status = status;
        this.termMonths = termMonths;
        this.remainingBalance = remainingBalance;
        this.appliedAt = appliedAt;
        this.repayments = repayments;
    }

    public static LoanResponseBuilder builder() {
        return new LoanResponseBuilder();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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

    @JsonProperty("supportingDocumentsPath")
    public String getSupportingDocumentsPath() {
        return supportingDocsPath;
    }

    public void setSupportingDocsPath(String supportingDocsPath) {
        this.supportingDocsPath = supportingDocsPath;
    }

    public LoanStatus getStatus() {
        return status;
    }

    public void setStatus(LoanStatus status) {
        this.status = status;
    }

    public Integer getTermMonths() {
        return termMonths;
    }

    public void setTermMonths(Integer termMonths) {
        this.termMonths = termMonths;
    }

    public BigDecimal getRemainingBalance() {
        return remainingBalance;
    }

    public void setRemainingBalance(BigDecimal remainingBalance) {
        this.remainingBalance = remainingBalance;
    }

    public LocalDateTime getAppliedAt() {
        return appliedAt;
    }

    public void setAppliedAt(LocalDateTime appliedAt) {
        this.appliedAt = appliedAt;
    }

    public List<LoanRepaymentResponse> getRepayments() {
        return repayments;
    }

    public void setRepayments(List<LoanRepaymentResponse> repayments) {
        this.repayments = repayments;
    }

    public static class LoanResponseBuilder {
        private Long id;
        private BigDecimal loanAmount;
        private String purpose;
        private String financialDetails;
        private String supportingDocsPath;
        private LoanStatus status;
        private Integer termMonths;
        private BigDecimal remainingBalance;
        private LocalDateTime appliedAt;
        private List<LoanRepaymentResponse> repayments;

        public LoanResponseBuilder id(Long id) {
            this.id = id;
            return this;
        }

        public LoanResponseBuilder loanAmount(BigDecimal loanAmount) {
            this.loanAmount = loanAmount;
            return this;
        }

        public LoanResponseBuilder purpose(String purpose) {
            this.purpose = purpose;
            return this;
        }

        public LoanResponseBuilder financialDetails(String financialDetails) {
            this.financialDetails = financialDetails;
            return this;
        }

        public LoanResponseBuilder supportingDocsPath(String supportingDocsPath) {
            this.supportingDocsPath = supportingDocsPath;
            return this;
        }

        public LoanResponseBuilder status(LoanStatus status) {
            this.status = status;
            return this;
        }

        public LoanResponseBuilder termMonths(Integer termMonths) {
            this.termMonths = termMonths;
            return this;
        }

        public LoanResponseBuilder remainingBalance(BigDecimal remainingBalance) {
            this.remainingBalance = remainingBalance;
            return this;
        }

        public LoanResponseBuilder appliedAt(LocalDateTime appliedAt) {
            this.appliedAt = appliedAt;
            return this;
        }

        public LoanResponseBuilder repayments(List<LoanRepaymentResponse> repayments) {
            this.repayments = repayments;
            return this;
        }

        public LoanResponse build() {
            return new LoanResponse(id, loanAmount, purpose, financialDetails, supportingDocsPath, status, termMonths, remainingBalance, appliedAt, repayments);
        }
    }
}
