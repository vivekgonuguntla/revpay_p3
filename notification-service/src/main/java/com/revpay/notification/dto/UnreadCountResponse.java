package com.revpay.notification.dto;

public class UnreadCountResponse {
    private Long count;

    public UnreadCountResponse() {
    }

    public UnreadCountResponse(Long count) {
        this.count = count;
    }

    public static UnreadCountResponseBuilder builder() {
        return new UnreadCountResponseBuilder();
    }

    public Long getCount() {
        return count;
    }

    public void setCount(Long count) {
        this.count = count;
    }

    public static class UnreadCountResponseBuilder {
        private Long count;

        UnreadCountResponseBuilder() {
        }

        public UnreadCountResponseBuilder count(Long count) {
            this.count = count;
            return this;
        }

        public UnreadCountResponse build() {
            return new UnreadCountResponse(count);
        }
    }
}
