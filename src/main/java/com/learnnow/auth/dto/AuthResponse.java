package com.learnnow.auth.dto;

public class AuthResponse { //reusable dto for returning requests to the client

    private String accessToken;
    private String message;
    private String tokenType = "Bearer";
    private Boolean success;

    public AuthResponse(String accessToken) {
        this.accessToken = accessToken;
    }
    public AuthResponse(String accessToken, String message) {
        this.accessToken = accessToken;
        this.message = message;
    }
    public AuthResponse(Boolean success,  String message) {
        this.success = success;
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

    public Boolean getSuccess() {
        return success;
    }
    public void setSuccess(Boolean success) {
        this.success = success;
    }
}