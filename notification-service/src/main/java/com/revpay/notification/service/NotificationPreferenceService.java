package com.revpay.notification.service;

import com.revpay.notification.dto.NotificationPreferenceResponse;
import com.revpay.notification.dto.UpdateNotificationPreferenceRequest;

public interface NotificationPreferenceService {

    NotificationPreferenceResponse getPreferences(Long userId);

    NotificationPreferenceResponse updatePreferences(Long userId, UpdateNotificationPreferenceRequest request);
}
