package com.revpay.auth.dto;

import jakarta.validation.constraints.NotBlank;

public class SecurityQuestionDto {

    @NotBlank(message = "Question is required")
    private String question;

    @NotBlank(message = "Answer is required")
    private String answer;

    // No-args constructor
    public SecurityQuestionDto() {
    }

    // All-args constructor
    public SecurityQuestionDto(String question, String answer) {
        this.question = question;
        this.answer = answer;
    }

    // Getters and Setters
    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }
}
