package com.revpay.wallet.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@FeignClient(name = "notification-service", url = "${service.notification.url}")
public interface NotificationServiceClient {

    @PostMapping("/api/internal/notifications")
    void sendNotification(@RequestBody NotificationRequest notification);

    class NotificationRequest {
        public Long userId;
        public String category;
        public String type;
        public String title;
        public String message;
        public BigDecimal amount;
        public String counterparty;
        public String eventStatus;
        public String navigationTarget;
        public LocalDateTime eventTime;
        public String metadataJson;
    }
}
