package com.revpay.auth.dto;

import com.revpay.auth.entity.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

import java.util.List;

public class RegisterRequest {

    @NotBlank(message = "Full name is required")
    private String fullName;

    @NotBlank(message = "Username is required")
    private String username;

    @Email(message = "Valid email is required")
    @NotBlank(message = "Email is required")
    private String email;

    @NotBlank(message = "Password is required")
    private String password;

    private String phoneNumber;

    private Role role;

    private List<SecurityQuestionDto> securityQuestions;

    // No-args constructor
    public RegisterRequest() {
    }

    // All-args constructor
    public RegisterRequest(String fullName, String username, String email, String password,
                          String phoneNumber, Role role, List<SecurityQuestionDto> securityQuestions) {
        this.fullName = fullName;
        this.username = username;
        this.email = email;
        this.password = password;
        this.phoneNumber = phoneNumber;
        this.role = role;
        this.securityQuestions = securityQuestions;
    }

    // Getters and Setters
    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public List<SecurityQuestionDto> getSecurityQuestions() {
        return securityQuestions;
    }

    public void setSecurityQuestions(List<SecurityQuestionDto> securityQuestions) {
        this.securityQuestions = securityQuestions;
    }

    // Builder
    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String fullName;
        private String username;
        private String email;
        private String password;
        private String phoneNumber;
        private Role role;
        private List<SecurityQuestionDto> securityQuestions;

        public Builder fullName(String fullName) {
            this.fullName = fullName;
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

        public Builder password(String password) {
            this.password = password;
            return this;
        }

        public Builder phoneNumber(String phoneNumber) {
            this.phoneNumber = phoneNumber;
            return this;
        }

        public Builder role(Role role) {
            this.role = role;
            return this;
        }

        public Builder securityQuestions(List<SecurityQuestionDto> securityQuestions) {
            this.securityQuestions = securityQuestions;
            return this;
        }

        public RegisterRequest build() {
            return new RegisterRequest(fullName, username, email, password, phoneNumber, role, securityQuestions);
        }
    }
}
