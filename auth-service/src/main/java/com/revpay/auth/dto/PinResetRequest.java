package com.revpay.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;

import java.util.List;

public class PinResetRequest {

    @Email(message = "Valid email is required")
    @NotBlank(message = "Email is required")
    private String email;

    @NotBlank(message = "New PIN is required")
    @Pattern(regexp = "\\d{4}", message = "PIN must be exactly 4 digits")
    private String newPin;

    @NotEmpty(message = "Security answers are required")
    private List<SecurityQuestionDto> answers;

    // No-args constructor
    public PinResetRequest() {
    }

    // All-args constructor
    public PinResetRequest(String email, String newPin, List<SecurityQuestionDto> answers) {
        this.email = email;
        this.newPin = newPin;
        this.answers = answers;
    }

    // Getters and Setters
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getNewPin() {
        return newPin;
    }

    public void setNewPin(String newPin) {
        this.newPin = newPin;
    }

    public List<SecurityQuestionDto> getAnswers() {
        return answers;
    }

    public void setAnswers(List<SecurityQuestionDto> answers) {
        this.answers = answers;
    }
}
