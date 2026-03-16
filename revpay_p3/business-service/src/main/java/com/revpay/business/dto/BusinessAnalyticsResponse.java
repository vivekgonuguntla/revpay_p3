package com.revpay.business.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public class BusinessAnalyticsResponse {
    private BigDecimal totalRevenue;
    private TransactionSummary transactionSummary;
    private OutstandingInvoices outstandingInvoices;
    private List<PaymentTrend> paymentTrends;
    private List<TopCustomer> topCustomers;

    public BusinessAnalyticsResponse() {
    }

    public BusinessAnalyticsResponse(BigDecimal totalRevenue, TransactionSummary transactionSummary,
                                     OutstandingInvoices outstandingInvoices, List<PaymentTrend> paymentTrends,
                                     List<TopCustomer> topCustomers) {
        this.totalRevenue = totalRevenue;
        this.transactionSummary = transactionSummary;
        this.outstandingInvoices = outstandingInvoices;
        this.paymentTrends = paymentTrends;
        this.topCustomers = topCustomers;
    }

    public static BusinessAnalyticsResponseBuilder builder() {
        return new BusinessAnalyticsResponseBuilder();
    }

    public BigDecimal getTotalRevenue() {
        return totalRevenue;
    }

    public void setTotalRevenue(BigDecimal totalRevenue) {
        this.totalRevenue = totalRevenue;
    }

    public TransactionSummary getTransactionSummary() {
        return transactionSummary;
    }

    public void setTransactionSummary(TransactionSummary transactionSummary) {
        this.transactionSummary = transactionSummary;
    }

    public OutstandingInvoices getOutstandingInvoices() {
        return outstandingInvoices;
    }

    public void setOutstandingInvoices(OutstandingInvoices outstandingInvoices) {
        this.outstandingInvoices = outstandingInvoices;
    }

    public List<PaymentTrend> getPaymentTrends() {
        return paymentTrends;
    }

    public void setPaymentTrends(List<PaymentTrend> paymentTrends) {
        this.paymentTrends = paymentTrends;
    }

    public List<TopCustomer> getTopCustomers() {
        return topCustomers;
    }

    public void setTopCustomers(List<TopCustomer> topCustomers) {
        this.topCustomers = topCustomers;
    }

    public static class BusinessAnalyticsResponseBuilder {
        private BigDecimal totalRevenue;
        private TransactionSummary transactionSummary;
        private OutstandingInvoices outstandingInvoices;
        private List<PaymentTrend> paymentTrends;
        private List<TopCustomer> topCustomers;

        public BusinessAnalyticsResponseBuilder totalRevenue(BigDecimal totalRevenue) {
            this.totalRevenue = totalRevenue;
            return this;
        }

        public BusinessAnalyticsResponseBuilder transactionSummary(TransactionSummary transactionSummary) {
            this.transactionSummary = transactionSummary;
            return this;
        }

        public BusinessAnalyticsResponseBuilder outstandingInvoices(OutstandingInvoices outstandingInvoices) {
            this.outstandingInvoices = outstandingInvoices;
            return this;
        }

        public BusinessAnalyticsResponseBuilder paymentTrends(List<PaymentTrend> paymentTrends) {
            this.paymentTrends = paymentTrends;
            return this;
        }

        public BusinessAnalyticsResponseBuilder topCustomers(List<TopCustomer> topCustomers) {
            this.topCustomers = topCustomers;
            return this;
        }

        public BusinessAnalyticsResponse build() {
            return new BusinessAnalyticsResponse(totalRevenue, transactionSummary, outstandingInvoices, paymentTrends, topCustomers);
        }
    }

    public static class TransactionSummary {
        private Long totalTransactions;
        private BigDecimal totalReceived;
        private BigDecimal totalSent;

        public TransactionSummary() {
        }

        public TransactionSummary(Long totalTransactions, BigDecimal totalReceived, BigDecimal totalSent) {
            this.totalTransactions = totalTransactions;
            this.totalReceived = totalReceived;
            this.totalSent = totalSent;
        }

        public static TransactionSummaryBuilder builder() {
            return new TransactionSummaryBuilder();
        }

        public Long getTotalTransactions() {
            return totalTransactions;
        }

        public void setTotalTransactions(Long totalTransactions) {
            this.totalTransactions = totalTransactions;
        }

        public BigDecimal getTotalReceived() {
            return totalReceived;
        }

        public void setTotalReceived(BigDecimal totalReceived) {
            this.totalReceived = totalReceived;
        }

        public BigDecimal getTotalSent() {
            return totalSent;
        }

        public void setTotalSent(BigDecimal totalSent) {
            this.totalSent = totalSent;
        }

        public static class TransactionSummaryBuilder {
            private Long totalTransactions;
            private BigDecimal totalReceived;
            private BigDecimal totalSent;

            public TransactionSummaryBuilder totalTransactions(Long totalTransactions) {
                this.totalTransactions = totalTransactions;
                return this;
            }

            public TransactionSummaryBuilder totalReceived(BigDecimal totalReceived) {
                this.totalReceived = totalReceived;
                return this;
            }

            public TransactionSummaryBuilder totalSent(BigDecimal totalSent) {
                this.totalSent = totalSent;
                return this;
            }

            public TransactionSummary build() {
                return new TransactionSummary(totalTransactions, totalReceived, totalSent);
            }
        }
    }

    public static class OutstandingInvoices {
        private Long count;
        private BigDecimal totalAmount;

        public OutstandingInvoices() {
        }

        public OutstandingInvoices(Long count, BigDecimal totalAmount) {
            this.count = count;
            this.totalAmount = totalAmount;
        }

        public static OutstandingInvoicesBuilder builder() {
            return new OutstandingInvoicesBuilder();
        }

        public Long getCount() {
            return count;
        }

        public void setCount(Long count) {
            this.count = count;
        }

        public BigDecimal getTotalAmount() {
            return totalAmount;
        }

        public void setTotalAmount(BigDecimal totalAmount) {
            this.totalAmount = totalAmount;
        }

        public static class OutstandingInvoicesBuilder {
            private Long count;
            private BigDecimal totalAmount;

            public OutstandingInvoicesBuilder count(Long count) {
                this.count = count;
                return this;
            }

            public OutstandingInvoicesBuilder totalAmount(BigDecimal totalAmount) {
                this.totalAmount = totalAmount;
                return this;
            }

            public OutstandingInvoices build() {
                return new OutstandingInvoices(count, totalAmount);
            }
        }
    }

    public static class PaymentTrend {
        private LocalDate date;
        private BigDecimal amount;

        public PaymentTrend() {
        }

        public PaymentTrend(LocalDate date, BigDecimal amount) {
            this.date = date;
            this.amount = amount;
        }

        public static PaymentTrendBuilder builder() {
            return new PaymentTrendBuilder();
        }

        public LocalDate getDate() {
            return date;
        }

        public void setDate(LocalDate date) {
            this.date = date;
        }

        public BigDecimal getAmount() {
            return amount;
        }

        public void setAmount(BigDecimal amount) {
            this.amount = amount;
        }

        public static class PaymentTrendBuilder {
            private LocalDate date;
            private BigDecimal amount;

            public PaymentTrendBuilder date(LocalDate date) {
                this.date = date;
                return this;
            }

            public PaymentTrendBuilder amount(BigDecimal amount) {
                this.amount = amount;
                return this;
            }

            public PaymentTrend build() {
                return new PaymentTrend(date, amount);
            }
        }
    }

    public static class TopCustomer {
        private String customer;
        private BigDecimal totalPaid;

        public TopCustomer() {
        }

        public TopCustomer(String customer, BigDecimal totalPaid) {
            this.customer = customer;
            this.totalPaid = totalPaid;
        }

        public static TopCustomerBuilder builder() {
            return new TopCustomerBuilder();
        }

        public String getCustomer() {
            return customer;
        }

        public void setCustomer(String customer) {
            this.customer = customer;
        }

        public BigDecimal getTotalPaid() {
            return totalPaid;
        }

        public void setTotalPaid(BigDecimal totalPaid) {
            this.totalPaid = totalPaid;
        }

        public static class TopCustomerBuilder {
            private String customer;
            private BigDecimal totalPaid;

            public TopCustomerBuilder customer(String customer) {
                this.customer = customer;
                return this;
            }

            public TopCustomerBuilder totalPaid(BigDecimal totalPaid) {
                this.totalPaid = totalPaid;
                return this;
            }

            public TopCustomer build() {
                return new TopCustomer(customer, totalPaid);
            }
        }
    }
}
