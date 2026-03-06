package com.revpay.business.dto;

import java.math.BigDecimal;

public class BusinessAnalyticsResponse {
    private Long totalInvoices;
    private Long paidInvoices;
    private Long pendingInvoices;
    private Long overdueInvoices;
    private BigDecimal totalRevenue;
    private BigDecimal pendingRevenue;
    private BigDecimal overdueRevenue;
    private Long totalLoans;
    private Long activeLoans;
    private BigDecimal totalLoanAmount;
    private BigDecimal totalOutstanding;

    public BusinessAnalyticsResponse() {
    }

    public BusinessAnalyticsResponse(Long totalInvoices, Long paidInvoices, Long pendingInvoices, Long overdueInvoices, BigDecimal totalRevenue, BigDecimal pendingRevenue, BigDecimal overdueRevenue, Long totalLoans, Long activeLoans, BigDecimal totalLoanAmount, BigDecimal totalOutstanding) {
        this.totalInvoices = totalInvoices;
        this.paidInvoices = paidInvoices;
        this.pendingInvoices = pendingInvoices;
        this.overdueInvoices = overdueInvoices;
        this.totalRevenue = totalRevenue;
        this.pendingRevenue = pendingRevenue;
        this.overdueRevenue = overdueRevenue;
        this.totalLoans = totalLoans;
        this.activeLoans = activeLoans;
        this.totalLoanAmount = totalLoanAmount;
        this.totalOutstanding = totalOutstanding;
    }

    public static BusinessAnalyticsResponseBuilder builder() {
        return new BusinessAnalyticsResponseBuilder();
    }

    public Long getTotalInvoices() {
        return totalInvoices;
    }

    public void setTotalInvoices(Long totalInvoices) {
        this.totalInvoices = totalInvoices;
    }

    public Long getPaidInvoices() {
        return paidInvoices;
    }

    public void setPaidInvoices(Long paidInvoices) {
        this.paidInvoices = paidInvoices;
    }

    public Long getPendingInvoices() {
        return pendingInvoices;
    }

    public void setPendingInvoices(Long pendingInvoices) {
        this.pendingInvoices = pendingInvoices;
    }

    public Long getOverdueInvoices() {
        return overdueInvoices;
    }

    public void setOverdueInvoices(Long overdueInvoices) {
        this.overdueInvoices = overdueInvoices;
    }

    public BigDecimal getTotalRevenue() {
        return totalRevenue;
    }

    public void setTotalRevenue(BigDecimal totalRevenue) {
        this.totalRevenue = totalRevenue;
    }

    public BigDecimal getPendingRevenue() {
        return pendingRevenue;
    }

    public void setPendingRevenue(BigDecimal pendingRevenue) {
        this.pendingRevenue = pendingRevenue;
    }

    public BigDecimal getOverdueRevenue() {
        return overdueRevenue;
    }

    public void setOverdueRevenue(BigDecimal overdueRevenue) {
        this.overdueRevenue = overdueRevenue;
    }

    public Long getTotalLoans() {
        return totalLoans;
    }

    public void setTotalLoans(Long totalLoans) {
        this.totalLoans = totalLoans;
    }

    public Long getActiveLoans() {
        return activeLoans;
    }

    public void setActiveLoans(Long activeLoans) {
        this.activeLoans = activeLoans;
    }

    public BigDecimal getTotalLoanAmount() {
        return totalLoanAmount;
    }

    public void setTotalLoanAmount(BigDecimal totalLoanAmount) {
        this.totalLoanAmount = totalLoanAmount;
    }

    public BigDecimal getTotalOutstanding() {
        return totalOutstanding;
    }

    public void setTotalOutstanding(BigDecimal totalOutstanding) {
        this.totalOutstanding = totalOutstanding;
    }

    public static class BusinessAnalyticsResponseBuilder {
        private Long totalInvoices;
        private Long paidInvoices;
        private Long pendingInvoices;
        private Long overdueInvoices;
        private BigDecimal totalRevenue;
        private BigDecimal pendingRevenue;
        private BigDecimal overdueRevenue;
        private Long totalLoans;
        private Long activeLoans;
        private BigDecimal totalLoanAmount;
        private BigDecimal totalOutstanding;

        public BusinessAnalyticsResponseBuilder totalInvoices(Long totalInvoices) {
            this.totalInvoices = totalInvoices;
            return this;
        }

        public BusinessAnalyticsResponseBuilder paidInvoices(Long paidInvoices) {
            this.paidInvoices = paidInvoices;
            return this;
        }

        public BusinessAnalyticsResponseBuilder pendingInvoices(Long pendingInvoices) {
            this.pendingInvoices = pendingInvoices;
            return this;
        }

        public BusinessAnalyticsResponseBuilder overdueInvoices(Long overdueInvoices) {
            this.overdueInvoices = overdueInvoices;
            return this;
        }

        public BusinessAnalyticsResponseBuilder totalRevenue(BigDecimal totalRevenue) {
            this.totalRevenue = totalRevenue;
            return this;
        }

        public BusinessAnalyticsResponseBuilder pendingRevenue(BigDecimal pendingRevenue) {
            this.pendingRevenue = pendingRevenue;
            return this;
        }

        public BusinessAnalyticsResponseBuilder overdueRevenue(BigDecimal overdueRevenue) {
            this.overdueRevenue = overdueRevenue;
            return this;
        }

        public BusinessAnalyticsResponseBuilder totalLoans(Long totalLoans) {
            this.totalLoans = totalLoans;
            return this;
        }

        public BusinessAnalyticsResponseBuilder activeLoans(Long activeLoans) {
            this.activeLoans = activeLoans;
            return this;
        }

        public BusinessAnalyticsResponseBuilder totalLoanAmount(BigDecimal totalLoanAmount) {
            this.totalLoanAmount = totalLoanAmount;
            return this;
        }

        public BusinessAnalyticsResponseBuilder totalOutstanding(BigDecimal totalOutstanding) {
            this.totalOutstanding = totalOutstanding;
            return this;
        }

        public BusinessAnalyticsResponse build() {
            return new BusinessAnalyticsResponse(totalInvoices, paidInvoices, pendingInvoices, overdueInvoices, totalRevenue, pendingRevenue, overdueRevenue, totalLoans, activeLoans, totalLoanAmount, totalOutstanding);
        }
    }
}
