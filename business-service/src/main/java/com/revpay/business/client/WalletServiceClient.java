package com.revpay.business.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.math.BigDecimal;

@FeignClient(name = "wallet-service", url = "${wallet.service.url:http://localhost:8082}")
public interface WalletServiceClient {

    @GetMapping("/api/wallets/user/{userId}/balance")
    WalletBalanceResponse getWalletBalance(@PathVariable Long userId);

    @PostMapping("/api/wallets/debit")
    void debitWallet(@RequestBody WalletTransactionRequest request);

    @PostMapping("/api/wallets/credit")
    void creditWallet(@RequestBody WalletTransactionRequest request);

    class WalletBalanceResponse {
        public BigDecimal balance;
        public String currency;
    }

    class WalletTransactionRequest {
        public Long userId;
        public BigDecimal amount;
        public String currency;
        public String description;
    }
}
