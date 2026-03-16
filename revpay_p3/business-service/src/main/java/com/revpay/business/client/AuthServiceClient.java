package com.revpay.business.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

@FeignClient(name = "auth-service", url = "${auth.service.url:http://localhost:8081}")
public interface AuthServiceClient {

    @GetMapping("/api/v1/auth/user-by-email")
    Map<String, Object> getUserByEmail(@RequestParam("email") String email);

    @GetMapping("/api/v1/auth/user/{userId}")
    Map<String, Object> getUserById(@PathVariable("userId") Long userId);
}
