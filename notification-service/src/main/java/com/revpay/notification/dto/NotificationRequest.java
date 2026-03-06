package com.revpay.notification.dto;

import com.revpay.notification.entity.NotificationCategory;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class NotificationRequest {

    @NotNull(message = "User ID is required")
    private Long userId;

    @NotNull(message = "Category is required")
    private NotificationCategory category;

    @NotBlank(message = "Type is required")
    private String type;

    @NotBlank(message = "Title is required")
    private String title;

    @NotBlank(message = "Message is required")
    private String message;

    private BigDecimal amount;
    private String counterparty;
    private String eventStatus;
    private String navigationTarget;
    private LocalDateTime eventTime;
    private String metadataJson;

    public NotificationRequest() {
    }

    public NotificationRequest(Long userId, NotificationCategory category, String type, String title, String message, BigDecimal amount, String counterparty, String eventStatus, String navigationTarget, LocalDateTime eventTime, String metadataJson) {
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
    }

    public static NotificationRequestBuilder builder() {
        return new NotificationRequestBuilder();
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

    public static class NotificationRequestBuilder {
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

        NotificationRequestBuilder() {
        }

        public NotificationRequestBuilder userId(Long userId) {
            this.userId = userId;
            return this;
        }

        public NotificationRequestBuilder category(NotificationCategory category) {
            this.category = category;
            return this;
        }

        public NotificationRequestBuilder type(String type) {
            this.type = type;
            return this;
        }

        public NotificationRequestBuilder title(String title) {
            this.title = title;
            return this;
        }

        public NotificationRequestBuilder message(String message) {
            this.message = message;
            return this;
        }

        public NotificationRequestBuilder amount(BigDecimal amount) {
            this.amount = amount;
            return this;
        }

        public NotificationRequestBuilder counterparty(String counterparty) {
            this.counterparty = counterparty;
            return this;
        }

        public NotificationRequestBuilder eventStatus(String eventStatus) {
            this.eventStatus = eventStatus;
            return this;
        }

        public NotificationRequestBuilder navigationTarget(String navigationTarget) {
            this.navigationTarget = navigationTarget;
            return this;
        }

        public NotificationRequestBuilder eventTime(LocalDateTime eventTime) {
            this.eventTime = eventTime;
            return this;
        }

        public NotificationRequestBuilder metadataJson(String metadataJson) {
            this.metadataJson = metadataJson;
            return this;
        }

        public NotificationRequest build() {
            return new NotificationRequest(userId, category, type, title, message, amount, counterparty, eventStatus, navigationTarget, eventTime, metadataJson);
        }
    }
}
