package com.revpay.wallet.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public class SendMoneyRequest {

    @NotBlank(message = "Receiver email is required")
    private String receiverEmail;

    @NotNull(message = "Amount is required")
    @Positive(message = "Amount must be positive")
    private BigDecimal amount;

    private String description;

    @NotBlank(message = "PIN is required")
    private String pin;

    public SendMoneyRequest() {
    }

    public SendMoneyRequest(String receiverEmail, BigDecimal amount, String description, String pin) {
        this.receiverEmail = receiverEmail;
        this.amount = amount;
        this.description = description;
        this.pin = pin;
    }

    public static SendMoneyRequestBuilder builder() {
        return new SendMoneyRequestBuilder();
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPin() {
        return pin;
    }

    public void setPin(String pin) {
        this.pin = pin;
    }

    public static class SendMoneyRequestBuilder {
        private String receiverEmail;
        private BigDecimal amount;
        private String description;
        private String pin;

        SendMoneyRequestBuilder() {
        }

        public SendMoneyRequestBuilder receiverEmail(String receiverEmail) {
            this.receiverEmail = receiverEmail;
            return this;
        }

        public SendMoneyRequestBuilder amount(BigDecimal amount) {
            this.amount = amount;
            return this;
        }

        public SendMoneyRequestBuilder description(String description) {
            this.description = description;
            return this;
        }

        public SendMoneyRequestBuilder pin(String pin) {
            this.pin = pin;
            return this;
        }

        public SendMoneyRequest build() {
            return new SendMoneyRequest(receiverEmail, amount, description, pin);
        }
    }
}
