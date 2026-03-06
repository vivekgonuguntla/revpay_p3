package com.revpay.card.exception;

public class CardLimitExceededException extends RuntimeException {
    public CardLimitExceededException(String message) {
        super(message);
    }
}
