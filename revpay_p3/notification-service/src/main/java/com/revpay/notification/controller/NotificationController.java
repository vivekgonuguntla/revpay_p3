package com.revpay.notification.controller;

import com.revpay.notification.dto.NotificationResponse;
import com.revpay.notification.dto.UnreadCountResponse;
import com.revpay.notification.entity.NotificationCategory;
import com.revpay.notification.service.NotificationService;
import com.revpay.notification.util.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/notifications")
public class NotificationController {

    private static final Logger log = LoggerFactory.getLogger(NotificationController.class);

    private final NotificationService notificationService;
    private final JwtUtil jwtUtil;

    public NotificationController(NotificationService notificationService, JwtUtil jwtUtil) {
        this.notificationService = notificationService;
        this.jwtUtil = jwtUtil;
    }

    @GetMapping
    public ResponseEntity<List<NotificationResponse>> getNotifications(
            @RequestParam(required = false) NotificationCategory category,
            @RequestParam(required = false, defaultValue = "false") Boolean unreadOnly,
            HttpServletRequest request) {

        Long userId = jwtUtil.extractUserIdFromRequest(request);
        log.info("Fetching notifications for user: {}, category: {}, unreadOnly: {}", userId, category, unreadOnly);

        List<NotificationResponse> notifications = notificationService.getNotifications(userId, category, unreadOnly);
        return ResponseEntity.ok(notifications);
    }

    @GetMapping("/unread-count")
    public ResponseEntity<UnreadCountResponse> getUnreadCount(HttpServletRequest request) {
        Long userId = jwtUtil.extractUserIdFromRequest(request);
        log.info("Fetching unread count for user: {}", userId);

        Long count = notificationService.getUnreadCount(userId);
        return ResponseEntity.ok(UnreadCountResponse.builder().count(count).build());
    }

    @PatchMapping("/{id}/read")
    public ResponseEntity<NotificationResponse> markAsRead(
            @PathVariable Long id,
            HttpServletRequest request) {

        Long userId = jwtUtil.extractUserIdFromRequest(request);
        log.info("Marking notification {} as read for user: {}", id, userId);

        notificationService.markAsRead(id, userId);
        return ResponseEntity.ok(NotificationResponse.builder().id(id).isRead(true).build());
    }

    @PatchMapping("/read-all")
    public ResponseEntity<Map<String, String>> markAllAsRead(HttpServletRequest request) {
        Long userId = jwtUtil.extractUserIdFromRequest(request);
        log.info("Marking all notifications as read for user: {}", userId);

        notificationService.markAllAsRead(userId);
        return ResponseEntity.ok(Map.of("message", "All notifications marked as read"));
    }
}
