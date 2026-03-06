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
    public MoneyRequestResponse createRequest(Long requesterUserId, CreateMoneyRequestDto dto) {
        log.info("Creating money request from user {} to {}", requesterUserId, dto.getPayerEmail());

        // Get payer by email
        Map<String, Object> payerData;
        try {
            payerData = authServiceClient.getUserByEmail(dto.getPayerEmail());
            if (payerData == null || payerData.get("id") == null) {
                throw new RuntimeException("Payer not found: " + dto.getPayerEmail());
            }
        } catch (Exception e) {
            log.error("Error fetching payer: {}", e.getMessage());
            throw new RuntimeException("Payer not found: " + dto.getPayerEmail());
        }

        Long payerUserId = ((Number) payerData.get("id")).longValue();

        if (requesterUserId.equals(payerUserId)) {
            throw new RuntimeException("Cannot request money from yourself");
        }

        MoneyRequest request = MoneyRequest.builder()
            .requesterId(requesterUserId)
            .payerId(payerUserId)
            .amount(dto.getAmount())
            .note(dto.getNote())
            .status(RequestStatus.PENDING)
            .build();

        MoneyRequest savedRequest = moneyRequestRepository.save(request);

        // Send notifications
        try {
            notificationServiceClient.sendNotification(Map.of(
                "userId", requesterUserId,
                "type", "MONEY_REQUEST_SENT",
                "message", "Money request sent to " + dto.getPayerEmail() + " for $" + dto.getAmount(),
                "requestId", savedRequest.getId()
            ));

            notificationServiceClient.sendNotification(Map.of(
                "userId", payerUserId,
                "type", "MONEY_REQUEST_RECEIVED",
                "message", "Money request received for $" + dto.getAmount(),
                "requestId", savedRequest.getId()
            ));
        } catch (Exception e) {
            log.error("Error sending notifications: {}", e.getMessage());
        }

        log.info("Money request created successfully: {}", savedRequest.getId());

        // Get requester email for response
        String requesterEmail = null;
        try {
            Map<String, Object> requesterData = authServiceClient.getUserById(requesterUserId);
            requesterEmail = (String) requesterData.get("email");
        } catch (Exception e) {
            log.warn("Could not fetch requester email: {}", e.getMessage());
        }

        return mapToResponse(savedRequest, requesterEmail, dto.getPayerEmail(), requesterUserId);
    }

    public List<MoneyRequestResponse> getRequests(Long userId) {
        log.info("Getting money requests for user: {}", userId);

        List<MoneyRequest> requests = moneyRequestRepository.findAllByUserId(userId);

        return requests.stream()
            .map(r -> {
                String requesterEmail = null;
                String payerEmail = null;

                try {
                    Map<String, Object> requesterData = authServiceClient.getUserById(r.getRequesterId());
                    requesterEmail = (String) requesterData.get("email");
                } catch (Exception e) {
                    log.warn("Could not fetch requester email: {}", e.getMessage());
                }

                try {
                    Map<String, Object> payerData = authServiceClient.getUserById(r.getPayerId());
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
            Map<String, Object> requesterData = authServiceClient.getUserById(request.getRequesterId());
            String requesterEmail = (String) requesterData.get("email");

            // Initiate transaction
            SendMoneyRequest sendRequest = SendMoneyRequest.builder()
                .receiverEmail(requesterEmail)
                .amount(request.getAmount())
                .description("Payment for request: " + request.getNote())
                .pin(dto.getPin())
                .build();

            transactionService.sendMoney(userId, sendRequest, token);
            request.setStatus(RequestStatus.ACCEPTED);

            // Send notification to requester
            try {
                notificationServiceClient.sendNotification(Map.of(
                    "userId", request.getRequesterId(),
                    "type", "MONEY_REQUEST_ACCEPTED",
                    "message", "Your money request for $" + request.getAmount() + " was accepted",
                    "requestId", request.getId()
                ));
            } catch (Exception e) {
                log.error("Error sending notification: {}", e.getMessage());
            }
        } else {
            request.setStatus(RequestStatus.DECLINED);

            // Send notification to requester
            try {
                notificationServiceClient.sendNotification(Map.of(
                    "userId", request.getRequesterId(),
                    "type", "MONEY_REQUEST_DECLINED",
                    "message", "Your money request for $" + request.getAmount() + " was declined",
                    "requestId", request.getId()
                ));
            } catch (Exception e) {
                log.error("Error sending notification: {}", e.getMessage());
            }
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
}
