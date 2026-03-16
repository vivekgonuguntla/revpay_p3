package com.revpay.business.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "loan_repayments")
public class LoanRepayment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "loan_id", nullable = false)
    private BusinessLoan loan;

    @Column(name = "loan_id", insertable = false, updatable = false)
    private Long loanId;

    @Column(nullable = false)
    private LocalDate dueDate;

    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RepaymentStatus status;

    private LocalDateTime paidDate;

    public LoanRepayment() {
    }

    public LoanRepayment(Long id, BusinessLoan loan, LocalDate dueDate, BigDecimal amount, RepaymentStatus status, LocalDateTime paidDate) {
        this.id = id;
        this.loan = loan;
        this.dueDate = dueDate;
        this.amount = amount;
        this.status = status;
        this.paidDate = paidDate;
    }

    public static LoanRepaymentBuilder builder() {
        return new LoanRepaymentBuilder();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public BusinessLoan getLoan() {
        return loan;
    }

    public void setLoan(BusinessLoan loan) {
        this.loan = loan;
    }

    public Long getLoanId() {
        return loanId;
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

    @PrePersist
    public void onCreate() {
        if (status == null) {
            status = RepaymentStatus.PENDING;
        }
    }

    public static class LoanRepaymentBuilder {
        private Long id;
        private BusinessLoan loan;
        private LocalDate dueDate;
        private BigDecimal amount;
        private RepaymentStatus status;
        private LocalDateTime paidDate;

        public LoanRepaymentBuilder id(Long id) {
            this.id = id;
            return this;
        }

        public LoanRepaymentBuilder loan(BusinessLoan loan) {
            this.loan = loan;
            return this;
        }

        public LoanRepaymentBuilder dueDate(LocalDate dueDate) {
            this.dueDate = dueDate;
            return this;
        }

        public LoanRepaymentBuilder amount(BigDecimal amount) {
            this.amount = amount;
            return this;
        }

        public LoanRepaymentBuilder status(RepaymentStatus status) {
            this.status = status;
            return this;
        }

        public LoanRepaymentBuilder paidDate(LocalDateTime paidDate) {
            this.paidDate = paidDate;
            return this;
        }

        public LoanRepayment build() {
            return new LoanRepayment(id, loan, dueDate, amount, status, paidDate);
        }
    }
}
