package com.revpay.wallet.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "money_requests")
public class MoneyRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long requesterId;

    @Column(nullable = false)
    private Long payerId;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal amount;

    private String note;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RequestStatus status;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public MoneyRequest() {
    }

    public MoneyRequest(Long id, Long requesterId, Long payerId, BigDecimal amount, String note, RequestStatus status, LocalDateTime createdAt) {
        this.id = id;
        this.requesterId = requesterId;
        this.payerId = payerId;
        this.amount = amount;
        this.note = note;
        this.status = status;
        this.createdAt = createdAt;
    }

    public static MoneyRequestBuilder builder() {
        return new MoneyRequestBuilder();
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

    @PrePersist
    public void prePersist() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
        if (status == null) {
            status = RequestStatus.PENDING;
        }
    }

    public static class MoneyRequestBuilder {
        private Long id;
        private Long requesterId;
        private Long payerId;
        private BigDecimal amount;
        private String note;
        private RequestStatus status;
        private LocalDateTime createdAt;

        MoneyRequestBuilder() {
        }

        public MoneyRequestBuilder id(Long id) {
            this.id = id;
            return this;
        }

        public MoneyRequestBuilder requesterId(Long requesterId) {
            this.requesterId = requesterId;
            return this;
        }

        public MoneyRequestBuilder payerId(Long payerId) {
            this.payerId = payerId;
            return this;
        }

        public MoneyRequestBuilder amount(BigDecimal amount) {
            this.amount = amount;
            return this;
        }

        public MoneyRequestBuilder note(String note) {
            this.note = note;
            return this;
        }

        public MoneyRequestBuilder status(RequestStatus status) {
            this.status = status;
            return this;
        }

        public MoneyRequestBuilder createdAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public MoneyRequest build() {
            return new MoneyRequest(id, requesterId, payerId, amount, note, status, createdAt);
        }
    }
}
