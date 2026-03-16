package com.revpay.wallet.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@FeignClient(name = "auth-service", url = "${service.auth.url}")
public interface AuthServiceClient {

    @PostMapping("/api/v1/security/pin/verify")
    Map<String, Object> verifyPin(@RequestHeader("Authorization") String token, @RequestBody Map<String, String> request);

    @GetMapping("/api/v1/auth/user-by-email")
    Map<String, Object> getUserByEmail(@RequestHeader("Authorization") String token, @RequestParam("email") String email);

    @GetMapping("/api/v1/auth/user/{userId}")
    Map<String, Object> getUserById(@RequestHeader("Authorization") String token, @PathVariable("userId") Long userId);
}
