package com.revpay.business.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "business_bank_accounts")
public class BusinessBankAccount {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false)
    private String bankName;

    @Column(nullable = false)
    private String accountHolderName;

    @Column(nullable = false)
    private String encryptedAccountNumber;

    @Column(nullable = false)
    private String accountLastFour;

    @Column(nullable = false)
    private String encryptedRoutingNumber;

    @Column(nullable = false)
    private String accountType;

    private boolean isDefault;

    private LocalDateTime createdAt;

    public BusinessBankAccount() {
    }

    public BusinessBankAccount(Long id, Long userId, String bankName, String accountHolderName, String encryptedAccountNumber, String accountLastFour, String encryptedRoutingNumber, String accountType, boolean isDefault, LocalDateTime createdAt) {
        this.id = id;
        this.userId = userId;
        this.bankName = bankName;
        this.accountHolderName = accountHolderName;
        this.encryptedAccountNumber = encryptedAccountNumber;
        this.accountLastFour = accountLastFour;
        this.encryptedRoutingNumber = encryptedRoutingNumber;
        this.accountType = accountType;
        this.isDefault = isDefault;
        this.createdAt = createdAt;
    }

    public static BusinessBankAccountBuilder builder() {
        return new BusinessBankAccountBuilder();
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

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    public String getAccountHolderName() {
        return accountHolderName;
    }

    public void setAccountHolderName(String accountHolderName) {
        this.accountHolderName = accountHolderName;
    }

    public String getEncryptedAccountNumber() {
        return encryptedAccountNumber;
    }

    public void setEncryptedAccountNumber(String encryptedAccountNumber) {
        this.encryptedAccountNumber = encryptedAccountNumber;
    }

    public String getAccountLastFour() {
        return accountLastFour;
    }

    public void setAccountLastFour(String accountLastFour) {
        this.accountLastFour = accountLastFour;
    }

    public String getEncryptedRoutingNumber() {
        return encryptedRoutingNumber;
    }

    public void setEncryptedRoutingNumber(String encryptedRoutingNumber) {
        this.encryptedRoutingNumber = encryptedRoutingNumber;
    }

    public String getAccountType() {
        return accountType;
    }

    public void setAccountType(String accountType) {
        this.accountType = accountType;
    }

    public boolean isDefault() {
        return isDefault;
    }

    public void setDefault(boolean isDefault) {
        this.isDefault = isDefault;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    @PrePersist
    public void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }

    public static class BusinessBankAccountBuilder {
        private Long id;
        private Long userId;
        private String bankName;
        private String accountHolderName;
        private String encryptedAccountNumber;
        private String accountLastFour;
        private String encryptedRoutingNumber;
        private String accountType;
        private boolean isDefault;
        private LocalDateTime createdAt;

        public BusinessBankAccountBuilder id(Long id) {
            this.id = id;
            return this;
        }

        public BusinessBankAccountBuilder userId(Long userId) {
            this.userId = userId;
            return this;
        }

        public BusinessBankAccountBuilder bankName(String bankName) {
            this.bankName = bankName;
            return this;
        }

        public BusinessBankAccountBuilder accountHolderName(String accountHolderName) {
            this.accountHolderName = accountHolderName;
            return this;
        }

        public BusinessBankAccountBuilder encryptedAccountNumber(String encryptedAccountNumber) {
            this.encryptedAccountNumber = encryptedAccountNumber;
            return this;
        }

        public BusinessBankAccountBuilder accountLastFour(String accountLastFour) {
            this.accountLastFour = accountLastFour;
            return this;
        }

        public BusinessBankAccountBuilder encryptedRoutingNumber(String encryptedRoutingNumber) {
            this.encryptedRoutingNumber = encryptedRoutingNumber;
            return this;
        }

        public BusinessBankAccountBuilder accountType(String accountType) {
            this.accountType = accountType;
            return this;
        }

        public BusinessBankAccountBuilder isDefault(boolean isDefault) {
            this.isDefault = isDefault;
            return this;
        }

        public BusinessBankAccountBuilder createdAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public BusinessBankAccount build() {
            return new BusinessBankAccount(id, userId, bankName, accountHolderName, encryptedAccountNumber, accountLastFour, encryptedRoutingNumber, accountType, isDefault, createdAt);
        }
    }
}
