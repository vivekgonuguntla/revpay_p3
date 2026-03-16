package com.revpay.card.service;

import com.revpay.card.client.NotificationServiceClient;
import com.revpay.card.dto.AddCardRequest;
import com.revpay.card.dto.CardResponse;
import com.revpay.card.entity.Card;
import com.revpay.card.entity.CardType;
import com.revpay.card.entity.PaymentMethodType;
import com.revpay.card.exception.CardLimitExceededException;
import com.revpay.card.repository.CardRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CardServiceImplTest {

    @Mock
    private CardRepository cardRepository;

    @Mock
    private EncryptionService encryptionService;

    @Mock
    private NotificationServiceClient notificationClient;

    @InjectMocks
    private CardServiceImpl cardService;

    @Test
    void addCardSetsDefaultAndSendsNotificationWhenRequested() {
        AddCardRequest request = buildRequest();
        request.setSetAsDefault(true);

        when(cardRepository.countByUserId(8L)).thenReturn(1L);
        when(cardRepository.existsByUserIdAndIsDefaultTrue(8L)).thenReturn(true);
        when(encryptionService.encrypt(anyString()))
                .thenAnswer(invocation -> "enc-" + invocation.getArgument(0));
        when(cardRepository.save(any(Card.class))).thenAnswer(invocation -> {
            Card card = invocation.getArgument(0);
            card.setId(5L);
            card.setCreatedAt(LocalDateTime.now());
            return card;
        });

        CardResponse response = cardService.addCard(8L, request);

        ArgumentCaptor<Card> cardCaptor = ArgumentCaptor.forClass(Card.class);
        verify(cardRepository).clearDefaultForUser(8L);
        verify(cardRepository).save(cardCaptor.capture());
        verify(notificationClient).sendNotification(any(NotificationServiceClient.NotificationRequest.class));

        Card saved = cardCaptor.getValue();
        assertEquals("1111", saved.getLastFourDigits());
        assertNotEquals(request.getCardNumber(), saved.getEncryptedCardNumber());
        assertNotEquals(request.getCvv(), saved.getEncryptedCvv());
        assertEquals(CardType.VISA, saved.getCardType());
        assertEquals(PaymentMethodType.CREDIT, saved.getPaymentMethodType());
        assertTrue(saved.isDefault());
        assertEquals(5L, response.getId());
        assertEquals("VISA", response.getCardType());
    }

    @Test
    void addCardSetsDefaultWhenNoExistingDefault() {
        AddCardRequest request = buildRequest();
        request.setSetAsDefault(false);

        when(cardRepository.countByUserId(8L)).thenReturn(0L);
        when(cardRepository.existsByUserIdAndIsDefaultTrue(8L)).thenReturn(false);
        when(encryptionService.encrypt(anyString()))
                .thenAnswer(invocation -> "enc-" + invocation.getArgument(0));
        when(cardRepository.save(any(Card.class))).thenAnswer(invocation -> invocation.getArgument(0));

        cardService.addCard(8L, request);

        ArgumentCaptor<Card> cardCaptor = ArgumentCaptor.forClass(Card.class);
        verify(cardRepository).save(cardCaptor.capture());
        verify(cardRepository, never()).clearDefaultForUser(8L);
        assertTrue(cardCaptor.getValue().isDefault());
    }

    @Test
    void addCardThrowsWhenLimitExceeded() {
        when(cardRepository.countByUserId(8L)).thenReturn(10L);

        assertThrows(CardLimitExceededException.class,
                () -> cardService.addCard(8L, buildRequest()));

        verify(cardRepository, never()).save(any(Card.class));
        verify(notificationClient, never()).sendNotification(any(NotificationServiceClient.NotificationRequest.class));
    }

    @Test
    void deleteCardPromotesNextCardWhenDefaultIsDeleted() {
        Card existing = Card.builder()
                .id(5L)
                .userId(8L)
                .lastFourDigits("1111")
                .isDefault(true)
                .build();
        Card next = Card.builder()
                .id(6L)
                .userId(8L)
                .isDefault(false)
                .build();

        when(cardRepository.findById(5L)).thenReturn(Optional.of(existing));
        when(cardRepository.findByUserId(8L)).thenReturn(List.of(next));

        cardService.deleteCard(8L, 5L);

        verify(cardRepository).delete(existing);
        verify(cardRepository).save(next);
        assertTrue(next.isDefault());
        verify(notificationClient).sendNotification(any(NotificationServiceClient.NotificationRequest.class));
    }

    @Test
    void setDefaultCardReturnsEarlyWhenAlreadyDefault() {
        Card existing = Card.builder()
                .id(5L)
                .userId(8L)
                .isDefault(true)
                .cardType(CardType.VISA)
                .paymentMethodType(PaymentMethodType.DEBIT)
                .lastFourDigits("1111")
                .build();

        when(cardRepository.findById(5L)).thenReturn(Optional.of(existing));

        CardResponse response = cardService.setDefaultCard(8L, 5L);

        assertTrue(response.isDefault());
        verify(cardRepository, never()).clearDefaultForUser(anyLong());
        verify(cardRepository, never()).save(any(Card.class));
        verify(notificationClient, never()).sendNotification(any(NotificationServiceClient.NotificationRequest.class));
    }

    @Test
    void validateCardReturnsFalseForDifferentUser() {
        Card existing = Card.builder()
                .id(5L)
                .userId(99L)
                .build();

        when(cardRepository.findById(5L)).thenReturn(Optional.of(existing));

        assertFalse(cardService.validateCard(8L, 5L));
    }

    private AddCardRequest buildRequest() {
        AddCardRequest request = new AddCardRequest();
        request.setCardHolderName("Nikhil");
        request.setCardNumber("4111111111111111");
        request.setExpiryDate("12/28");
        request.setCvv("123");
        request.setPaymentMethodType("credit");
        request.setSetAsDefault(false);
        return request;
    }
}
