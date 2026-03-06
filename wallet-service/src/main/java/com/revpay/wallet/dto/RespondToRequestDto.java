package com.revpay.wallet.dto;

import jakarta.validation.constraints.NotNull;

public class RespondToRequestDto {

    @NotNull(message = "Accept field is required")
    private Boolean accept;

    private String pin; // Required if accepting

    public RespondToRequestDto() {
    }

    public RespondToRequestDto(Boolean accept, String pin) {
        this.accept = accept;
        this.pin = pin;
    }

    public static RespondToRequestDtoBuilder builder() {
        return new RespondToRequestDtoBuilder();
    }

    public Boolean getAccept() {
        return accept;
    }

    public void setAccept(Boolean accept) {
        this.accept = accept;
    }

    public String getPin() {
        return pin;
    }

    public void setPin(String pin) {
        this.pin = pin;
    }

    public static class RespondToRequestDtoBuilder {
        private Boolean accept;
        private String pin;

        RespondToRequestDtoBuilder() {
        }

        public RespondToRequestDtoBuilder accept(Boolean accept) {
            this.accept = accept;
            return this;
        }

        public RespondToRequestDtoBuilder pin(String pin) {
            this.pin = pin;
            return this;
        }

        public RespondToRequestDto build() {
            return new RespondToRequestDto(accept, pin);
        }
    }
}
