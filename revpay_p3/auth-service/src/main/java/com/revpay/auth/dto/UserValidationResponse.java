package com.revpay.auth.dto;

public class UserValidationResponse {

    private boolean valid;
    private Long userId;
    private String username;
    private String email;
    private String role;

    // No-args constructor
    public UserValidationResponse() {
    }

    // All-args constructor
    public UserValidationResponse(boolean valid, Long userId, String username, String email, String role) {
        this.valid = valid;
        this.userId = userId;
        this.username = username;
        this.email = email;
        this.role = role;
    }

    // Getters and Setters
    public boolean isValid() {
        return valid;
    }

    public void setValid(boolean valid) {
        this.valid = valid;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    // Builder
    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private boolean valid;
        private Long userId;
        private String username;
        private String email;
        private String role;

        public Builder valid(boolean valid) {
            this.valid = valid;
            return this;
        }

        public Builder userId(Long userId) {
            this.userId = userId;
            return this;
        }

        public Builder username(String username) {
            this.username = username;
            return this;
        }

        public Builder email(String email) {
            this.email = email;
            return this;
        }

        public Builder role(String role) {
            this.role = role;
            return this;
        }

        public UserValidationResponse build() {
            return new UserValidationResponse(valid, userId, username, email, role);
        }
    }
}
