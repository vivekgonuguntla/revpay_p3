package com.revpay.wallet.service;

import com.revpay.wallet.client.AuthServiceClient;
import com.revpay.wallet.client.NotificationServiceClient;
import com.revpay.wallet.dto.CreateMoneyRequestDto;
import com.revpay.wallet.dto.MoneyRequestResponse;
import com.revpay.wallet.dto.RespondToRequestDto;
import com.revpay.wallet.dto.SendMoneyRequest;
import com.revpay.wallet.entity.MoneyRequest;
import com.revpay.wallet.entity.RequestStatus;
import com.revpay.wallet.repository.MoneyRequestRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MoneyRequestServiceTest {

    @Mock
    private MoneyRequestRepository moneyRequestRepository;

    @Mock
    private AuthServiceClient authServiceClient;

    @Mock
    private NotificationServiceClient notificationServiceClient;

    @InjectMocks
    private MoneyRequestService moneyRequestService;

    private CreateMoneyRequestDto createRequestDto;
    private RespondToRequestDto respondToRequestDto;
    private MoneyRequest moneyRequest;

    @BeforeEach
    void setUp() {
        createRequestDto = CreateMoneyRequestDto.builder()
                .payerEmail("payer@example.com")
                .amount(BigDecimal.valueOf(50.00))
                .note("Test request")
                .build();

        respondToRequestDto = RespondToRequestDto.builder()
                .accept(true)
                .pin("1234")
                .build();

        moneyRequest = MoneyRequest.builder()
                .id(1L)
                .requesterId(1L)
                .payerId(2L)
                .amount(BigDecimal.valueOf(50.00))
                .note("Test request")
                .status(RequestStatus.PENDING)
                .build();
    }

    @Test
    void createRequestShouldCreateSuccessfully() {
        when(authServiceClient.getUserByEmail(anyString(), eq("payer@example.com")))
                .thenReturn(Map.of("id", 2L));
        when(moneyRequestRepository.save(any(MoneyRequest.class))).thenReturn(moneyRequest);
        when(authServiceClient.getUserById(anyString(), eq(1L)))
                .thenReturn(Map.of("email", "requester@example.com"));

        MoneyRequestResponse response = moneyRequestService.createRequest(1L, createRequestDto, "token");

        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals(1L, response.getRequesterId());
        assertEquals(2L, response.getPayerId());
        assertEquals(BigDecimal.valueOf(50.00), response.getAmount());
        assertEquals("Test request", response.getNote());
        assertEquals(RequestStatus.PENDING, response.getStatus());
        assertEquals("OUTGOING", response.getDirection());

        verify(authServiceClient).getUserByEmail("token", "payer@example.com");
        verify(moneyRequestRepository).save(any(MoneyRequest.class));
        verify(notificationServiceClient, times(2)).sendNotification(any());
    }

    @Test
    void createRequestShouldThrowExceptionWhenPayerNotFound() {
        when(authServiceClient.getUserByEmail(anyString(), eq("payer@example.com")))
                .thenReturn(null);

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> moneyRequestService.createRequest(1L, createRequestDto, "token"));

        assertEquals("Payer not found: payer@example.com", exception.getMessage());
    }

    @Test
    void createRequestShouldThrowExceptionWhenRequestingFromSelf() {
        when(authServiceClient.getUserByEmail(anyString(), eq("payer@example.com")))
                .thenReturn(Map.of("id", 1L));

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> moneyRequestService.createRequest(1L, createRequestDto, "token"));

        assertEquals("Cannot request money from yourself", exception.getMessage());
    }

    @Test
    void getRequestsShouldReturnRequests() {
        when(moneyRequestRepository.findAllByUserId(1L)).thenReturn(List.of(moneyRequest));
        when(authServiceClient.getUserById(anyString(), anyLong()))
                .thenReturn(Map.of("email", "requester@example.com"), Map.of("email", "payer@example.com"));

        List<MoneyRequestResponse> responses = moneyRequestService.getRequests(1L, "token");

        assertNotNull(responses);
        assertEquals(1, responses.size());

        MoneyRequestResponse response = responses.get(0);
        assertEquals(1L, response.getId());
        assertEquals("requester@example.com", response.getRequesterEmail());
        assertEquals("payer@example.com", response.getPayerEmail());
        assertEquals("OUTGOING", response.getDirection());

        verify(moneyRequestRepository).findAllByUserId(1L);
    }

    @Test
    void respondToRequestShouldDeclineSuccessfully() {
        respondToRequestDto.setAccept(false);

        when(moneyRequestRepository.findById(1L)).thenReturn(Optional.of(moneyRequest));
        when(moneyRequestRepository.save(any(MoneyRequest.class))).thenReturn(moneyRequest);

        moneyRequestService.respondToRequest(2L, 1L, respondToRequestDto, "token");

        verify(moneyRequestRepository).save(any(MoneyRequest.class));
        verify(notificationServiceClient).sendNotification(any());
    }

    @Test
    void respondToRequestShouldThrowExceptionWhenRequestNotFound() {
        when(moneyRequestRepository.findById(1L)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> moneyRequestService.respondToRequest(2L, 1L, respondToRequestDto, "token"));

        assertEquals("Request not found", exception.getMessage());
    }

    @Test
    void respondToRequestShouldThrowExceptionWhenNotAuthorized() {
        when(moneyRequestRepository.findById(1L)).thenReturn(Optional.of(moneyRequest));

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> moneyRequestService.respondToRequest(1L, 1L, respondToRequestDto, "token"));

        assertEquals("Not authorized to respond to this request", exception.getMessage());
    }

    @Test
    void respondToRequestShouldThrowExceptionWhenRequestNotPending() {
        moneyRequest.setStatus(RequestStatus.ACCEPTED);

        when(moneyRequestRepository.findById(1L)).thenReturn(Optional.of(moneyRequest));

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> moneyRequestService.respondToRequest(2L, 1L, respondToRequestDto, "token"));

        assertEquals("Request is already ACCEPTED", exception.getMessage());
    }
}