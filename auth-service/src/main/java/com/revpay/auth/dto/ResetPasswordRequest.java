package com.revpay.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public class ResetPasswordRequest {

    @Email(message = "Valid email is required")
    @NotBlank(message = "Email is required")
    private String email;

    @NotBlank(message = "New password is required")
    private String newPassword;

    @NotEmpty(message = "Security answers are required")
    private List<SecurityQuestionDto> answers;

    // No-args constructor
    public ResetPasswordRequest() {
    }

    // All-args constructor
    public ResetPasswordRequest(String email, String newPassword, List<SecurityQuestionDto> answers) {
        this.email = email;
        this.newPassword = newPassword;
        this.answers = answers;
    }

    // Getters and Setters
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }

    public List<SecurityQuestionDto> getAnswers() {
        return answers;
    }

    public void setAnswers(List<SecurityQuestionDto> answers) {
        this.answers = answers;
    }
}
