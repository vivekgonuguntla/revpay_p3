package com.revpay.notification.service;

import com.revpay.notification.dto.NotificationPreferenceResponse;
import com.revpay.notification.dto.UpdateNotificationPreferenceRequest;
import com.revpay.notification.entity.NotificationPreference;
import com.revpay.notification.repository.NotificationPreferenceRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class NotificationPreferenceServiceImplTest {

    @Mock
    private NotificationPreferenceRepository preferenceRepository;

    @InjectMocks
    private NotificationPreferenceServiceImpl preferenceService;

    @Test
    void getPreferencesCreatesDefaultsWhenMissing() {
        NotificationPreference createdPreference = NotificationPreference.builder()
                .id(1L)
                .userId(99L)
                .transactionsEnabled(true)
                .requestsEnabled(true)
                .alertsEnabled(true)
                .lowBalanceThreshold(BigDecimal.valueOf(100))
                .build();

        when(preferenceRepository.findByUserId(99L)).thenReturn(Optional.empty());
        when(preferenceRepository.save(any(NotificationPreference.class))).thenReturn(createdPreference);

        NotificationPreferenceResponse response = preferenceService.getPreferences(99L);

        assertEquals(99L, response.getUserId());
        assertTrue(response.isTransactionsEnabled());
        assertTrue(response.isRequestsEnabled());
        assertTrue(response.isAlertsEnabled());
        assertEquals(BigDecimal.valueOf(100), response.getLowBalanceThreshold());
        verify(preferenceRepository).save(any(NotificationPreference.class));
    }

    @Test
    void getPreferencesRepairsLegacyRows() {
        NotificationPreference legacyPreference = NotificationPreference.builder()
                .id(3L)
                .userId(11L)
                .transactionsEnabled(false)
                .requestsEnabled(false)
                .alertsEnabled(true)
                .lowBalanceThreshold(BigDecimal.valueOf(100))
                .build();

        when(preferenceRepository.findByUserId(11L)).thenReturn(Optional.of(legacyPreference));
        when(preferenceRepository.save(legacyPreference)).thenReturn(legacyPreference);

        NotificationPreferenceResponse response = preferenceService.getPreferences(11L);

        assertTrue(response.isTransactionsEnabled());
        assertTrue(response.isRequestsEnabled());
        assertTrue(response.isAlertsEnabled());
        assertEquals(BigDecimal.valueOf(100), response.getLowBalanceThreshold());
        verify(preferenceRepository).save(legacyPreference);
    }

    @Test
    void updatePreferencesAppliesOnlyProvidedFields() {
        NotificationPreference existingPreference = NotificationPreference.builder()
                .id(7L)
                .userId(21L)
                .transactionsEnabled(true)
                .requestsEnabled(true)
                .alertsEnabled(true)
                .lowBalanceThreshold(BigDecimal.valueOf(100))
                .build();
        UpdateNotificationPreferenceRequest request = UpdateNotificationPreferenceRequest.builder()
                .transactionsEnabled(false)
                .alertsEnabled(false)
                .lowBalanceThreshold(BigDecimal.valueOf(50))
                .build();

        when(preferenceRepository.findByUserId(21L)).thenReturn(Optional.of(existingPreference));
        when(preferenceRepository.save(existingPreference)).thenReturn(existingPreference);

        NotificationPreferenceResponse response = preferenceService.updatePreferences(21L, request);

        assertFalse(response.isTransactionsEnabled());
        assertTrue(response.isRequestsEnabled());
        assertFalse(response.isAlertsEnabled());
        assertEquals(BigDecimal.valueOf(50), response.getLowBalanceThreshold());
        verify(preferenceRepository).save(existingPreference);
    }

    @Test
    void updatePreferencesCreatesDefaultsBeforeApplyingOverrides() {
        NotificationPreference createdPreference = NotificationPreference.builder()
                .id(8L)
                .userId(25L)
                .transactionsEnabled(true)
                .requestsEnabled(true)
                .alertsEnabled(true)
                .lowBalanceThreshold(BigDecimal.valueOf(100))
                .build();
        UpdateNotificationPreferenceRequest request = UpdateNotificationPreferenceRequest.builder()
                .requestsEnabled(false)
                .build();

        when(preferenceRepository.findByUserId(25L)).thenReturn(Optional.empty());
        when(preferenceRepository.save(any(NotificationPreference.class)))
                .thenReturn(createdPreference)
                .thenReturn(createdPreference);

        NotificationPreferenceResponse response = preferenceService.updatePreferences(25L, request);

        assertTrue(response.isTransactionsEnabled());
        assertFalse(response.isRequestsEnabled());
        assertTrue(response.isAlertsEnabled());
        verify(preferenceRepository, times(2)).save(any(NotificationPreference.class));
    }
}
