package com.learnnow.auth.dto;

public class LoginResponse {

    private String accessToken;
    private String message;
    private String tokenType = "Bearer";

    public LoginResponse(String accessToken, String message) {
        this.accessToken = accessToken;
        this.message = message;
    }

    // Getters and Setters
    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getTokenType() {
        return tokenType;
    }

    public void setTokenType(String tokenType) {
        this.tokenType = tokenType;
    }
}