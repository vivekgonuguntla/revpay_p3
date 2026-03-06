package com.revpay.wallet.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class WalletResponse {
    private Long id;
    private Long userId;
    private BigDecimal balance;
    private String currency;
    private LocalDateTime createdAt;

    public WalletResponse() {
    }

    public WalletResponse(Long id, Long userId, BigDecimal balance, String currency, LocalDateTime createdAt) {
        this.id = id;
        this.userId = userId;
        this.balance = balance;
        this.currency = currency;
        this.createdAt = createdAt;
    }

    public static WalletResponseBuilder builder() {
        return new WalletResponseBuilder();
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

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public static class WalletResponseBuilder {
        private Long id;
        private Long userId;
        private BigDecimal balance;
        private String currency;
        private LocalDateTime createdAt;

        WalletResponseBuilder() {
        }

        public WalletResponseBuilder id(Long id) {
            this.id = id;
            return this;
        }

        public WalletResponseBuilder userId(Long userId) {
            this.userId = userId;
            return this;
        }

        public WalletResponseBuilder balance(BigDecimal balance) {
            this.balance = balance;
            return this;
        }

        public WalletResponseBuilder currency(String currency) {
            this.currency = currency;
            return this;
        }

        public WalletResponseBuilder createdAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public WalletResponse build() {
            return new WalletResponse(id, userId, balance, currency, createdAt);
        }
    }
}
