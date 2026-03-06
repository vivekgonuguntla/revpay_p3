package com.revpay.notification.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "notifications")
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long userId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NotificationCategory category;

    @Column(nullable = false)
    private String type;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String message;

    @Column(precision = 19, scale = 2)
    private BigDecimal amount;

    private String counterparty;

    private String eventStatus;

    private String navigationTarget;

    private LocalDateTime eventTime;

    @Column(columnDefinition = "TEXT")
    private String metadataJson;

    @Column(nullable = false)
    private boolean isRead = false;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public Notification() {
    }

    public Notification(Long id, Long userId, NotificationCategory category, String type, String title, String message, BigDecimal amount, String counterparty, String eventStatus, String navigationTarget, LocalDateTime eventTime, String metadataJson, boolean isRead, LocalDateTime createdAt) {
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

    public static NotificationBuilder builder() {
        return new NotificationBuilder();
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

    public static class NotificationBuilder {
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
        private boolean isRead = false;
        private LocalDateTime createdAt;

        NotificationBuilder() {
        }

        public NotificationBuilder id(Long id) {
            this.id = id;
            return this;
        }

        public NotificationBuilder userId(Long userId) {
            this.userId = userId;
            return this;
        }

        public NotificationBuilder category(NotificationCategory category) {
            this.category = category;
            return this;
        }

        public NotificationBuilder type(String type) {
            this.type = type;
            return this;
        }

        public NotificationBuilder title(String title) {
            this.title = title;
            return this;
        }

        public NotificationBuilder message(String message) {
            this.message = message;
            return this;
        }

        public NotificationBuilder amount(BigDecimal amount) {
            this.amount = amount;
            return this;
        }

        public NotificationBuilder counterparty(String counterparty) {
            this.counterparty = counterparty;
            return this;
        }

        public NotificationBuilder eventStatus(String eventStatus) {
            this.eventStatus = eventStatus;
            return this;
        }

        public NotificationBuilder navigationTarget(String navigationTarget) {
            this.navigationTarget = navigationTarget;
            return this;
        }

        public NotificationBuilder eventTime(LocalDateTime eventTime) {
            this.eventTime = eventTime;
            return this;
        }

        public NotificationBuilder metadataJson(String metadataJson) {
            this.metadataJson = metadataJson;
            return this;
        }

        public NotificationBuilder isRead(boolean isRead) {
            this.isRead = isRead;
            return this;
        }

        public NotificationBuilder createdAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public Notification build() {
            return new Notification(id, userId, category, type, title, message, amount, counterparty, eventStatus, navigationTarget, eventTime, metadataJson, isRead, createdAt);
        }
    }
}
