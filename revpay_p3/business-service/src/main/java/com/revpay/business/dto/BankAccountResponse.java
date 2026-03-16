package com.revpay.business.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;

public class BankAccountResponse {
    private Long id;
    private String bankName;
    private String accountHolderName;
    private String accountLastFour;
    private String accountType;
    private boolean isDefault;
    private LocalDateTime createdAt;

    public BankAccountResponse() {
    }

    public BankAccountResponse(Long id, String bankName, String accountHolderName, String accountLastFour, String accountType, boolean isDefault, LocalDateTime createdAt) {
        this.id = id;
        this.bankName = bankName;
        this.accountHolderName = accountHolderName;
        this.accountLastFour = accountLastFour;
        this.accountType = accountType;
        this.isDefault = isDefault;
        this.createdAt = createdAt;
    }

    public static BankAccountResponseBuilder builder() {
        return new BankAccountResponseBuilder();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public String getAccountLastFour() {
        return accountLastFour;
    }

    public void setAccountLastFour(String accountLastFour) {
        this.accountLastFour = accountLastFour;
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

    @JsonProperty("defaultAccount")
    public boolean isDefaultAccount() {
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

    public static class BankAccountResponseBuilder {
        private Long id;
        private String bankName;
        private String accountHolderName;
        private String accountLastFour;
        private String accountType;
        private boolean isDefault;
        private LocalDateTime createdAt;

        public BankAccountResponseBuilder id(Long id) {
            this.id = id;
            return this;
        }

        public BankAccountResponseBuilder bankName(String bankName) {
            this.bankName = bankName;
            return this;
        }

        public BankAccountResponseBuilder accountHolderName(String accountHolderName) {
            this.accountHolderName = accountHolderName;
            return this;
        }

        public BankAccountResponseBuilder accountLastFour(String accountLastFour) {
            this.accountLastFour = accountLastFour;
            return this;
        }

        public BankAccountResponseBuilder accountType(String accountType) {
            this.accountType = accountType;
            return this;
        }

        public BankAccountResponseBuilder isDefault(boolean isDefault) {
            this.isDefault = isDefault;
            return this;
        }

        public BankAccountResponseBuilder createdAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public BankAccountResponse build() {
            return new BankAccountResponse(id, bankName, accountHolderName, accountLastFour, accountType, isDefault, createdAt);
        }
    }
}
