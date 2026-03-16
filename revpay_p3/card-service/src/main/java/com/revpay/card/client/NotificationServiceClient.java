package com.revpay.card.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.time.LocalDateTime;
import java.util.Map;

@FeignClient(name = "notification-service", url = "${notification.service.url:http://localhost:8083}")
public interface NotificationServiceClient {

    @PostMapping("/api/internal/notifications")
    void sendNotification(@RequestBody NotificationRequest request);

    class NotificationRequest {
        public Long userId;
        public String category;
        public String type;
        public String title;
        public String message;
        public String counterparty;
        public String eventStatus;
        public String navigationTarget;
        public LocalDateTime eventTime;
        public String metadataJson;
    }
}
