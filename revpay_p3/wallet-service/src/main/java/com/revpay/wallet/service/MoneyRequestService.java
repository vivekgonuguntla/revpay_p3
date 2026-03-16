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
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class MoneyRequestService {

    private final MoneyRequestRepository moneyRequestRepository;
    private final AuthServiceClient authServiceClient;
    private final NotificationServiceClient notificationServiceClient;
    private final TransactionService transactionService;

    @Transactional
    public MoneyRequestResponse createRequest(Long requesterUserId, CreateMoneyRequestDto dto, String token) {
        log.info("Creating money request from user {} to {}", requesterUserId, dto.getPayerEmail());

        // Get payer by email
        Map<String, Object> payerData;
        try {
            payerData = authServiceClient.getUserByEmail(token, dto.getPayerEmail());
            if (payerData == null || payerData.get("id") == null) {
                throw new RuntimeException("Payer not found: " + dto.getPayerEmail());
            }
        } catch (Exception e) {
            log.error("Error fetching payer: {}", e.getMessage());
            throw new RuntimeException("Payer not found: " + dto.getPayerEmail());
        }

        Long payerUserId = ((Number) payerData.get("id")).longValue();
        String payerEmail = (String) payerData.get("email");

        if (requesterUserId.equals(payerUserId)) {
            throw new RuntimeException("Cannot request money from yourself");
        }

        String requesterEmail = null;
        try {
            Map<String, Object> requesterData = authServiceClient.getUserById(token, requesterUserId);
            requesterEmail = (String) requesterData.get("email");
        } catch (Exception e) {
            log.warn("Could not fetch requester email: {}", e.getMessage());
        }

        MoneyRequest request = MoneyRequest.builder()
            .requesterId(requesterUserId)
            .payerId(payerUserId)
            .amount(dto.getAmount())
            .note(dto.getNote())
            .status(RequestStatus.PENDING)
            .build();

        MoneyRequest savedRequest = moneyRequestRepository.save(request);

        sendRequestNotification(requesterUserId, "MONEY_REQUEST_SENT", "Request Sent",
                "Money request sent to " + payerEmail + " for $" + dto.getAmount(),
                dto.getAmount(), payerEmail, "PENDING", savedRequest.getId());
        sendRequestNotification(payerUserId, "MONEY_REQUEST_RECEIVED", "Money Request Received",
                "Money request received from " + (requesterEmail != null ? requesterEmail : "another user") + " for $" + dto.getAmount(),
                dto.getAmount(), requesterEmail, "PENDING", savedRequest.getId());

        log.info("Money request created successfully: {}", savedRequest.getId());
        return mapToResponse(savedRequest, requesterEmail, payerEmail, requesterUserId);
    }

    public List<MoneyRequestResponse> getRequests(Long userId, String token) {
        log.info("Getting money requests for user: {}", userId);

        List<MoneyRequest> requests = moneyRequestRepository.findAllByUserId(userId);

        return requests.stream()
            .map(r -> {
                String requesterEmail = null;
                String payerEmail = null;

                try {
                    Map<String, Object> requesterData = authServiceClient.getUserById(token, r.getRequesterId());
                    requesterEmail = (String) requesterData.get("email");
                } catch (Exception e) {
                    log.warn("Could not fetch requester email: {}", e.getMessage());
                }

                try {
                    Map<String, Object> payerData = authServiceClient.getUserById(token, r.getPayerId());
                    payerEmail = (String) payerData.get("email");
                } catch (Exception e) {
                    log.warn("Could not fetch payer email: {}", e.getMessage());
                }

                return mapToResponse(r, requesterEmail, payerEmail, userId);
            })
            .collect(Collectors.toList());
    }

    @Transactional
    public void respondToRequest(Long userId, Long requestId, RespondToRequestDto dto, String token) {
        log.info("User {} responding to money request {}: {}", userId, requestId, dto.getAccept());

        MoneyRequest request = moneyRequestRepository.findById(requestId)
            .orElseThrow(() -> new RuntimeException("Request not found"));

        if (!request.getPayerId().equals(userId)) {
            throw new RuntimeException("Not authorized to respond to this request");
        }

        if (request.getStatus() != RequestStatus.PENDING) {
            throw new RuntimeException("Request is already " + request.getStatus());
        }

        if (dto.getAccept()) {
            // Get requester email
            Map<String, Object> requesterData = authServiceClient.getUserById(token, request.getRequesterId());
            String requesterEmail = (String) requesterData.get("email");
            String payerEmail = null;
            try {
                Map<String, Object> payerData = authServiceClient.getUserById(token, userId);
                payerEmail = (String) payerData.get("email");
            } catch (Exception e) {
                log.warn("Could not fetch payer email: {}", e.getMessage());
            }

            // Initiate transaction
            SendMoneyRequest sendRequest = SendMoneyRequest.builder()
                .receiverEmail(requesterEmail)
                .amount(request.getAmount())
                .description("Payment for request: " + request.getNote())
                .pin(dto.getPin())
                .build();

            transactionService.sendMoney(userId, sendRequest, token);
            request.setStatus(RequestStatus.ACCEPTED);

            sendRequestNotification(request.getRequesterId(), "MONEY_REQUEST_ACCEPTED", "Request Accepted",
                    "Your money request for $" + request.getAmount() + " was accepted",
                    request.getAmount(), payerEmail, "ACCEPTED", request.getId());
            sendRequestNotification(request.getPayerId(), "MONEY_REQUEST_COMPLETED", "Request Completed",
                    "You paid $" + request.getAmount() + " for a money request",
                    request.getAmount(), requesterEmail, "ACCEPTED", request.getId());
        } else {
            request.setStatus(RequestStatus.DECLINED);
            String requesterEmail = null;
            try {
                Map<String, Object> requesterData = authServiceClient.getUserById(token, request.getRequesterId());
                requesterEmail = (String) requesterData.get("email");
            } catch (Exception e) {
                log.warn("Could not fetch requester email: {}", e.getMessage());
            }
            sendRequestNotification(request.getRequesterId(), "MONEY_REQUEST_DECLINED", "Request Declined",
                    "Your money request for $" + request.getAmount() + " was declined",
                    request.getAmount(), null, "DECLINED", request.getId());
            sendRequestNotification(request.getPayerId(), "MONEY_REQUEST_COMPLETED", "Request Declined",
                    "You declined a money request for $" + request.getAmount(),
                    request.getAmount(), requesterEmail, "DECLINED", request.getId());
        }

        moneyRequestRepository.save(request);
        log.info("Money request {} response completed", requestId);
    }

    private MoneyRequestResponse mapToResponse(MoneyRequest request, String requesterEmail, String payerEmail, Long currentUserId) {
        String direction = request.getRequesterId().equals(currentUserId) ? "OUTGOING" : "INCOMING";

        return MoneyRequestResponse.builder()
            .id(request.getId())
            .requesterId(request.getRequesterId())
            .payerId(request.getPayerId())
            .requesterEmail(requesterEmail)
            .payerEmail(payerEmail)
            .amount(request.getAmount())
            .note(request.getNote())
            .status(request.getStatus())
            .createdAt(request.getCreatedAt())
            .direction(direction)
            .build();
    }

    private void sendRequestNotification(Long userId, String type, String title, String message,
                                         java.math.BigDecimal amount, String counterparty,
                                         String eventStatus, Long requestId) {
        try {
            NotificationServiceClient.NotificationRequest notification =
                    new NotificationServiceClient.NotificationRequest();
            notification.userId = userId;
            notification.category = "REQUESTS";
            notification.type = type;
            notification.title = title;
            notification.message = message;
            notification.amount = amount;
            notification.counterparty = counterparty;
            notification.eventStatus = eventStatus;
            notification.navigationTarget = "/requests/" + requestId;
            notification.eventTime = java.time.LocalDateTime.now();
            notification.metadataJson = "{\"requestId\":" + requestId + "}";
            notificationServiceClient.sendNotification(notification);
        } catch (Exception e) {
            log.error("Error sending notification: {}", e.getMessage());
        }
    }
}
