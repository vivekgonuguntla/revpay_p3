package com.revpay.wallet.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@FeignClient(name = "card-service", url = "${service.card.url}")
public interface CardServiceClient {

    @GetMapping("/api/v1/cards/{cardId}/validate")
    Map<String, Object> validateCard(@PathVariable("cardId") Long cardId, @RequestHeader("Authorization") String token);
}
