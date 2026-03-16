package com.revpay.wallet.service;

import com.revpay.wallet.client.AuthServiceClient;
import com.revpay.wallet.client.NotificationServiceClient;
import com.revpay.wallet.dto.SendMoneyRequest;
import com.revpay.wallet.dto.TransactionHistoryResponse;
import com.revpay.wallet.dto.TransactionResponse;
import com.revpay.wallet.entity.Transaction;
import com.revpay.wallet.entity.TransactionStatus;
import com.revpay.wallet.entity.TransactionType;
import com.revpay.wallet.entity.Wallet;
import com.revpay.wallet.exception.InsufficientFundsException;
import com.revpay.wallet.exception.InvalidPinException;
import com.revpay.wallet.repository.TransactionRepository;
import com.revpay.wallet.repository.WalletRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransactionServiceTest {

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private WalletRepository walletRepository;

    @Mock
    private AuthServiceClient authServiceClient;

    @Mock
    private NotificationServiceClient notificationServiceClient;

    @InjectMocks
    private TransactionService transactionService;

    private Wallet senderWallet;
    private Wallet receiverWallet;
    private SendMoneyRequest sendMoneyRequest;
    private Transaction transaction;

    @BeforeEach
    void setUp() {
        senderWallet = Wallet.builder()
                .id(1L)
                .userId(1L)
                .balance(BigDecimal.valueOf(200.00))
                .currency("USD")
                .build();

        receiverWallet = Wallet.builder()
                .id(2L)
                .userId(2L)
                .balance(BigDecimal.valueOf(50.00))
                .currency("USD")
                .build();

        sendMoneyRequest = SendMoneyRequest.builder()
                .receiverEmail("receiver@example.com")
                .amount(BigDecimal.valueOf(50.00))
                .pin("1234")
                .description("Test transfer")
                .build();

        transaction = Transaction.builder()
                .id(1L)
                .senderWalletId(1L)
                .receiverWalletId(2L)
                .amount(BigDecimal.valueOf(50.00))
                .type(TransactionType.SEND)
                .status(TransactionStatus.COMPLETED)
                .description("Test transfer")
                .timestamp(LocalDateTime.now())
                .build();
    }

    @Test
    void sendMoneyShouldTransferSuccessfully() {
        when(authServiceClient.verifyPin(anyString(), anyMap()))
                .thenReturn(Map.of("valid", true));
        when(authServiceClient.getUserByEmail(anyString(), eq("receiver@example.com")))
                .thenReturn(Map.of("id", 2L));
        when(walletRepository.findByUserId(1L)).thenReturn(Optional.of(senderWallet));
        when(walletRepository.findByUserId(2L)).thenReturn(Optional.of(receiverWallet));
        doAnswer(invocation -> invocation.getArgument(0)).when(walletRepository).save(any(Wallet.class));
        when(transactionRepository.save(any(Transaction.class))).thenReturn(transaction);

        TransactionResponse response = transactionService.sendMoney(1L, sendMoneyRequest, "token");

        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals(1L, response.getSenderWalletId());
        assertEquals(2L, response.getReceiverWalletId());
        assertEquals(BigDecimal.valueOf(50.00), response.getAmount());
        assertEquals(TransactionType.SEND, response.getType());
        assertEquals(TransactionStatus.COMPLETED, response.getStatus());

        verify(authServiceClient).verifyPin("token", Map.of("pin", "1234"));
        verify(authServiceClient).getUserByEmail("token", "receiver@example.com");
        verify(walletRepository, times(2)).findByUserId(anyLong());
        verify(walletRepository, times(2)).save(any(Wallet.class));
        verify(transactionRepository).save(any(Transaction.class));
        verify(notificationServiceClient, times(3)).sendNotification(any());
    }

    @Test
    void sendMoneyShouldThrowExceptionWhenPinInvalid() {
        when(authServiceClient.verifyPin(anyString(), anyMap()))
                .thenThrow(new RuntimeException("Invalid PIN"));

        InvalidPinException exception = assertThrows(InvalidPinException.class,
                () -> transactionService.sendMoney(1L, sendMoneyRequest, "token"));

        assertEquals("PIN verification failed: Invalid PIN", exception.getMessage());
    }

    @Test
    void sendMoneyShouldThrowExceptionWhenReceiverNotFound() {
        when(authServiceClient.verifyPin(anyString(), anyMap()))
                .thenReturn(Map.of("valid", true));
        when(authServiceClient.getUserByEmail(anyString(), eq("receiver@example.com")))
                .thenReturn(null);

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> transactionService.sendMoney(1L, sendMoneyRequest, "token"));

        assertEquals("Receiver not found: receiver@example.com", exception.getMessage());
    }

    @Test
    void sendMoneyShouldThrowExceptionWhenSendingToSelf() {
        when(authServiceClient.verifyPin(anyString(), anyMap()))
                .thenReturn(Map.of("valid", true));
        when(authServiceClient.getUserByEmail(anyString(), eq("receiver@example.com")))
                .thenReturn(Map.of("id", 1L));

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> transactionService.sendMoney(1L, sendMoneyRequest, "token"));

        assertEquals("Cannot send money to yourself", exception.getMessage());
    }

    @Test
    void sendMoneyShouldThrowExceptionWhenInsufficientFunds() {
        sendMoneyRequest.setAmount(BigDecimal.valueOf(300.00));

        when(authServiceClient.verifyPin(anyString(), anyMap()))
                .thenReturn(Map.of("valid", true));
        when(authServiceClient.getUserByEmail(anyString(), eq("receiver@example.com")))
                .thenReturn(Map.of("id", 2L));
        when(walletRepository.findByUserId(1L)).thenReturn(Optional.of(senderWallet));

        InsufficientFundsException exception = assertThrows(InsufficientFundsException.class,
                () -> transactionService.sendMoney(1L, sendMoneyRequest, "token"));

        assertTrue(exception.getMessage().contains("Insufficient balance"));
    }

    @Test
    void sendMoneyShouldCreateWalletForReceiverIfNotExists() {
        when(authServiceClient.verifyPin(anyString(), anyMap()))
                .thenReturn(Map.of("valid", true));
        when(authServiceClient.getUserByEmail(anyString(), eq("receiver@example.com")))
                .thenReturn(Map.of("id", 2L));
        when(walletRepository.findByUserId(1L)).thenReturn(Optional.of(senderWallet));
        when(walletRepository.findByUserId(2L)).thenReturn(Optional.empty());
        doAnswer(invocation -> invocation.getArgument(0)).when(walletRepository).save(any(Wallet.class));
        when(transactionRepository.save(any(Transaction.class))).thenReturn(transaction);

        transactionService.sendMoney(1L, sendMoneyRequest, "token");

        ArgumentCaptor<Wallet> walletCaptor = ArgumentCaptor.forClass(Wallet.class);
        verify(walletRepository, times(3)).save(walletCaptor.capture());
        List<Wallet> savedWallets = walletCaptor.getAllValues();
        assertEquals(2L, savedWallets.get(0).getUserId());
    }

    @Test
    void getTransactionHistoryShouldReturnHistory() {
        when(walletRepository.findByUserId(1L)).thenReturn(Optional.of(senderWallet));
        when(transactionRepository.findAllByWalletId(1L)).thenReturn(List.of(transaction));
        when(walletRepository.findById(anyLong())).thenReturn(Optional.of(senderWallet), Optional.of(receiverWallet));
        when(authServiceClient.getUserById(anyString(), anyLong()))
                .thenReturn(Map.of("email", "sender@example.com"), Map.of("email", "receiver@example.com"));

        TransactionHistoryResponse response = transactionService.getTransactionHistory(1L, "token");

        assertNotNull(response);
        assertEquals(1, response.getTotalCount());
        assertEquals(1, response.getTransactions().size());

        TransactionResponse txResponse = response.getTransactions().get(0);
        assertEquals(1L, txResponse.getId());
        assertEquals("receiver@example.com", txResponse.getReceiverEmail());
        assertEquals("sender@example.com", txResponse.getSenderEmail());

        verify(walletRepository).findByUserId(1L);
        verify(transactionRepository).findAllByWalletId(1L);
    }

    @Test
    void getTransactionHistoryShouldThrowExceptionWhenWalletNotFound() {
        when(walletRepository.findByUserId(1L)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> transactionService.getTransactionHistory(1L, "token"));

        assertEquals("Wallet not found", exception.getMessage());
    }
}
