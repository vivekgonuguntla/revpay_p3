package com.revpay.business.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.Map;

@FeignClient(name = "card-service", url = "${card.service.url:http://localhost:8082}")
public interface CardServiceClient {

    @PostMapping("/api/v1/cards")
    Map<String, Object> addCard(@RequestHeader("X-User-Id") Long userId,
                                @RequestHeader("X-User-Email") String email,
                                @RequestHeader("X-User-Role") String role,
                                @RequestBody Map<String, Object> request);

    @DeleteMapping("/api/v1/cards/{id}")
    Map<String, String> deleteCard(@RequestHeader("X-User-Id") Long userId,
                                   @RequestHeader("X-User-Email") String email,
                                   @RequestHeader("X-User-Role") String role,
                                   @PathVariable("id") Long id);
}
