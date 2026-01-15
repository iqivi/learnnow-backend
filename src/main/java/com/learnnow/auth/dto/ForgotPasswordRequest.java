package com.learnnow.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

// DTO for initiating the reset
public class ForgotPasswordRequest {
    @NotBlank
    @Email
    private String email;

    // Getter and Setter
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
}
