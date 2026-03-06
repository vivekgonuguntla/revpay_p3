package com.revpay.business.dto;

import com.revpay.business.entity.RepaymentStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class LoanRepaymentResponse {
    private Long id;
    private Long loanId;
    private LocalDate dueDate;
    private BigDecimal amount;
    private RepaymentStatus status;
    private LocalDateTime paidDate;

    public LoanRepaymentResponse() {
    }

    public LoanRepaymentResponse(Long id, Long loanId, LocalDate dueDate, BigDecimal amount, RepaymentStatus status, LocalDateTime paidDate) {
        this.id = id;
        this.loanId = loanId;
        this.dueDate = dueDate;
        this.amount = amount;
        this.status = status;
        this.paidDate = paidDate;
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

    public Long getLoanId() {
        return loanId;
    }

    public void setLoanId(Long loanId) {
        this.loanId = loanId;
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

    public LocalDateTime getPaidDate() {
        return paidDate;
    }

    public void setPaidDate(LocalDateTime paidDate) {
        this.paidDate = paidDate;
    }

    public static class LoanRepaymentResponseBuilder {
        private Long id;
        private Long loanId;
        private LocalDate dueDate;
        private BigDecimal amount;
        private RepaymentStatus status;
        private LocalDateTime paidDate;

        public LoanRepaymentResponseBuilder id(Long id) {
            this.id = id;
            return this;
        }

        public LoanRepaymentResponseBuilder loanId(Long loanId) {
            this.loanId = loanId;
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

        public LoanRepaymentResponseBuilder paidDate(LocalDateTime paidDate) {
            this.paidDate = paidDate;
            return this;
        }

        public LoanRepaymentResponse build() {
            return new LoanRepaymentResponse(id, loanId, dueDate, amount, status, paidDate);
        }
    }
}
