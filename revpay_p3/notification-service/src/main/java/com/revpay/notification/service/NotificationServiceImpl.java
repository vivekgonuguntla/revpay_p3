package com.revpay.notification.service;

import com.revpay.notification.dto.NotificationRequest;
import com.revpay.notification.dto.NotificationResponse;
import com.revpay.notification.entity.Notification;
import com.revpay.notification.entity.NotificationCategory;
import com.revpay.notification.entity.NotificationPreference;
import com.revpay.notification.exception.ResourceNotFoundException;
import com.revpay.notification.repository.NotificationPreferenceRepository;
import com.revpay.notification.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final NotificationPreferenceRepository preferenceRepository;

    @Override
    @Transactional
    public NotificationResponse createNotification(NotificationRequest request) {
        log.debug("Creating notification for user: {}", request.getUserId());

        if (!isNotificationEnabled(request)) {
            log.debug("Notification suppressed by preferences for user {}", request.getUserId());
            return NotificationResponse.builder()
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
                    .isRead(false)
                    .build();
        }

        Notification notification = Notification.builder()
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

        Notification savedNotification = notificationRepository.save(notification);
        log.info("Notification created successfully with ID: {}", savedNotification.getId());

        return mapToResponse(savedNotification);
    }

    @Override
    public List<NotificationResponse> getNotifications(Long userId, NotificationCategory category, Boolean unreadOnly) {
        log.debug("Fetching notifications for user: {}, category: {}, unreadOnly: {}", userId, category, unreadOnly);

        List<Notification> notifications;

        if (Boolean.TRUE.equals(unreadOnly) && category != null) {
            notifications = notificationRepository.findByUserIdAndCategoryAndIsReadFalseOrderByCreatedAtDesc(userId, category);
        } else if (unreadOnly != null && unreadOnly) {
            notifications = notificationRepository.findByUserIdAndIsReadFalseOrderByCreatedAtDesc(userId);
        } else if (category != null) {
            notifications = notificationRepository.findByUserIdAndCategoryOrderByCreatedAtDesc(userId, category);
        } else {
            notifications = notificationRepository.findByUserIdOrderByCreatedAtDesc(userId);
        }

        return notifications.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public NotificationResponse markAsRead(Long notificationId, Long userId) {
        log.debug("Marking notification as read: {}", notificationId);

        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new ResourceNotFoundException("Notification not found with ID: " + notificationId));

        if (!notification.getUserId().equals(userId)) {
            throw new RuntimeException("Notification does not belong to user");
        }

        notification.setRead(true);
        Notification updatedNotification = notificationRepository.save(notification);

        return mapToResponse(updatedNotification);
    }

    @Override
    @Transactional
    public void markAllAsRead(Long userId) {
        log.debug("Marking all notifications as read for user: {}", userId);

        List<Notification> unreadNotifications = notificationRepository.findByUserIdAndIsReadFalse(userId);
        unreadNotifications.forEach(notification -> notification.setRead(true));
        notificationRepository.saveAll(unreadNotifications);

        log.info("Marked {} notifications as read for user: {}", unreadNotifications.size(), userId);
    }

    @Override
    public Long getUnreadCount(Long userId) {
        log.debug("Getting unread count for user: {}", userId);
        return notificationRepository.countByUserIdAndIsReadFalse(userId);
    }

    private NotificationResponse mapToResponse(Notification notification) {
        return NotificationResponse.builder()
                .id(notification.getId())
                .userId(notification.getUserId())
                .category(notification.getCategory())
                .type(notification.getType())
                .title(notification.getTitle())
                .message(notification.getMessage())
                .amount(notification.getAmount())
                .counterparty(notification.getCounterparty())
                .eventStatus(notification.getEventStatus())
                .navigationTarget(notification.getNavigationTarget())
                .eventTime(notification.getEventTime())
                .metadataJson(notification.getMetadataJson())
                .isRead(notification.isRead())
                .createdAt(notification.getCreatedAt())
                .build();
    }

    private boolean isNotificationEnabled(NotificationRequest request) {
        NotificationPreference preference = preferenceRepository.findByUserId(request.getUserId())
                .orElse(NotificationPreference.builder()
                        .userId(request.getUserId())
                        .transactionsEnabled(true)
                        .requestsEnabled(true)
                        .alertsEnabled(true)
                        .lowBalanceThreshold(BigDecimal.valueOf(100))
                        .build());
        preference = normalizeLegacyPreferences(preference);

        return switch (request.getCategory()) {
            case TRANSACTIONS -> preference.isTransactionsEnabled();
            case REQUESTS -> preference.isRequestsEnabled();
            case ALERTS -> {
                if (!preference.isAlertsEnabled()) {
                    yield false;
                }
                if ("LOW_BALANCE".equalsIgnoreCase(request.getType())
                        && preference.getLowBalanceThreshold() != null
                        && request.getAmount() != null) {
                    yield request.getAmount().compareTo(preference.getLowBalanceThreshold()) <= 0;
                }
                yield true;
            }
        };
    }

    private NotificationPreference normalizeLegacyPreferences(NotificationPreference preference) {
        if (!shouldRepairLegacyPreferences(preference)) {
            return preference;
        }

        preference.setTransactionsEnabled(true);
        preference.setRequestsEnabled(true);
        preference.setAlertsEnabled(true);
        preference.setLowBalanceThreshold(BigDecimal.valueOf(100));

        if (preference.getId() != null) {
            return preferenceRepository.save(preference);
        }
        return preference;
    }

    private boolean shouldRepairLegacyPreferences(NotificationPreference preference) {
        if (preference.getLowBalanceThreshold() == null) {
            return true;
        }

        return !preference.isTransactionsEnabled()
                && !preference.isRequestsEnabled()
                && preference.isAlertsEnabled()
                && BigDecimal.valueOf(100).compareTo(preference.getLowBalanceThreshold()) == 0;
    }
}
