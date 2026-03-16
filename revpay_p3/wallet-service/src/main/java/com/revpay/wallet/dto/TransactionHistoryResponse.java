package com.revpay.wallet.dto;

import java.util.List;

public class TransactionHistoryResponse {
    private List<TransactionResponse> transactions;
    private int totalCount;

    public TransactionHistoryResponse() {
    }

    public TransactionHistoryResponse(List<TransactionResponse> transactions, int totalCount) {
        this.transactions = transactions;
        this.totalCount = totalCount;
    }

    public static TransactionHistoryResponseBuilder builder() {
        return new TransactionHistoryResponseBuilder();
    }

    public List<TransactionResponse> getTransactions() {
        return transactions;
    }

    public void setTransactions(List<TransactionResponse> transactions) {
        this.transactions = transactions;
    }

    public int getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }

    public static class TransactionHistoryResponseBuilder {
        private List<TransactionResponse> transactions;
        private int totalCount;

        TransactionHistoryResponseBuilder() {
        }

        public TransactionHistoryResponseBuilder transactions(List<TransactionResponse> transactions) {
            this.transactions = transactions;
            return this;
        }

        public TransactionHistoryResponseBuilder totalCount(int totalCount) {
            this.totalCount = totalCount;
            return this;
        }

        public TransactionHistoryResponse build() {
            return new TransactionHistoryResponse(transactions, totalCount);
        }
    }
}
