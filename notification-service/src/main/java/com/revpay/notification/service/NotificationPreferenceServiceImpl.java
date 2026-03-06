package com.revpay.notification.service;

import com.revpay.notification.dto.NotificationPreferenceResponse;
import com.revpay.notification.dto.UpdateNotificationPreferenceRequest;
import com.revpay.notification.entity.NotificationPreference;
import com.revpay.notification.repository.NotificationPreferenceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationPreferenceServiceImpl implements NotificationPreferenceService {

    private final NotificationPreferenceRepository preferenceRepository;

    @Override
    public NotificationPreferenceResponse getPreferences(Long userId) {
        log.debug("Fetching notification preferences for user: {}", userId);

        NotificationPreference preference = preferenceRepository.findByUserId(userId)
                .orElseGet(() -> createDefaultPreferences(userId));

        return mapToResponse(preference);
    }

    @Override
    @Transactional
    public NotificationPreferenceResponse updatePreferences(Long userId, UpdateNotificationPreferenceRequest request) {
        log.debug("Updating notification preferences for user: {}", userId);

        NotificationPreference preference = preferenceRepository.findByUserId(userId)
                .orElseGet(() -> createDefaultPreferences(userId));

        if (request.getTransactionsEnabled() != null) {
            preference.setTransactionsEnabled(request.getTransactionsEnabled());
        }
        if (request.getRequestsEnabled() != null) {
            preference.setRequestsEnabled(request.getRequestsEnabled());
        }
        if (request.getAlertsEnabled() != null) {
            preference.setAlertsEnabled(request.getAlertsEnabled());
        }
        if (request.getLowBalanceThreshold() != null) {
            preference.setLowBalanceThreshold(request.getLowBalanceThreshold());
        }

        NotificationPreference updatedPreference = preferenceRepository.save(preference);
        log.info("Updated notification preferences for user: {}", userId);

        return mapToResponse(updatedPreference);
    }

    private NotificationPreference createDefaultPreferences(Long userId) {
        log.debug("Creating default notification preferences for user: {}", userId);

        NotificationPreference preference = NotificationPreference.builder()
                .userId(userId)
                .transactionsEnabled(true)
                .requestsEnabled(true)
                .alertsEnabled(true)
                .build();

        return preferenceRepository.save(preference);
    }

    private NotificationPreferenceResponse mapToResponse(NotificationPreference preference) {
        return NotificationPreferenceResponse.builder()
                .id(preference.getId())
                .userId(preference.getUserId())
                .transactionsEnabled(preference.isTransactionsEnabled())
                .requestsEnabled(preference.isRequestsEnabled())
                .alertsEnabled(preference.isAlertsEnabled())
                .lowBalanceThreshold(preference.getLowBalanceThreshold())
                .build();
    }
}
