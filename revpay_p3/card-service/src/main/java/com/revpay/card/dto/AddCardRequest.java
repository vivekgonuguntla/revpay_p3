package com.revpay.card.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public class AddCardRequest {

    @NotBlank(message = "Card holder name is required")
    private String cardHolderName;

    @NotBlank(message = "Card number is required")
    @Pattern(regexp = "^[0-9]{13,19}$", message = "Card number must be 13-19 digits")
    private String cardNumber;

    @NotBlank(message = "Expiry date is required")
    @Pattern(regexp = "^(0[1-9]|1[0-2])/[0-9]{2}$", message = "Expiry date must be in MM/YY format")
    private String expiryDate;

    @NotBlank(message = "CVV is required")
    @Pattern(regexp = "^[0-9]{3,4}$", message = "CVV must be 3 or 4 digits")
    private String cvv;

    private String cardType;

    @NotBlank(message = "Payment method type is required")
    private String paymentMethodType;

    private Boolean setAsDefault;

    public AddCardRequest() {
    }

    public AddCardRequest(String cardHolderName, String cardNumber, String expiryDate,
                         String cvv, String cardType, String paymentMethodType, Boolean setAsDefault) {
        this.cardHolderName = cardHolderName;
        this.cardNumber = cardNumber;
        this.expiryDate = expiryDate;
        this.cvv = cvv;
        this.cardType = cardType;
        this.paymentMethodType = paymentMethodType;
        this.setAsDefault = setAsDefault;
    }

    public static AddCardRequestBuilder builder() {
        return new AddCardRequestBuilder();
    }

    public String getCardHolderName() {
        return cardHolderName;
    }

    public void setCardHolderName(String cardHolderName) {
        this.cardHolderName = cardHolderName;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    public String getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(String expiryDate) {
        this.expiryDate = expiryDate;
    }

    public String getCvv() {
        return cvv;
    }

    public void setCvv(String cvv) {
        this.cvv = cvv;
    }

    public String getCardType() {
        return cardType;
    }

    public void setCardType(String cardType) {
        this.cardType = cardType;
    }

    public String getPaymentMethodType() {
        return paymentMethodType;
    }

    public void setPaymentMethodType(String paymentMethodType) {
        this.paymentMethodType = paymentMethodType;
    }

    public Boolean getSetAsDefault() {
        return setAsDefault;
    }

    public void setSetAsDefault(Boolean setAsDefault) {
        this.setAsDefault = setAsDefault;
    }

    public static class AddCardRequestBuilder {
        private String cardHolderName;
        private String cardNumber;
        private String expiryDate;
        private String cvv;
        private String cardType;
        private String paymentMethodType;
        private Boolean setAsDefault;

        AddCardRequestBuilder() {
        }

        public AddCardRequestBuilder cardHolderName(String cardHolderName) {
            this.cardHolderName = cardHolderName;
            return this;
        }

        public AddCardRequestBuilder cardNumber(String cardNumber) {
            this.cardNumber = cardNumber;
            return this;
        }

        public AddCardRequestBuilder expiryDate(String expiryDate) {
            this.expiryDate = expiryDate;
            return this;
        }

        public AddCardRequestBuilder cvv(String cvv) {
            this.cvv = cvv;
            return this;
        }

        public AddCardRequestBuilder cardType(String cardType) {
            this.cardType = cardType;
            return this;
        }

        public AddCardRequestBuilder paymentMethodType(String paymentMethodType) {
            this.paymentMethodType = paymentMethodType;
            return this;
        }

        public AddCardRequestBuilder setAsDefault(Boolean setAsDefault) {
            this.setAsDefault = setAsDefault;
            return this;
        }

        public AddCardRequest build() {
            return new AddCardRequest(cardHolderName, cardNumber, expiryDate,
                                     cvv, cardType, paymentMethodType, setAsDefault);
        }
    }
}
