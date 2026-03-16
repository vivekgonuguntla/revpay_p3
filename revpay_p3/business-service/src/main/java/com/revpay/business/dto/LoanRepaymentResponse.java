package com.revpay.business.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.revpay.business.entity.RepaymentStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class LoanRepaymentResponse {
    private Long id;
    private Integer installmentNumber;
    private LocalDate dueDate;
    private BigDecimal amount;
    private RepaymentStatus status;
    private LocalDateTime paidAt;

    public LoanRepaymentResponse() {
    }

    public LoanRepaymentResponse(Long id, Integer installmentNumber, LocalDate dueDate, BigDecimal amount, RepaymentStatus status, LocalDateTime paidAt) {
        this.id = id;
        this.installmentNumber = installmentNumber;
        this.dueDate = dueDate;
        this.amount = amount;
        this.status = status;
        this.paidAt = paidAt;
    }

    public static LoanRepaymentResponseBuilder builder() {
        return new LoanRepaymentResponseBuilder();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getInstallmentNumber() {
        return installmentNumber;
    }

    public void setInstallmentNumber(Integer installmentNumber) {
        this.installmentNumber = installmentNumber;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public RepaymentStatus getStatus() {
        return status;
    }

    public void setStatus(RepaymentStatus status) {
        this.status = status;
    }

    public LocalDateTime getPaidAt() {
        return paidAt;
    }

    @JsonProperty("paidDate")
    public LocalDateTime getPaidDate() {
        return paidAt;
    }

    public void setPaidAt(LocalDateTime paidAt) {
        this.paidAt = paidAt;
    }

    public static class LoanRepaymentResponseBuilder {
        private Long id;
        private Integer installmentNumber;
        private LocalDate dueDate;
        private BigDecimal amount;
        private RepaymentStatus status;
        private LocalDateTime paidAt;

        public LoanRepaymentResponseBuilder id(Long id) {
            this.id = id;
            return this;
        }

        public LoanRepaymentResponseBuilder installmentNumber(Integer installmentNumber) {
            this.installmentNumber = installmentNumber;
            return this;
        }

        public LoanRepaymentResponseBuilder dueDate(LocalDate dueDate) {
            this.dueDate = dueDate;
            return this;
        }

        public LoanRepaymentResponseBuilder amount(BigDecimal amount) {
            this.amount = amount;
            return this;
        }

        public LoanRepaymentResponseBuilder status(RepaymentStatus status) {
            this.status = status;
            return this;
        }

        public LoanRepaymentResponseBuilder paidAt(LocalDateTime paidAt) {
            this.paidAt = paidAt;
            return this;
        }

        public LoanRepaymentResponse build() {
            return new LoanRepaymentResponse(id, installmentNumber, dueDate, amount, status, paidAt);
        }
    }
}
