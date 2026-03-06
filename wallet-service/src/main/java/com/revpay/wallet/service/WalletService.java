package com.revpay.wallet.service;

import com.revpay.wallet.client.AuthServiceClient;
import com.revpay.wallet.client.CardServiceClient;
import com.revpay.wallet.dto.WalletOperationRequest;
import com.revpay.wallet.dto.WalletResponse;
import com.revpay.wallet.entity.*;
import com.revpay.wallet.exception.InsufficientFundsException;
import com.revpay.wallet.repository.TransactionRepository;
import com.revpay.wallet.repository.WalletRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class WalletService {

    private final WalletRepository walletRepository;
    private final TransactionRepository transactionRepository;
    private final CardServiceClient cardServiceClient;
    private final AuthServiceClient authServiceClient;

    @Transactional
    public WalletResponse getOrCreateWallet(Long userId) {
        log.info("Getting or creating wallet for user: {}", userId);

        Wallet wallet = walletRepository.findByUserId(userId)
            .orElseGet(() -> {
                log.info("Creating new wallet for user: {}", userId);
                Wallet newWallet = Wallet.builder()
                    .userId(userId)
                    .balance(BigDecimal.ZERO)
                    .currency("USD")
                    .build();
                return walletRepository.save(newWallet);
            });

        return mapToResponse(wallet);
    }

    public WalletResponse getBalance(Long userId) {
        log.info("Getting balance for user: {}", userId);

        Wallet wallet = walletRepository.findByUserId(userId)
            .orElseThrow(() -> new RuntimeException("Wallet not found for user: " + userId));

        return mapToResponse(wallet);
    }

    @Transactional
    public WalletResponse addFunds(Long userId, WalletOperationRequest request, String token) {
        log.info("Adding {} to wallet for user: {} using card: {}", request.getAmount(), userId, request.getCardId());

        if (request.getCardId() == null) {
            throw new RuntimeException("Card ID is required for adding funds");
        }

        // Validate card with card service
        try {
            Map<String, Object> cardValidation = cardServiceClient.validateCard(request.getCardId(), token);
            if (cardValidation == null || !Boolean.TRUE.equals(cardValidation.get("valid"))) {
                throw new RuntimeException("Invalid card");
            }
        } catch (Exception e) {
            log.error("Error validating card: {}", e.getMessage());
            throw new RuntimeException("Card validation failed: " + e.getMessage());
        }

        Wallet wallet = walletRepository.findByUserId(userId)
            .orElseThrow(() -> new RuntimeException("Wallet not found"));

        wallet.setBalance(wallet.getBalance().add(request.getAmount()));
        Wallet savedWallet = walletRepository.save(wallet);

        // Record transaction
        Transaction transaction = Transaction.builder()
            .receiverWalletId(wallet.getId())
            .amount(request.getAmount())
            .type(TransactionType.DEPOSIT)
            .status(TransactionStatus.COMPLETED)
            .description("Funded wallet using card ID: " + request.getCardId())
            .timestamp(LocalDateTime.now())
            .build();
        transactionRepository.save(transaction);

        log.info("Funds added successfully. New balance: {}", savedWallet.getBalance());
        return mapToResponse(savedWallet);
    }

    @Transactional
    public WalletResponse withdraw(Long userId, WalletOperationRequest request) {
        log.info("Withdrawing {} from wallet for user: {}", request.getAmount(), userId);

        Wallet wallet = walletRepository.findByUserId(userId)
            .orElseThrow(() -> new RuntimeException("Wallet not found"));

        if (wallet.getBalance().compareTo(request.getAmount()) < 0) {
            throw new InsufficientFundsException("Insufficient balance. Available: " + wallet.getBalance());
        }

        wallet.setBalance(wallet.getBalance().subtract(request.getAmount()));
        Wallet savedWallet = walletRepository.save(wallet);

        // Record transaction
        Transaction transaction = Transaction.builder()
            .senderWalletId(wallet.getId())
            .amount(request.getAmount())
            .type(TransactionType.WITHDRAWAL)
            .status(TransactionStatus.COMPLETED)
            .description("Withdrawn to primary card")
            .timestamp(LocalDateTime.now())
            .build();
        transactionRepository.save(transaction);

        log.info("Withdrawal successful. New balance: {}", savedWallet.getBalance());
        return mapToResponse(savedWallet);
    }

    private WalletResponse mapToResponse(Wallet wallet) {
        return WalletResponse.builder()
            .id(wallet.getId())
            .userId(wallet.getUserId())
            .balance(wallet.getBalance())
            .currency(wallet.getCurrency())
            .createdAt(wallet.getCreatedAt())
            .build();
    }
}
