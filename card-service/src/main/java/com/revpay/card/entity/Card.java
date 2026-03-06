package com.revpay.card.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "cards")
public class Card {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false)
    private String cardHolderName;

    @Column(nullable = false)
    private String encryptedCardNumber;

    @Column(nullable = false)
    private String lastFourDigits;

    @Column(nullable = false)
    private String expiryDate;

    @Column(nullable = false)
    private String encryptedCvv;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CardType cardType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentMethodType paymentMethodType;

    @Column(nullable = false)
    private boolean isDefault;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    public Card() {
    }

    public Card(Long id, Long userId, String cardHolderName, String encryptedCardNumber,
                String lastFourDigits, String expiryDate, String encryptedCvv,
                CardType cardType, PaymentMethodType paymentMethodType,
                boolean isDefault, LocalDateTime createdAt) {
        this.id = id;
        this.userId = userId;
        this.cardHolderName = cardHolderName;
        this.encryptedCardNumber = encryptedCardNumber;
        this.lastFourDigits = lastFourDigits;
        this.expiryDate = expiryDate;
        this.encryptedCvv = encryptedCvv;
        this.cardType = cardType;
        this.paymentMethodType = paymentMethodType;
        this.isDefault = isDefault;
        this.createdAt = createdAt;
    }

    public static CardBuilder builder() {
        return new CardBuilder();
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

    public String getCardHolderName() {
        return cardHolderName;
    }

    public void setCardHolderName(String cardHolderName) {
        this.cardHolderName = cardHolderName;
    }

    public String getEncryptedCardNumber() {
        return encryptedCardNumber;
    }

    public void setEncryptedCardNumber(String encryptedCardNumber) {
        this.encryptedCardNumber = encryptedCardNumber;
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

    public String getEncryptedCvv() {
        return encryptedCvv;
    }

    public void setEncryptedCvv(String encryptedCvv) {
        this.encryptedCvv = encryptedCvv;
    }

    public CardType getCardType() {
        return cardType;
    }

    public void setCardType(CardType cardType) {
        this.cardType = cardType;
    }

    public PaymentMethodType getPaymentMethodType() {
        return paymentMethodType;
    }

    public void setPaymentMethodType(PaymentMethodType paymentMethodType) {
        this.paymentMethodType = paymentMethodType;
    }

    public boolean isDefault() {
        return isDefault;
    }

    public void setDefault(boolean isDefault) {
        this.isDefault = isDefault;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    public static class CardBuilder {
        private Long id;
        private Long userId;
        private String cardHolderName;
        private String encryptedCardNumber;
        private String lastFourDigits;
        private String expiryDate;
        private String encryptedCvv;
        private CardType cardType;
        private PaymentMethodType paymentMethodType;
        private boolean isDefault;
        private LocalDateTime createdAt;

        CardBuilder() {
        }

        public CardBuilder id(Long id) {
            this.id = id;
            return this;
        }

        public CardBuilder userId(Long userId) {
            this.userId = userId;
            return this;
        }

        public CardBuilder cardHolderName(String cardHolderName) {
            this.cardHolderName = cardHolderName;
            return this;
        }

        public CardBuilder encryptedCardNumber(String encryptedCardNumber) {
            this.encryptedCardNumber = encryptedCardNumber;
            return this;
        }

        public CardBuilder lastFourDigits(String lastFourDigits) {
            this.lastFourDigits = lastFourDigits;
            return this;
        }

        public CardBuilder expiryDate(String expiryDate) {
            this.expiryDate = expiryDate;
            return this;
        }

        public CardBuilder encryptedCvv(String encryptedCvv) {
            this.encryptedCvv = encryptedCvv;
            return this;
        }

        public CardBuilder cardType(CardType cardType) {
            this.cardType = cardType;
            return this;
        }

        public CardBuilder paymentMethodType(PaymentMethodType paymentMethodType) {
            this.paymentMethodType = paymentMethodType;
            return this;
        }

        public CardBuilder isDefault(boolean isDefault) {
            this.isDefault = isDefault;
            return this;
        }

        public CardBuilder createdAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public Card build() {
            return new Card(id, userId, cardHolderName, encryptedCardNumber,
                          lastFourDigits, expiryDate, encryptedCvv,
                          cardType, paymentMethodType, isDefault, createdAt);
        }
    }
}
