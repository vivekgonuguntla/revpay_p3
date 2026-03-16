package com.revpay.notification.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.revpay.notification.entity.NotificationCategory;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class NotificationResponse {
    private Long id;
    private Long userId;
    private NotificationCategory category;
    private String type;
    private String title;
    private String message;
    private BigDecimal amount;
    private String counterparty;
    private String eventStatus;
    private String navigationTarget;
    private LocalDateTime eventTime;
    private String metadataJson;
    private boolean isRead;
    private LocalDateTime createdAt;

    public NotificationResponse() {
    }

    public NotificationResponse(Long id, Long userId, NotificationCategory category, String type, String title, String message, BigDecimal amount, String counterparty, String eventStatus, String navigationTarget, LocalDateTime eventTime, String metadataJson, boolean isRead, LocalDateTime createdAt) {
        this.id = id;
        this.userId = userId;
        this.category = category;
        this.type = type;
        this.title = title;
        this.message = message;
        this.amount = amount;
        this.counterparty = counterparty;
        this.eventStatus = eventStatus;
        this.navigationTarget = navigationTarget;
        this.eventTime = eventTime;
        this.metadataJson = metadataJson;
        this.isRead = isRead;
        this.createdAt = createdAt;
    }

    public static NotificationResponseBuilder builder() {
        return new NotificationResponseBuilder();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public NotificationCategory getCategory() {
        return category;
    }

    public void setCategory(NotificationCategory category) {
        this.category = category;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getCounterparty() {
        return counterparty;
    }

    public void setCounterparty(String counterparty) {
        this.counterparty = counterparty;
    }

    public String getEventStatus() {
        return eventStatus;
    }

    @JsonProperty("status")
    public String getStatus() {
        return eventStatus;
    }

    public void setEventStatus(String eventStatus) {
        this.eventStatus = eventStatus;
    }

    public String getNavigationTarget() {
        return navigationTarget;
    }

    public void setNavigationTarget(String navigationTarget) {
        this.navigationTarget = navigationTarget;
    }

    public LocalDateTime getEventTime() {
        return eventTime;
    }

    public void setEventTime(LocalDateTime eventTime) {
        this.eventTime = eventTime;
    }

    public String getMetadataJson() {
        return metadataJson;
    }

    public void setMetadataJson(String metadataJson) {
        this.metadataJson = metadataJson;
    }

    public boolean isRead() {
        return isRead;
    }

    public void setRead(boolean read) {
        isRead = read;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public static class NotificationResponseBuilder {
        private Long id;
        private Long userId;
        private NotificationCategory category;
        private String type;
        private String title;
        private String message;
        private BigDecimal amount;
        private String counterparty;
        private String eventStatus;
        private String navigationTarget;
        private LocalDateTime eventTime;
        private String metadataJson;
        private boolean isRead;
        private LocalDateTime createdAt;

        NotificationResponseBuilder() {
        }

        public NotificationResponseBuilder id(Long id) {
            this.id = id;
            return this;
        }

        public NotificationResponseBuilder userId(Long userId) {
            this.userId = userId;
            return this;
        }

        public NotificationResponseBuilder category(NotificationCategory category) {
            this.category = category;
            return this;
        }

        public NotificationResponseBuilder type(String type) {
            this.type = type;
            return this;
        }

        public NotificationResponseBuilder title(String title) {
            this.title = title;
            return this;
        }

        public NotificationResponseBuilder message(String message) {
            this.message = message;
            return this;
        }

        public NotificationResponseBuilder amount(BigDecimal amount) {
            this.amount = amount;
            return this;
        }

        public NotificationResponseBuilder counterparty(String counterparty) {
            this.counterparty = counterparty;
            return this;
        }

        public NotificationResponseBuilder eventStatus(String eventStatus) {
            this.eventStatus = eventStatus;
            return this;
        }

        public NotificationResponseBuilder navigationTarget(String navigationTarget) {
            this.navigationTarget = navigationTarget;
            return this;
        }

        public NotificationResponseBuilder eventTime(LocalDateTime eventTime) {
            this.eventTime = eventTime;
            return this;
        }

        public NotificationResponseBuilder metadataJson(String metadataJson) {
            this.metadataJson = metadataJson;
            return this;
        }

        public NotificationResponseBuilder isRead(boolean isRead) {
            this.isRead = isRead;
            return this;
        }

        public NotificationResponseBuilder createdAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public NotificationResponse build() {
            return new NotificationResponse(id, userId, category, type, title, message, amount, counterparty, eventStatus, navigationTarget, eventTime, metadataJson, isRead, createdAt);
        }
    }
}
