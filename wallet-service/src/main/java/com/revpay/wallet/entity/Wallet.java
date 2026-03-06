package com.revpay.wallet.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "wallets")
public class Wallet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private Long userId;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal balance;

    @Column(nullable = false)
    private String currency;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public Wallet() {
    }

    public Wallet(Long id, Long userId, BigDecimal balance, String currency, LocalDateTime createdAt) {
        this.id = id;
        this.userId = userId;
        this.balance = balance;
        this.currency = currency;
        this.createdAt = createdAt;
    }

    public static WalletBuilder builder() {
        return new WalletBuilder();
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

    @PrePersist
    public void prePersist() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
        if (balance == null) {
            balance = BigDecimal.ZERO;
        }
        if (currency == null) {
            currency = "USD";
        }
    }

    public static class WalletBuilder {
        private Long id;
        private Long userId;
        private BigDecimal balance;
        private String currency;
        private LocalDateTime createdAt;

        WalletBuilder() {
        }

        public WalletBuilder id(Long id) {
            this.id = id;
            return this;
        }

        public WalletBuilder userId(Long userId) {
            this.userId = userId;
            return this;
        }

        public WalletBuilder balance(BigDecimal balance) {
            this.balance = balance;
            return this;
        }

        public WalletBuilder currency(String currency) {
            this.currency = currency;
            return this;
        }

        public WalletBuilder createdAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public Wallet build() {
            return new Wallet(id, userId, balance, currency, createdAt);
        }
    }
}
