package com.revpay.business.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "business_loans")
public class BusinessLoan {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal loanAmount;

    @Column(length = 2000, nullable = false)
    private String purpose;

    @Column(length = 4000, nullable = false)
    private String financialDetails;

    @Column(length = 2000)
    private String supportingDocsPath;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private LoanStatus status;

    @Column(nullable = false)
    private Integer termMonths;

    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal remainingBalance;

    private LocalDateTime appliedAt;

    @OneToMany(mappedBy = "loan", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<LoanRepayment> repayments;

    public BusinessLoan() {
    }

    public BusinessLoan(Long id, Long userId, BigDecimal loanAmount, String purpose, String financialDetails, String supportingDocsPath, LoanStatus status, Integer termMonths, BigDecimal remainingBalance, LocalDateTime appliedAt, List<LoanRepayment> repayments) {
        this.id = id;
        this.userId = userId;
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

    public static BusinessLoanBuilder builder() {
        return new BusinessLoanBuilder();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
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

    public List<LoanRepayment> getRepayments() {
        return repayments;
    }

    public void setRepayments(List<LoanRepayment> repayments) {
        this.repayments = repayments;
    }

    @PrePersist
    public void onCreate() {
        if (appliedAt == null) {
            appliedAt = LocalDateTime.now();
        }
        if (status == null) {
            status = LoanStatus.SUBMITTED;
        }
        if (remainingBalance == null) {
            remainingBalance = loanAmount;
        }
    }

    public static class BusinessLoanBuilder {
        private Long id;
        private Long userId;
        private BigDecimal loanAmount;
        private String purpose;
        private String financialDetails;
        private String supportingDocsPath;
        private LoanStatus status;
        private Integer termMonths;
        private BigDecimal remainingBalance;
        private LocalDateTime appliedAt;
        private List<LoanRepayment> repayments;

        public BusinessLoanBuilder id(Long id) {
            this.id = id;
            return this;
        }

        public BusinessLoanBuilder userId(Long userId) {
            this.userId = userId;
            return this;
        }

        public BusinessLoanBuilder loanAmount(BigDecimal loanAmount) {
            this.loanAmount = loanAmount;
            return this;
        }

        public BusinessLoanBuilder purpose(String purpose) {
            this.purpose = purpose;
            return this;
        }

        public BusinessLoanBuilder financialDetails(String financialDetails) {
            this.financialDetails = financialDetails;
            return this;
        }

        public BusinessLoanBuilder supportingDocsPath(String supportingDocsPath) {
            this.supportingDocsPath = supportingDocsPath;
            return this;
        }

        public BusinessLoanBuilder status(LoanStatus status) {
            this.status = status;
            return this;
        }

        public BusinessLoanBuilder termMonths(Integer termMonths) {
            this.termMonths = termMonths;
            return this;
        }

        public BusinessLoanBuilder remainingBalance(BigDecimal remainingBalance) {
            this.remainingBalance = remainingBalance;
            return this;
        }

        public BusinessLoanBuilder appliedAt(LocalDateTime appliedAt) {
            this.appliedAt = appliedAt;
            return this;
        }

        public BusinessLoanBuilder repayments(List<LoanRepayment> repayments) {
            this.repayments = repayments;
            return this;
        }

        public BusinessLoan build() {
            return new BusinessLoan(id, userId, loanAmount, purpose, financialDetails, supportingDocsPath, status, termMonths, remainingBalance, appliedAt, repayments);
        }
    }
}
