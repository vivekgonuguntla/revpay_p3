package com.revpay.notification.dto;

import java.math.BigDecimal;

public class UpdateNotificationPreferenceRequest {
    private Boolean transactionsEnabled;
    private Boolean requestsEnabled;
    private Boolean alertsEnabled;
    private BigDecimal lowBalanceThreshold;

    public UpdateNotificationPreferenceRequest() {
    }

    public UpdateNotificationPreferenceRequest(Boolean transactionsEnabled, Boolean requestsEnabled, Boolean alertsEnabled, BigDecimal lowBalanceThreshold) {
        this.transactionsEnabled = transactionsEnabled;
        this.requestsEnabled = requestsEnabled;
        this.alertsEnabled = alertsEnabled;
        this.lowBalanceThreshold = lowBalanceThreshold;
    }

    public static UpdateNotificationPreferenceRequestBuilder builder() {
        return new UpdateNotificationPreferenceRequestBuilder();
    }

    public Boolean getTransactionsEnabled() {
        return transactionsEnabled;
    }

    public void setTransactionsEnabled(Boolean transactionsEnabled) {
        this.transactionsEnabled = transactionsEnabled;
    }

    public Boolean getRequestsEnabled() {
        return requestsEnabled;
    }

    public void setRequestsEnabled(Boolean requestsEnabled) {
        this.requestsEnabled = requestsEnabled;
    }

    public Boolean getAlertsEnabled() {
        return alertsEnabled;
    }

    public void setAlertsEnabled(Boolean alertsEnabled) {
        this.alertsEnabled = alertsEnabled;
    }

    public BigDecimal getLowBalanceThreshold() {
        return lowBalanceThreshold;
    }

    public void setLowBalanceThreshold(BigDecimal lowBalanceThreshold) {
        this.lowBalanceThreshold = lowBalanceThreshold;
    }

    public static class UpdateNotificationPreferenceRequestBuilder {
        private Boolean transactionsEnabled;
        private Boolean requestsEnabled;
        private Boolean alertsEnabled;
        private BigDecimal lowBalanceThreshold;

        UpdateNotificationPreferenceRequestBuilder() {
        }

        public UpdateNotificationPreferenceRequestBuilder transactionsEnabled(Boolean transactionsEnabled) {
            this.transactionsEnabled = transactionsEnabled;
            return this;
        }

        public UpdateNotificationPreferenceRequestBuilder requestsEnabled(Boolean requestsEnabled) {
            this.requestsEnabled = requestsEnabled;
            return this;
        }

        public UpdateNotificationPreferenceRequestBuilder alertsEnabled(Boolean alertsEnabled) {
            this.alertsEnabled = alertsEnabled;
            return this;
        }

        public UpdateNotificationPreferenceRequestBuilder lowBalanceThreshold(BigDecimal lowBalanceThreshold) {
            this.lowBalanceThreshold = lowBalanceThreshold;
            return this;
        }

        public UpdateNotificationPreferenceRequest build() {
            return new UpdateNotificationPreferenceRequest(transactionsEnabled, requestsEnabled, alertsEnabled, lowBalanceThreshold);
        }
    }
}
