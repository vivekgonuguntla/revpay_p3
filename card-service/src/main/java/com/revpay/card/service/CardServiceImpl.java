package com.revpay.card.service;

import com.revpay.card.client.NotificationServiceClient;
import com.revpay.card.dto.AddCardRequest;
import com.revpay.card.dto.CardResponse;
import com.revpay.card.entity.Card;
import com.revpay.card.entity.CardType;
import com.revpay.card.entity.PaymentMethodType;
import com.revpay.card.exception.CardLimitExceededException;
import com.revpay.card.exception.ResourceNotFoundException;
import com.revpay.card.repository.CardRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class CardServiceImpl implements CardService {

    private static final Logger log = LoggerFactory.getLogger(CardServiceImpl.class);

    private final CardRepository cardRepository;
    private final EncryptionService encryptionService;
    private final NotificationServiceClient notificationClient;

    public CardServiceImpl(CardRepository cardRepository, EncryptionService encryptionService,
                          NotificationServiceClient notificationClient) {
        this.cardRepository = cardRepository;
        this.encryptionService = encryptionService;
        this.notificationClient = notificationClient;
    }

    private static final int MAX_CARDS_PER_USER = 10;

    @Override
    @Transactional
    public CardResponse addCard(Long userId, AddCardRequest request) {
        log.info("Adding card for user: {}", userId);

        // Check card limit
        long cardCount = cardRepository.countByUserId(userId);
        if (cardCount >= MAX_CARDS_PER_USER) {
            throw new CardLimitExceededException("Maximum card limit (" + MAX_CARDS_PER_USER + ") exceeded");
        }

        // Parse card type and payment method type
        CardType cardType = parseCardType(request.getCardType());
        PaymentMethodType paymentMethodType = parsePaymentMethodType(request.getPaymentMethodType());

        // Encrypt sensitive data
        String encryptedCardNumber = encryptionService.encrypt(request.getCardNumber());
        String encryptedCvv = encryptionService.encrypt(request.getCvv());
        String lastFourDigits = request.getCardNumber().substring(request.getCardNumber().length() - 4);

        // Check if this is the first card (should be default)
        boolean isFirstCard = cardRepository.countByUserId(userId) == 0;

        Card card = Card.builder()
                .userId(userId)
                .cardHolderName(request.getCardHolderName())
                .encryptedCardNumber(encryptedCardNumber)
                .lastFourDigits(lastFourDigits)
                .expiryDate(request.getExpiryDate())
                .encryptedCvv(encryptedCvv)
                .cardType(cardType)
                .paymentMethodType(paymentMethodType)
                .isDefault(isFirstCard)
                .build();

        Card savedCard = cardRepository.save(card);
        CardResponse response = mapToResponse(savedCard);

        // Send notification
        try {
            Map<String, Object> metadata = new HashMap<>();
            metadata.put("cardId", savedCard.getId());
            metadata.put("lastFourDigits", lastFourDigits);
            metadata.put("cardType", cardType.name());

            notificationClient.sendNotification(userId, "CARD_ADDED",
                "Card Added",
                "Card ending in " + lastFourDigits + " was added successfully.",
                metadata);
        } catch (Exception e) {
            log.error("Failed to send notification for card addition", e);
        }

        return response;
    }

    @Override
    public List<CardResponse> getCards(Long userId) {
        log.info("Fetching cards for user: {}", userId);
        return cardRepository.findByUserId(userId).stream()
                .sorted(Comparator.comparing(Card::isDefault).reversed()
                        .thenComparing(Card::getCreatedAt).reversed())
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void deleteCard(Long userId, Long cardId) {
        log.info("Deleting card {} for user: {}", cardId, userId);

        Card card = cardRepository.findById(cardId)
                .orElseThrow(() -> new ResourceNotFoundException("Card not found"));

        if (!card.getUserId().equals(userId)) {
            throw new ResourceNotFoundException("Card not found");
        }

        boolean wasDefault = card.isDefault();
        String lastFourDigits = card.getLastFourDigits();

        cardRepository.delete(card);

        // If deleted card was default, set next card as default
        if (wasDefault) {
            cardRepository.findByUserId(userId).stream()
                    .findFirst()
                    .ifPresent(nextCard -> {
                        nextCard.setDefault(true);
                        cardRepository.save(nextCard);
                    });
        }

        // Send notification
        try {
            Map<String, Object> metadata = new HashMap<>();
            metadata.put("lastFourDigits", lastFourDigits);

            notificationClient.sendNotification(userId, "CARD_REMOVED",
                "Card Removed",
                "Card ending in " + lastFourDigits + " was removed.",
                metadata);
        } catch (Exception e) {
            log.error("Failed to send notification for card deletion", e);
        }
    }

    @Override
    @Transactional
    public CardResponse setDefaultCard(Long userId, Long cardId) {
        log.info("Setting card {} as default for user: {}", cardId, userId);

        Card card = cardRepository.findById(cardId)
                .orElseThrow(() -> new ResourceNotFoundException("Card not found"));

        if (!card.getUserId().equals(userId)) {
            throw new ResourceNotFoundException("Card not found");
        }

        if (card.isDefault()) {
            return mapToResponse(card);
        }

        // Clear existing default
        cardRepository.clearDefaultForUser(userId);

        // Set new default
        card.setDefault(true);
        Card savedCard = cardRepository.save(card);
        CardResponse response = mapToResponse(savedCard);

        // Send notification
        try {
            Map<String, Object> metadata = new HashMap<>();
            metadata.put("cardId", savedCard.getId());
            metadata.put("lastFourDigits", savedCard.getLastFourDigits());

            notificationClient.sendNotification(userId, "DEFAULT_CARD_UPDATED",
                "Default Card Updated",
                "Card ending in " + savedCard.getLastFourDigits() + " is now your default card.",
                metadata);
        } catch (Exception e) {
            log.error("Failed to send notification for default card update", e);
        }

        return response;
    }

    private CardResponse mapToResponse(Card card) {
        return CardResponse.builder()
                .id(card.getId())
                .cardHolderName(card.getCardHolderName())
                .lastFourDigits(card.getLastFourDigits())
                .expiryDate(card.getExpiryDate())
                .cardType(card.getCardType().name())
                .paymentMethodType(card.getPaymentMethodType().name())
                .isDefault(card.isDefault())
                .build();
    }

    private CardType parseCardType(String cardTypeStr) {
        try {
            return CardType.valueOf(cardTypeStr.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid card type: " + cardTypeStr);
        }
    }

    private PaymentMethodType parsePaymentMethodType(String paymentMethodTypeStr) {
        try {
            return PaymentMethodType.valueOf(paymentMethodTypeStr.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid payment method type: " + paymentMethodTypeStr);
        }
    }
}
