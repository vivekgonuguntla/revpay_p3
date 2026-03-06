package com.revpay.business.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "notification-service", url = "${notification.service.url:http://localhost:8083}")
public interface NotificationServiceClient {

    @PostMapping("/api/notifications/send")
    void sendNotification(@RequestBody NotificationRequest request);

    class NotificationRequest {
        public Long userId;
        public String title;
        public String message;
        public String type;
        public String category;
    }
}
