package com.revpay.card.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

@FeignClient(name = "notification-service", url = "${notification.service.url:http://localhost:8083}")
public interface NotificationServiceClient {

    @PostMapping("/api/v1/notifications/send")
    void sendNotification(
            @RequestParam("userId") Long userId,
            @RequestParam("type") String type,
            @RequestParam("title") String title,
            @RequestParam("message") String message,
            @RequestParam("metadata") Map<String, Object> metadata
    );
}
