package com.revpay.notification.controller;

import com.revpay.notification.dto.NotificationRequest;
import com.revpay.notification.dto.NotificationResponse;
import com.revpay.notification.service.NotificationService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/internal/notifications")
public class InternalController {

    private static final Logger log = LoggerFactory.getLogger(InternalController.class);

    private final NotificationService notificationService;

    public InternalController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @PostMapping
    public ResponseEntity<NotificationResponse> createNotification(@Valid @RequestBody NotificationRequest request) {
        log.info("Received internal request to create notification for user: {}", request.getUserId());

        NotificationResponse notification = notificationService.createNotification(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(notification);
    }
}
