package com.revpay.wallet.dto;

import com.revpay.wallet.entity.TransactionStatus;
import com.revpay.wallet.entity.TransactionType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class TransactionResponse {
    private Long id;
    private Long senderWalletId;
    private Long receiverWalletId;
    private String senderEmail;
    private String receiverEmail;
    private BigDecimal amount;
    private TransactionType type;
    private TransactionStatus status;
    private String description;
    private LocalDateTime timestamp;

    public TransactionResponse() {
    }

    public TransactionResponse(Long id, Long senderWalletId, Long receiverWalletId, String senderEmail, String receiverEmail, BigDecimal amount, TransactionType type, TransactionStatus status, String description, LocalDateTime timestamp) {
        this.id = id;
        this.senderWalletId = senderWalletId;
        this.receiverWalletId = receiverWalletId;
        this.senderEmail = senderEmail;
        this.receiverEmail = receiverEmail;
        this.amount = amount;
        this.type = type;
        this.status = status;
        this.description = description;
        this.timestamp = timestamp;
    }

    public static TransactionResponseBuilder builder() {
        return new TransactionResponseBuilder();
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

    public String getSenderEmail() {
        return senderEmail;
    }

    public void setSenderEmail(String senderEmail) {
        this.senderEmail = senderEmail;
    }

    public String getReceiverEmail() {
        return receiverEmail;
    }

    public void setReceiverEmail(String receiverEmail) {
        this.receiverEmail = receiverEmail;
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

    public static class TransactionResponseBuilder {
        private Long id;
        private Long senderWalletId;
        private Long receiverWalletId;
        private String senderEmail;
        private String receiverEmail;
        private BigDecimal amount;
        private TransactionType type;
        private TransactionStatus status;
        private String description;
        private LocalDateTime timestamp;

        TransactionResponseBuilder() {
        }

        public TransactionResponseBuilder id(Long id) {
            this.id = id;
            return this;
        }

        public TransactionResponseBuilder senderWalletId(Long senderWalletId) {
            this.senderWalletId = senderWalletId;
            return this;
        }

        public TransactionResponseBuilder receiverWalletId(Long receiverWalletId) {
            this.receiverWalletId = receiverWalletId;
            return this;
        }

        public TransactionResponseBuilder senderEmail(String senderEmail) {
            this.senderEmail = senderEmail;
            return this;
        }

        public TransactionResponseBuilder receiverEmail(String receiverEmail) {
            this.receiverEmail = receiverEmail;
            return this;
        }

        public TransactionResponseBuilder amount(BigDecimal amount) {
            this.amount = amount;
            return this;
        }

        public TransactionResponseBuilder type(TransactionType type) {
            this.type = type;
            return this;
        }

        public TransactionResponseBuilder status(TransactionStatus status) {
            this.status = status;
            return this;
        }

        public TransactionResponseBuilder description(String description) {
            this.description = description;
            return this;
        }

        public TransactionResponseBuilder timestamp(LocalDateTime timestamp) {
            this.timestamp = timestamp;
            return this;
        }

        public TransactionResponse build() {
            return new TransactionResponse(id, senderWalletId, receiverWalletId, senderEmail, receiverEmail, amount, type, status, description, timestamp);
        }
    }
}
