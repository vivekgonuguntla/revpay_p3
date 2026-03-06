package com.revpay.notification.dto;

import java.math.BigDecimal;

public class NotificationPreferenceResponse {
    private Long id;
    private Long userId;
    private boolean transactionsEnabled;
    private boolean requestsEnabled;
    private boolean alertsEnabled;
    private BigDecimal lowBalanceThreshold;

    public NotificationPreferenceResponse() {
    }

    public NotificationPreferenceResponse(Long id, Long userId, boolean transactionsEnabled, boolean requestsEnabled, boolean alertsEnabled, BigDecimal lowBalanceThreshold) {
        this.id = id;
        this.userId = userId;
        this.transactionsEnabled = transactionsEnabled;
        this.requestsEnabled = requestsEnabled;
        this.alertsEnabled = alertsEnabled;
        this.lowBalanceThreshold = lowBalanceThreshold;
    }

    public static NotificationPreferenceResponseBuilder builder() {
        return new NotificationPreferenceResponseBuilder();
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

    public static class NotificationPreferenceResponseBuilder {
        private Long id;
        private Long userId;
        private boolean transactionsEnabled;
        private boolean requestsEnabled;
        private boolean alertsEnabled;
        private BigDecimal lowBalanceThreshold;

        NotificationPreferenceResponseBuilder() {
        }

        public NotificationPreferenceResponseBuilder id(Long id) {
            this.id = id;
            return this;
        }

        public NotificationPreferenceResponseBuilder userId(Long userId) {
            this.userId = userId;
            return this;
        }

        public NotificationPreferenceResponseBuilder transactionsEnabled(boolean transactionsEnabled) {
            this.transactionsEnabled = transactionsEnabled;
            return this;
        }

        public NotificationPreferenceResponseBuilder requestsEnabled(boolean requestsEnabled) {
            this.requestsEnabled = requestsEnabled;
            return this;
        }

        public NotificationPreferenceResponseBuilder alertsEnabled(boolean alertsEnabled) {
            this.alertsEnabled = alertsEnabled;
            return this;
        }

        public NotificationPreferenceResponseBuilder lowBalanceThreshold(BigDecimal lowBalanceThreshold) {
            this.lowBalanceThreshold = lowBalanceThreshold;
            return this;
        }

        public NotificationPreferenceResponse build() {
            return new NotificationPreferenceResponse(id, userId, transactionsEnabled, requestsEnabled, alertsEnabled, lowBalanceThreshold);
        }
    }
}
