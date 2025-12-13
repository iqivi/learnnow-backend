package com.learnnow.auth.dto;

public class LoginRequest {

    private String email;
    private String password;

    // Default constructor
    public LoginRequest() {
    }

    // Constructor with fields
    public LoginRequest(String email, String password) {
        this.email = email;
        this.password = password;
    }

    // Getters and Setters
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

    // Optional: toString() for logging/debugging
    @Override
    public String toString() {
        return "LoginRequest{" +
                "usernameOrEmail='" + email + '\'' +
                ", password='[PROTECTED]'" + // IMPORTANT: Never log the actual password
                '}';
    }
}