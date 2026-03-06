package com.revpay.wallet.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public class CreateMoneyRequestDto {

    @NotBlank(message = "Payer email is required")
    private String payerEmail;

    @NotNull(message = "Amount is required")
    @Positive(message = "Amount must be positive")
    private BigDecimal amount;

    private String note;

    public CreateMoneyRequestDto() {
    }

    public CreateMoneyRequestDto(String payerEmail, BigDecimal amount, String note) {
        this.payerEmail = payerEmail;
        this.amount = amount;
        this.note = note;
    }

    public static CreateMoneyRequestDtoBuilder builder() {
        return new CreateMoneyRequestDtoBuilder();
    }

    public String getPayerEmail() {
        return payerEmail;
    }

    public void setPayerEmail(String payerEmail) {
        this.payerEmail = payerEmail;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public static class CreateMoneyRequestDtoBuilder {
        private String payerEmail;
        private BigDecimal amount;
        private String note;

        CreateMoneyRequestDtoBuilder() {
        }

        public CreateMoneyRequestDtoBuilder payerEmail(String payerEmail) {
            this.payerEmail = payerEmail;
            return this;
        }

        public CreateMoneyRequestDtoBuilder amount(BigDecimal amount) {
            this.amount = amount;
            return this;
        }

        public CreateMoneyRequestDtoBuilder note(String note) {
            this.note = note;
            return this;
        }

        public CreateMoneyRequestDto build() {
            return new CreateMoneyRequestDto(payerEmail, amount, note);
        }
    }
}
