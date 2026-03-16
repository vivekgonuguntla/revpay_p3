package com.revpay.notification.service;

import com.revpay.notification.dto.NotificationRequest;
import com.revpay.notification.dto.NotificationResponse;
import com.revpay.notification.entity.Notification;
import com.revpay.notification.entity.NotificationCategory;
import com.revpay.notification.entity.NotificationPreference;
import com.revpay.notification.exception.ResourceNotFoundException;
import com.revpay.notification.repository.NotificationPreferenceRepository;
import com.revpay.notification.repository.NotificationRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class NotificationServiceImplTest {

    @Mock
    private NotificationRepository notificationRepository;

    @Mock
    private NotificationPreferenceRepository preferenceRepository;

    @InjectMocks
    private NotificationServiceImpl notificationService;

    @Test
    void createNotificationSavesWhenCategoryEnabled() {
        NotificationRequest request = baseRequest();
        Notification saved = notificationFromRequest(request);
        saved.setId(10L);
        saved.setCreatedAt(LocalDateTime.of(2026, 3, 16, 9, 30));

        when(preferenceRepository.findByUserId(1L)).thenReturn(Optional.of(defaultPreference()));
        when(notificationRepository.save(any(Notification.class))).thenReturn(saved);

        NotificationResponse response = notificationService.createNotification(request);

        assertEquals(10L, response.getId());
        assertEquals(1L, response.getUserId());
        assertEquals(NotificationCategory.TRANSACTIONS, response.getCategory());
        assertEquals("Payment received", response.getTitle());
        assertFalse(response.isRead());
        assertNotNull(response.getCreatedAt());
        verify(notificationRepository).save(any(Notification.class));
    }

    @Test
    void createNotificationReturnsUnsavedResponseWhenCategoryDisabled() {
        NotificationRequest request = baseRequest();
        NotificationPreference disabledPreference = defaultPreference();
        disabledPreference.setTransactionsEnabled(false);

        when(preferenceRepository.findByUserId(1L)).thenReturn(Optional.of(disabledPreference));

        NotificationResponse response = notificationService.createNotification(request);

        assertEquals(request.getUserId(), response.getUserId());
        assertEquals(request.getTitle(), response.getTitle());
        assertFalse(response.isRead());
        assertNull(response.getId());
        verify(notificationRepository, never()).save(any(Notification.class));
    }

    @Test
    void createNotificationSuppressesLowBalanceAlertAboveThreshold() {
        NotificationRequest request = NotificationRequest.builder()
                .userId(1L)
                .category(NotificationCategory.ALERTS)
                .type("LOW_BALANCE")
                .title("Low balance")
                .message("Balance is healthy")
                .amount(BigDecimal.valueOf(250))
                .build();

        when(preferenceRepository.findByUserId(1L)).thenReturn(Optional.of(defaultPreference()));

        NotificationResponse response = notificationService.createNotification(request);

        assertEquals("Low balance", response.getTitle());
        assertNull(response.getId());
        verify(notificationRepository, never()).save(any(Notification.class));
    }

    @Test
    void getNotificationsUsesUnreadAndCategoryQueryWhenBothFiltersPresent() {
        Notification notification = notificationFromRequest(baseRequest());
        notification.setId(5L);
        notification.setCreatedAt(LocalDateTime.of(2026, 3, 16, 8, 0));

        when(notificationRepository.findByUserIdAndCategoryAndIsReadFalseOrderByCreatedAtDesc(1L, NotificationCategory.TRANSACTIONS))
                .thenReturn(List.of(notification));

        List<NotificationResponse> responses =
                notificationService.getNotifications(1L, NotificationCategory.TRANSACTIONS, true);

        assertEquals(1, responses.size());
        assertEquals(5L, responses.get(0).getId());
        verify(notificationRepository).findByUserIdAndCategoryAndIsReadFalseOrderByCreatedAtDesc(1L, NotificationCategory.TRANSACTIONS);
    }

    @Test
    void markAsReadUpdatesOwnedNotification() {
        Notification notification = notificationFromRequest(baseRequest());
        notification.setId(7L);
        notification.setRead(false);

        when(notificationRepository.findById(7L)).thenReturn(Optional.of(notification));
        when(notificationRepository.save(notification)).thenReturn(notification);

        NotificationResponse response = notificationService.markAsRead(7L, 1L);

        assertTrue(response.isRead());
        verify(notificationRepository).save(notification);
    }

    @Test
    void markAsReadThrowsWhenNotificationBelongsToDifferentUser() {
        Notification notification = notificationFromRequest(baseRequest());
        notification.setId(7L);
        notification.setUserId(99L);

        when(notificationRepository.findById(7L)).thenReturn(Optional.of(notification));

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> notificationService.markAsRead(7L, 1L));

        assertEquals("Notification does not belong to user", exception.getMessage());
        verify(notificationRepository, never()).save(any(Notification.class));
    }

    @Test
    void markAsReadThrowsWhenNotificationMissing() {
        when(notificationRepository.findById(42L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> notificationService.markAsRead(42L, 1L));
    }

    @Test
    void markAllAsReadUpdatesEveryUnreadNotification() {
        Notification first = notificationFromRequest(baseRequest());
        Notification second = notificationFromRequest(baseRequest());
        second.setId(2L);

        when(notificationRepository.findByUserIdAndIsReadFalse(1L)).thenReturn(List.of(first, second));

        notificationService.markAllAsRead(1L);

        assertTrue(first.isRead());
        assertTrue(second.isRead());
        verify(notificationRepository).saveAll(List.of(first, second));
    }

    @Test
    void getUnreadCountDelegatesToRepository() {
        when(notificationRepository.countByUserIdAndIsReadFalse(1L)).thenReturn(3L);

        Long unreadCount = notificationService.getUnreadCount(1L);

        assertEquals(3L, unreadCount);
    }

    @Test
    void createNotificationRepairsPersistedLegacyPreferences() {
        NotificationRequest request = baseRequest();
        NotificationPreference legacyPreference = NotificationPreference.builder()
                .id(15L)
                .userId(1L)
                .transactionsEnabled(false)
                .requestsEnabled(false)
                .alertsEnabled(true)
                .lowBalanceThreshold(BigDecimal.valueOf(100))
                .build();
        Notification saved = notificationFromRequest(request);
        saved.setId(88L);

        when(preferenceRepository.findByUserId(1L)).thenReturn(Optional.of(legacyPreference));
        when(preferenceRepository.save(legacyPreference)).thenReturn(legacyPreference);
        when(notificationRepository.save(any(Notification.class))).thenReturn(saved);

        NotificationResponse response = notificationService.createNotification(request);

        assertEquals(88L, response.getId());
        assertTrue(legacyPreference.isTransactionsEnabled());
        assertTrue(legacyPreference.isRequestsEnabled());
        verify(preferenceRepository).save(legacyPreference);
    }

    private NotificationRequest baseRequest() {
        return NotificationRequest.builder()
                .userId(1L)
                .category(NotificationCategory.TRANSACTIONS)
                .type("PAYMENT_RECEIVED")
                .title("Payment received")
                .message("You received 500")
                .amount(BigDecimal.valueOf(500))
                .counterparty("Alex")
                .eventStatus("SUCCESS")
                .navigationTarget("/transactions/123")
                .eventTime(LocalDateTime.of(2026, 3, 16, 9, 0))
                .metadataJson("{\"transactionId\":123}")
                .build();
    }

    private Notification notificationFromRequest(NotificationRequest request) {
        return Notification.builder()
                .userId(request.getUserId())
                .category(request.getCategory())
                .type(request.getType())
                .title(request.getTitle())
                .message(request.getMessage())
                .amount(request.getAmount())
                .counterparty(request.getCounterparty())
                .eventStatus(request.getEventStatus())
                .navigationTarget(request.getNavigationTarget())
                .eventTime(request.getEventTime())
                .metadataJson(request.getMetadataJson())
                .isRead(false)
                .build();
    }

    private NotificationPreference defaultPreference() {
        return NotificationPreference.builder()
                .id(1L)
                .userId(1L)
                .transactionsEnabled(true)
                .requestsEnabled(true)
                .alertsEnabled(true)
                .lowBalanceThreshold(BigDecimal.valueOf(100))
                .build();
    }
}
