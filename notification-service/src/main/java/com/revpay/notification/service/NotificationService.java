package com.revpay.notification.service;

import com.revpay.notification.dto.NotificationRequest;
import com.revpay.notification.dto.NotificationResponse;
import com.revpay.notification.entity.NotificationCategory;

import java.util.List;

public interface NotificationService {

    NotificationResponse createNotification(NotificationRequest request);

    List<NotificationResponse> getNotifications(Long userId, NotificationCategory category, Boolean unreadOnly);

    NotificationResponse markAsRead(Long notificationId, Long userId);

    void markAllAsRead(Long userId);

    Long getUnreadCount(Long userId);
}
