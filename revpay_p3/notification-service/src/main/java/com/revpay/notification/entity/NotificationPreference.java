package com.revpay.notification.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;

@Entity
@Table(name = "notification_preferences")
public class NotificationPreference {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private Long userId;

    @Column(nullable = false)
    private boolean transactionsEnabled = true;

    @Column(nullable = false)
    private boolean requestsEnabled = true;

    @Column(nullable = false)
    private boolean alertsEnabled = true;

    @Column(precision = 19, scale = 2)
    private BigDecimal lowBalanceThreshold;

    public NotificationPreference() {
    }

    public NotificationPreference(Long id, Long userId, boolean transactionsEnabled, boolean requestsEnabled, boolean alertsEnabled, BigDecimal lowBalanceThreshold) {
        this.id = id;
        this.userId = userId;
        this.transactionsEnabled = transactionsEnabled;
        this.requestsEnabled = requestsEnabled;
        this.alertsEnabled = alertsEnabled;
        this.lowBalanceThreshold = lowBalanceThreshold;
    }

    public static NotificationPreferenceBuilder builder() {
        return new NotificationPreferenceBuilder();
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

    public boolean isTransactionsEnabled() {
        return transactionsEnabled;
    }

    public void setTransactionsEnabled(boolean transactionsEnabled) {
        this.transactionsEnabled = transactionsEnabled;
    }

    public boolean isRequestsEnabled() {
        return requestsEnabled;
    }

    public void setRequestsEnabled(boolean requestsEnabled) {
        this.requestsEnabled = requestsEnabled;
    }

    public boolean isAlertsEnabled() {
        return alertsEnabled;
    }

    public void setAlertsEnabled(boolean alertsEnabled) {
        this.alertsEnabled = alertsEnabled;
    }

    public BigDecimal getLowBalanceThreshold() {
        return lowBalanceThreshold;
    }

    public void setLowBalanceThreshold(BigDecimal lowBalanceThreshold) {
        this.lowBalanceThreshold = lowBalanceThreshold;
    }

    public static class NotificationPreferenceBuilder {
        private Long id;
        private Long userId;
        private boolean transactionsEnabled = true;
        private boolean requestsEnabled = true;
        private boolean alertsEnabled = true;
        private BigDecimal lowBalanceThreshold;

        NotificationPreferenceBuilder() {
        }

        public NotificationPreferenceBuilder id(Long id) {
            this.id = id;
            return this;
        }

        public NotificationPreferenceBuilder userId(Long userId) {
            this.userId = userId;
            return this;
        }

        public NotificationPreferenceBuilder transactionsEnabled(boolean transactionsEnabled) {
            this.transactionsEnabled = transactionsEnabled;
            return this;
        }

        public NotificationPreferenceBuilder requestsEnabled(boolean requestsEnabled) {
            this.requestsEnabled = requestsEnabled;
            return this;
        }

        public NotificationPreferenceBuilder alertsEnabled(boolean alertsEnabled) {
            this.alertsEnabled = alertsEnabled;
            return this;
        }

        public NotificationPreferenceBuilder lowBalanceThreshold(BigDecimal lowBalanceThreshold) {
            this.lowBalanceThreshold = lowBalanceThreshold;
            return this;
        }

        public NotificationPreference build() {
            return new NotificationPreference(id, userId, transactionsEnabled, requestsEnabled, alertsEnabled, lowBalanceThreshold);
        }
    }
}
