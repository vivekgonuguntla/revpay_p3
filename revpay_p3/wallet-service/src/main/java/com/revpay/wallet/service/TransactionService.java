package com.revpay.wallet.service;

import com.revpay.wallet.client.AuthServiceClient;
import com.revpay.wallet.client.NotificationServiceClient;
import com.revpay.wallet.dto.SendMoneyRequest;
import com.revpay.wallet.dto.TransactionHistoryResponse;
import com.revpay.wallet.dto.TransactionResponse;
import com.revpay.wallet.entity.*;
import com.revpay.wallet.exception.InsufficientFundsException;
import com.revpay.wallet.exception.InvalidPinException;
import com.revpay.wallet.repository.TransactionRepository;
import com.revpay.wallet.repository.WalletRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final WalletRepository walletRepository;
    private final AuthServiceClient authServiceClient;
    private final NotificationServiceClient notificationServiceClient;

    @Transactional
    public TransactionResponse sendMoney(Long senderUserId, SendMoneyRequest request, String token) {
        log.info("Initiating transfer from user {} to {} amount {}", senderUserId, request.getReceiverEmail(), request.getAmount());

        // Verify PIN
        try {
            Map<String, Object> pinVerification = authServiceClient.verifyPin(token, Map.of("pin", request.getPin()));
            if (pinVerification == null || !Boolean.TRUE.equals(pinVerification.get("valid"))) {
                throw new InvalidPinException("Invalid PIN");
            }
        } catch (Exception e) {
            log.error("PIN verification failed: {}", e.getMessage());
            throw new InvalidPinException("PIN verification failed: " + e.getMessage());
        }

        // Get receiver by email
        Map<String, Object> receiverData;
        try {
            receiverData = authServiceClient.getUserByEmail(token, request.getReceiverEmail());
            if (receiverData == null || receiverData.get("id") == null) {
                throw new RuntimeException("Receiver not found: " + request.getReceiverEmail());
            }
        } catch (Exception e) {
            log.error("Error fetching receiver: {}", e.getMessage());
            throw new RuntimeException("Receiver not found: " + request.getReceiverEmail());
        }

        Long receiverUserId = ((Number) receiverData.get("id")).longValue();
        String receiverEmail = (String) receiverData.get("email");

        if (senderUserId.equals(receiverUserId)) {
            throw new RuntimeException("Cannot send money to yourself");
        }

        String senderEmail = null;
        try {
            Map<String, Object> senderData = authServiceClient.getUserById(token, senderUserId);
            senderEmail = (String) senderData.get("email");
        } catch (Exception e) {
            log.warn("Could not fetch sender email: {}", e.getMessage());
        }

        Wallet senderWallet = walletRepository.findByUserId(senderUserId)
            .orElseThrow(() -> new RuntimeException("Sender wallet not found"));

        Wallet receiverWallet = walletRepository.findByUserId(receiverUserId)
            .orElseGet(() -> {
                log.info("Creating wallet for receiver: {}", receiverUserId);
                Wallet newWallet = Wallet.builder()
                    .userId(receiverUserId)
                    .balance(java.math.BigDecimal.ZERO)
                    .currency("USD")
                    .build();
                return walletRepository.save(newWallet);
            });

        if (senderWallet.getBalance().compareTo(request.getAmount()) < 0) {
            throw new InsufficientFundsException("Insufficient balance. Available: " + senderWallet.getBalance());
        }

        // Deduct from sender
        senderWallet.setBalance(senderWallet.getBalance().subtract(request.getAmount()));
        Wallet updatedSenderWallet = walletRepository.save(senderWallet);

        // Add to receiver
        receiverWallet.setBalance(receiverWallet.getBalance().add(request.getAmount()));
        walletRepository.save(receiverWallet);

        // Create transaction record
        Transaction transaction = Transaction.builder()
            .senderWalletId(senderWallet.getId())
            .receiverWalletId(receiverWallet.getId())
            .amount(request.getAmount())
            .type(TransactionType.SEND)
            .status(TransactionStatus.COMPLETED)
            .description(request.getDescription())
            .timestamp(LocalDateTime.now())
            .build();

        Transaction savedTransaction = transactionRepository.save(transaction);

        sendTransactionNotification(senderUserId, "MONEY_SENT", "Money Sent",
                "You sent $" + request.getAmount() + " to " + receiverEmail,
                request.getAmount(), receiverEmail, "COMPLETED", savedTransaction.getId());
        sendTransactionNotification(receiverUserId, "MONEY_RECEIVED", "Money Received",
                "You received $" + request.getAmount() + " from " + (senderEmail != null ? senderEmail : "another user"),
                request.getAmount(), senderEmail, "COMPLETED", savedTransaction.getId());
        sendLowBalanceNotification(senderUserId, updatedSenderWallet.getBalance());

        log.info("Transaction completed successfully: {}", savedTransaction.getId());
        return mapToResponse(savedTransaction, receiverEmail, senderEmail);
    }

    public TransactionHistoryResponse getTransactionHistory(Long userId, String token) {
        log.info("Getting transaction history for user: {}", userId);

        Wallet wallet = walletRepository.findByUserId(userId)
            .orElseThrow(() -> new RuntimeException("Wallet not found"));

        List<Transaction> transactions = transactionRepository.findAllByWalletId(wallet.getId());

        List<TransactionResponse> responses = transactions.stream()
            .map(t -> {
                // Fetch user emails for display
                String senderEmail = null;
                String receiverEmail = null;

                if (t.getSenderWalletId() != null) {
                    try {
                        Wallet senderWallet = walletRepository.findById(t.getSenderWalletId()).orElse(null);
                        if (senderWallet != null) {
                            Map<String, Object> userData = authServiceClient.getUserById(token, senderWallet.getUserId());
                            senderEmail = (String) userData.get("email");
                        }
                    } catch (Exception e) {
                        log.warn("Could not fetch sender email: {}", e.getMessage());
                    }
                }

                if (t.getReceiverWalletId() != null) {
                    try {
                        Wallet receiverWallet = walletRepository.findById(t.getReceiverWalletId()).orElse(null);
                        if (receiverWallet != null) {
                            Map<String, Object> userData = authServiceClient.getUserById(token, receiverWallet.getUserId());
                            receiverEmail = (String) userData.get("email");
                        }
                    } catch (Exception e) {
                        log.warn("Could not fetch receiver email: {}", e.getMessage());
                    }
                }

                return mapToResponse(t, receiverEmail, senderEmail);
            })
            .collect(Collectors.toList());

        return TransactionHistoryResponse.builder()
            .transactions(responses)
            .totalCount(responses.size())
            .build();
    }

    private TransactionResponse mapToResponse(Transaction transaction, String receiverEmail, String senderEmail) {
        return TransactionResponse.builder()
            .id(transaction.getId())
            .senderWalletId(transaction.getSenderWalletId())
            .receiverWalletId(transaction.getReceiverWalletId())
            .senderEmail(senderEmail)
            .receiverEmail(receiverEmail)
            .amount(transaction.getAmount())
            .type(transaction.getType())
            .status(transaction.getStatus())
            .description(transaction.getDescription())
            .timestamp(transaction.getTimestamp())
            .build();
    }

    private void sendTransactionNotification(Long userId, String type, String title, String message,
                                             java.math.BigDecimal amount, String counterparty,
                                             String eventStatus, Long transactionId) {
        try {
            NotificationServiceClient.NotificationRequest notification =
                    new NotificationServiceClient.NotificationRequest();
            notification.userId = userId;
            notification.category = "TRANSACTIONS";
            notification.type = type;
            notification.title = title;
            notification.message = message;
            notification.amount = amount;
            notification.counterparty = counterparty;
            notification.eventStatus = eventStatus;
            notification.navigationTarget = "/transactions/" + transactionId;
            notification.eventTime = LocalDateTime.now();
            notification.metadataJson = "{\"transactionId\":" + transactionId + "}";
            notificationServiceClient.sendNotification(notification);
        } catch (Exception e) {
            log.error("Error sending notification: {}", e.getMessage());
        }
    }

    private void sendLowBalanceNotification(Long userId, java.math.BigDecimal balance) {
        try {
            NotificationServiceClient.NotificationRequest notification =
                    new NotificationServiceClient.NotificationRequest();
            notification.userId = userId;
            notification.category = "ALERTS";
            notification.type = "LOW_BALANCE";
            notification.title = "Low Balance Alert";
            notification.message = "Your wallet balance is low.";
            notification.amount = balance;
            notification.eventStatus = "ACTIVE";
            notification.navigationTarget = "/wallet";
            notification.eventTime = LocalDateTime.now();
            notificationServiceClient.sendNotification(notification);
        } catch (Exception e) {
            log.error("Error sending low balance notification: {}", e.getMessage());
        }
    }
}
