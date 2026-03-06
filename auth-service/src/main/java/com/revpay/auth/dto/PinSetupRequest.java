package com.revpay.auth.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public class PinSetupRequest {

    @NotBlank(message = "PIN is required")
    @Pattern(regexp = "\\d{4}", message = "PIN must be exactly 4 digits")
    private String pin;

    // No-args constructor
    public PinSetupRequest() {
    }

    // All-args constructor
    public PinSetupRequest(String pin) {
        this.pin = pin;
    }

    // Getters and Setters
    public String getPin() {
        return pin;
    }

    public void setPin(String pin) {
        this.pin = pin;
    }
}
