package com.revpay.wallet.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public class WalletOperationRequest {

    @NotNull(message = "Amount is required")
    @Positive(message = "Amount must be positive")
    private BigDecimal amount;

    private Long cardId; // Optional, required for add funds

    public WalletOperationRequest() {
    }

    public WalletOperationRequest(BigDecimal amount, Long cardId) {
        this.amount = amount;
        this.cardId = cardId;
    }

    public static WalletOperationRequestBuilder builder() {
        return new WalletOperationRequestBuilder();
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public Long getCardId() {
        return cardId;
    }

    public void setCardId(Long cardId) {
        this.cardId = cardId;
    }

    public static class WalletOperationRequestBuilder {
        private BigDecimal amount;
        private Long cardId;

        WalletOperationRequestBuilder() {
        }

        public WalletOperationRequestBuilder amount(BigDecimal amount) {
            this.amount = amount;
            return this;
        }

        public WalletOperationRequestBuilder cardId(Long cardId) {
            this.cardId = cardId;
            return this;
        }

        public WalletOperationRequest build() {
            return new WalletOperationRequest(amount, cardId);
        }
    }
}
