package com.revpay.notification.service;

import com.revpay.notification.dto.NotificationRequest;
import com.revpay.notification.dto.NotificationResponse;
import com.revpay.notification.entity.Notification;
import com.revpay.notification.entity.NotificationCategory;
import com.revpay.notification.exception.ResourceNotFoundException;
import com.revpay.notification.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;

    @Override
    @Transactional
    public NotificationResponse createNotification(NotificationRequest request) {
        log.debug("Creating notification for user: {}", request.getUserId());

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

        if (unreadOnly != null && unreadOnly) {
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
}
