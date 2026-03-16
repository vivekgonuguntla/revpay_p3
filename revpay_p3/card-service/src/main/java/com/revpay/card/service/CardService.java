package com.revpay.card.service;

import com.revpay.card.dto.AddCardRequest;
import com.revpay.card.dto.CardResponse;

import java.util.List;

public interface CardService {
    CardResponse addCard(Long userId, AddCardRequest request);
    List<CardResponse> getCards(Long userId);
    void deleteCard(Long userId, Long cardId);
    CardResponse setDefaultCard(Long userId, Long cardId);
    boolean validateCard(Long userId, Long cardId);
}
