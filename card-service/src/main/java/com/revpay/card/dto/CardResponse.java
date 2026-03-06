package com.revpay.card.dto;

public class CardResponse {
    private Long id;
    private String cardHolderName;
    private String lastFourDigits;
    private String expiryDate;
    private String cardType;
    private String paymentMethodType;
    private boolean isDefault;

    public CardResponse() {
    }

    public CardResponse(Long id, String cardHolderName, String lastFourDigits,
                       String expiryDate, String cardType, String paymentMethodType,
                       boolean isDefault) {
        this.id = id;
        this.cardHolderName = cardHolderName;
        this.lastFourDigits = lastFourDigits;
        this.expiryDate = expiryDate;
        this.cardType = cardType;
        this.paymentMethodType = paymentMethodType;
        this.isDefault = isDefault;
    }

    public static CardResponseBuilder builder() {
        return new CardResponseBuilder();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCardHolderName() {
        return cardHolderName;
    }

    public void setCardHolderName(String cardHolderName) {
        this.cardHolderName = cardHolderName;
    }

    public String getLastFourDigits() {
        return lastFourDigits;
    }

    public void setLastFourDigits(String lastFourDigits) {
        this.lastFourDigits = lastFourDigits;
    }

    public String getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(String expiryDate) {
        this.expiryDate = expiryDate;
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

    public boolean isDefault() {
        return isDefault;
    }

    public void setDefault(boolean isDefault) {
        this.isDefault = isDefault;
    }

    public static class CardResponseBuilder {
        private Long id;
        private String cardHolderName;
        private String lastFourDigits;
        private String expiryDate;
        private String cardType;
        private String paymentMethodType;
        private boolean isDefault;

        CardResponseBuilder() {
        }

        public CardResponseBuilder id(Long id) {
            this.id = id;
            return this;
        }

        public CardResponseBuilder cardHolderName(String cardHolderName) {
            this.cardHolderName = cardHolderName;
            return this;
        }

        public CardResponseBuilder lastFourDigits(String lastFourDigits) {
            this.lastFourDigits = lastFourDigits;
            return this;
        }

        public CardResponseBuilder expiryDate(String expiryDate) {
            this.expiryDate = expiryDate;
            return this;
        }

        public CardResponseBuilder cardType(String cardType) {
            this.cardType = cardType;
            return this;
        }

        public CardResponseBuilder paymentMethodType(String paymentMethodType) {
            this.paymentMethodType = paymentMethodType;
            return this;
        }

        public CardResponseBuilder isDefault(boolean isDefault) {
            this.isDefault = isDefault;
            return this;
        }

        public CardResponse build() {
            return new CardResponse(id, cardHolderName, lastFourDigits,
                                   expiryDate, cardType, paymentMethodType, isDefault);
        }
    }
}
