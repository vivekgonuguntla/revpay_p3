package com.revpay.wallet.service;

import com.revpay.wallet.client.AuthServiceClient;
import com.revpay.wallet.client.CardServiceClient;
import com.revpay.wallet.client.NotificationServiceClient;
import com.revpay.wallet.dto.WalletOperationRequest;
import com.revpay.wallet.dto.WalletResponse;
import com.revpay.wallet.entity.Transaction;
import com.revpay.wallet.entity.TransactionStatus;
import com.revpay.wallet.entity.TransactionType;
import com.revpay.wallet.entity.Wallet;
import com.revpay.wallet.exception.InsufficientFundsException;
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
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WalletServiceTest {

    @Mock
    private WalletRepository walletRepository;

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private CardServiceClient cardServiceClient;

    @Mock
    private AuthServiceClient authServiceClient;

    @Mock
    private NotificationServiceClient notificationServiceClient;

    @InjectMocks
    private WalletService walletService;

    private Wallet wallet;
    private WalletOperationRequest operationRequest;

    @BeforeEach
    void setUp() {
        wallet = Wallet.builder()
                .id(1L)
                .userId(1L)
                .balance(BigDecimal.valueOf(100.00))
                .currency("USD")
                .createdAt(LocalDateTime.now())
                .build();

        operationRequest = WalletOperationRequest.builder()
                .amount(BigDecimal.valueOf(50.00))
                .cardId(123L)
                .build();
    }

    @Test
    void getOrCreateWalletShouldReturnExistingWallet() {
        when(walletRepository.findByUserId(1L)).thenReturn(Optional.of(wallet));

        WalletResponse response = walletService.getOrCreateWallet(1L);

        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals(1L, response.getUserId());
        assertEquals(BigDecimal.valueOf(100.00), response.getBalance());
        assertEquals("USD", response.getCurrency());

        verify(walletRepository).findByUserId(1L);
        verify(walletRepository, never()).save(any(Wallet.class));
    }

    @Test
    void getOrCreateWalletShouldCreateNewWallet() {
        when(walletRepository.findByUserId(1L)).thenReturn(Optional.empty());
        when(walletRepository.save(any(Wallet.class))).thenReturn(wallet);

        WalletResponse response = walletService.getOrCreateWallet(1L);

        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals(1L, response.getUserId());
        assertEquals(BigDecimal.valueOf(100.00), response.getBalance());
        assertEquals("USD", response.getCurrency());

        verify(walletRepository).findByUserId(1L);
        ArgumentCaptor<Wallet> walletCaptor = ArgumentCaptor.forClass(Wallet.class);
        verify(walletRepository).save(walletCaptor.capture());
        assertEquals(1L, walletCaptor.getValue().getUserId());
        assertEquals(BigDecimal.ZERO, walletCaptor.getValue().getBalance());
        assertEquals("USD", walletCaptor.getValue().getCurrency());
    }

    @Test
    void getBalanceShouldReturnWalletBalance() {
        when(walletRepository.findByUserId(1L)).thenReturn(Optional.of(wallet));

        WalletResponse response = walletService.getBalance(1L);

        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals(1L, response.getUserId());
        assertEquals(BigDecimal.valueOf(100.00), response.getBalance());
        assertEquals("USD", response.getCurrency());

        verify(walletRepository).findByUserId(1L);
    }

    @Test
    void getBalanceShouldThrowExceptionWhenWalletNotFound() {
        when(walletRepository.findByUserId(1L)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> walletService.getBalance(1L));

        assertEquals("Wallet not found for user: 1", exception.getMessage());
    }

    @Test
    void addFundsShouldAddFundsSuccessfully() {
        when(cardServiceClient.validateCard(eq(123L), anyString()))
                .thenReturn(Map.of("valid", true));
        when(walletRepository.findByUserId(1L)).thenReturn(Optional.of(wallet));
        doAnswer(invocation -> invocation.getArgument(0)).when(walletRepository).save(any(Wallet.class));
        when(transactionRepository.save(any(Transaction.class))).thenReturn(new Transaction());

        WalletResponse response = walletService.addFunds(1L, operationRequest, "token");

        assertNotNull(response);
        assertEquals(BigDecimal.valueOf(150.00), response.getBalance());

        verify(cardServiceClient).validateCard(123L, "token");
        verify(walletRepository).findByUserId(1L);
        verify(walletRepository).save(any(Wallet.class));
        verify(transactionRepository).save(any(Transaction.class));
        verify(notificationServiceClient).sendNotification(any());
    }

    @Test
    void addFundsShouldThrowExceptionWhenCardIdIsNull() {
        operationRequest.setCardId(null);

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> walletService.addFunds(1L, operationRequest, "token"));

        assertEquals("Card ID is required for adding funds", exception.getMessage());
    }

    @Test
    void addFundsShouldThrowExceptionWhenCardInvalid() {
        when(cardServiceClient.validateCard(eq(123L), anyString()))
                .thenThrow(new RuntimeException("Invalid card"));

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> walletService.addFunds(1L, operationRequest, "token"));

        assertEquals("Card validation failed: Invalid card", exception.getMessage());
    }

    @Test
    void withdrawShouldWithdrawSuccessfully() {
        when(walletRepository.findByUserId(1L)).thenReturn(Optional.of(wallet));
        doAnswer(invocation -> invocation.getArgument(0)).when(walletRepository).save(any(Wallet.class));
        when(transactionRepository.save(any(Transaction.class))).thenReturn(new Transaction());

        WalletResponse response = walletService.withdraw(1L, operationRequest);

        assertNotNull(response);
        assertEquals(BigDecimal.valueOf(50.00), response.getBalance());

        verify(walletRepository).findByUserId(1L);
        verify(walletRepository).save(any(Wallet.class));
        verify(transactionRepository).save(any(Transaction.class));
        verify(notificationServiceClient, times(2)).sendNotification(any());
    }

    @Test
    void withdrawShouldThrowExceptionWhenInsufficientFunds() {
        operationRequest.setAmount(BigDecimal.valueOf(200.00));

        when(walletRepository.findByUserId(1L)).thenReturn(Optional.of(wallet));

        InsufficientFundsException exception = assertThrows(InsufficientFundsException.class,
                () -> walletService.withdraw(1L, operationRequest));

        assertTrue(exception.getMessage().contains("Insufficient balance"));
    }

    @Test
    void withdrawShouldSendLowBalanceNotificationWhenBalanceLow() {
        wallet.setBalance(BigDecimal.valueOf(60.00));
        operationRequest.setAmount(BigDecimal.valueOf(10.00));

        when(walletRepository.findByUserId(1L)).thenReturn(Optional.of(wallet));
        doAnswer(invocation -> invocation.getArgument(0)).when(walletRepository).save(any(Wallet.class));
        when(transactionRepository.save(any(Transaction.class))).thenReturn(new Transaction());

        WalletResponse response = walletService.withdraw(1L, operationRequest);

        assertNotNull(response);
        assertEquals(BigDecimal.valueOf(50.00), response.getBalance());

        verify(notificationServiceClient, times(2)).sendNotification(any());
    }
}