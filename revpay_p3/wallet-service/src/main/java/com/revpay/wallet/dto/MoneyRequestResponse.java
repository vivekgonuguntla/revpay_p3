package com.revpay.wallet.dto;

import com.revpay.wallet.entity.RequestStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class MoneyRequestResponse {
    private Long id;
    private Long requesterId;
    private Long payerId;
    private String requesterEmail;
    private String payerEmail;
    private BigDecimal amount;
    private String note;
    private RequestStatus status;
    private LocalDateTime createdAt;
    private String direction; // INCOMING or OUTGOING

    public MoneyRequestResponse() {
    }

    public MoneyRequestResponse(Long id, Long requesterId, Long payerId, String requesterEmail, String payerEmail, BigDecimal amount, String note, RequestStatus status, LocalDateTime createdAt, String direction) {
        this.id = id;
        this.requesterId = requesterId;
        this.payerId = payerId;
        this.requesterEmail = requesterEmail;
        this.payerEmail = payerEmail;
        this.amount = amount;
        this.note = note;
        this.status = status;
        this.createdAt = createdAt;
        this.direction = direction;
    }

    public static MoneyRequestResponseBuilder builder() {
        return new MoneyRequestResponseBuilder();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getRequesterId() {
        return requesterId;
    }

    public void setRequesterId(Long requesterId) {
        this.requesterId = requesterId;
    }

    public Long getPayerId() {
        return payerId;
    }

    public void setPayerId(Long payerId) {
        this.payerId = payerId;
    }

    public String getRequesterEmail() {
        return requesterEmail;
    }

    public void setRequesterEmail(String requesterEmail) {
        this.requesterEmail = requesterEmail;
    }

    public String getPayerEmail() {
        return payerEmail;
    }

    public void setPayerEmail(String payerEmail) {
        this.payerEmail = payerEmail;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public RequestStatus getStatus() {
        return status;
    }

    public void setStatus(RequestStatus status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public String getDirection() {
        return direction;
    }

    public void setDirection(String direction) {
        this.direction = direction;
    }

    public static class MoneyRequestResponseBuilder {
        private Long id;
        private Long requesterId;
        private Long payerId;
        private String requesterEmail;
        private String payerEmail;
        private BigDecimal amount;
        private String note;
        private RequestStatus status;
        private LocalDateTime createdAt;
        private String direction;

        MoneyRequestResponseBuilder() {
        }

        public MoneyRequestResponseBuilder id(Long id) {
            this.id = id;
            return this;
        }

        public MoneyRequestResponseBuilder requesterId(Long requesterId) {
            this.requesterId = requesterId;
            return this;
        }

        public MoneyRequestResponseBuilder payerId(Long payerId) {
            this.payerId = payerId;
            return this;
        }

        public MoneyRequestResponseBuilder requesterEmail(String requesterEmail) {
            this.requesterEmail = requesterEmail;
            return this;
        }

        public MoneyRequestResponseBuilder payerEmail(String payerEmail) {
            this.payerEmail = payerEmail;
            return this;
        }

        public MoneyRequestResponseBuilder amount(BigDecimal amount) {
            this.amount = amount;
            return this;
        }

        public MoneyRequestResponseBuilder note(String note) {
            this.note = note;
            return this;
        }

        public MoneyRequestResponseBuilder status(RequestStatus status) {
            this.status = status;
            return this;
        }

        public MoneyRequestResponseBuilder createdAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public MoneyRequestResponseBuilder direction(String direction) {
            this.direction = direction;
            return this;
        }

        public MoneyRequestResponse build() {
            return new MoneyRequestResponse(id, requesterId, payerId, requesterEmail, payerEmail, amount, note, status, createdAt, direction);
        }
    }
}
