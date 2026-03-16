package com.revpay.wallet.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "transactions")
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "sender_wallet_id")
    private Long senderWalletId;

    @Column(name = "receiver_wallet_id")
    private Long receiverWalletId;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TransactionType type;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TransactionStatus status;

    private String description;

    @Column(nullable = false)
    private LocalDateTime timestamp;

    public Transaction() {
    }

    public Transaction(Long id, Long senderWalletId, Long receiverWalletId, BigDecimal amount, TransactionType type, TransactionStatus status, String description, LocalDateTime timestamp) {
        this.id = id;
        this.senderWalletId = senderWalletId;
        this.receiverWalletId = receiverWalletId;
        this.amount = amount;
        this.type = type;
        this.status = status;
        this.description = description;
        this.timestamp = timestamp;
    }

    public static TransactionBuilder builder() {
        return new TransactionBuilder();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getSenderWalletId() {
        return senderWalletId;
    }

    public void setSenderWalletId(Long senderWalletId) {
        this.senderWalletId = senderWalletId;
    }

    public Long getReceiverWalletId() {
        return receiverWalletId;
    }

    public void setReceiverWalletId(Long receiverWalletId) {
        this.receiverWalletId = receiverWalletId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public TransactionType getType() {
        return type;
    }

    public void setType(TransactionType type) {
        this.type = type;
    }

    public TransactionStatus getStatus() {
        return status;
    }

    public void setStatus(TransactionStatus status) {
        this.status = status;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    @PrePersist
    public void prePersist() {
        if (timestamp == null) {
            timestamp = LocalDateTime.now();
        }
        if (status == null) {
            status = TransactionStatus.PENDING;
        }
    }

    public static class TransactionBuilder {
        private Long id;
        private Long senderWalletId;
        private Long receiverWalletId;
        private BigDecimal amount;
        private TransactionType type;
        private TransactionStatus status;
        private String description;
        private LocalDateTime timestamp;

        TransactionBuilder() {
        }

        public TransactionBuilder id(Long id) {
            this.id = id;
            return this;
        }

        public TransactionBuilder senderWalletId(Long senderWalletId) {
            this.senderWalletId = senderWalletId;
            return this;
        }

        public TransactionBuilder receiverWalletId(Long receiverWalletId) {
            this.receiverWalletId = receiverWalletId;
            return this;
        }

        public TransactionBuilder amount(BigDecimal amount) {
            this.amount = amount;
            return this;
        }

        public TransactionBuilder type(TransactionType type) {
            this.type = type;
            return this;
        }

        public TransactionBuilder status(TransactionStatus status) {
            this.status = status;
            return this;
        }

        public TransactionBuilder description(String description) {
            this.description = description;
            return this;
        }

        public TransactionBuilder timestamp(LocalDateTime timestamp) {
            this.timestamp = timestamp;
            return this;
        }

        public Transaction build() {
            return new Transaction(id, senderWalletId, receiverWalletId, amount, type, status, description, timestamp);
        }
    }
}
