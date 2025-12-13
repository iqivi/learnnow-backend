package com.learnnow.auth.controller;

import com.learnnow.auth.dto.LoginRequest;
import com.learnnow.auth.dto.LoginResponse;
import com.learnnow.auth.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth") // Base URL for authentication endpoints
public class AuthController {

    private final AuthService authService;

    // Dependency Injection via constructor
    @Autowired
    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    /**
     * Handles POST requests to /api/auth/login.
     * Expects a JSON payload in the body matching the LoginRequest DTO.
     * request:
     * {
     *     "usernameOrEmail": "testuser@example.com",
     *     "password": "password123"
     * }
     * response:
     * {
     *     "accessToken": "eyJhbGciOiJIUzI1NiI...",
     *     "message": "Successfully logged in!",
     *     "tokenType": "Bearer"
     * }
     */
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> authenticateUser(@RequestBody LoginRequest loginRequest) {

        try {
            // 1. Call the service layer to perform authentication
            LoginResponse response = authService.authenticateUser(loginRequest);

            // 2. Return a 200 OK with the token in the body
            return new ResponseEntity<>(response, HttpStatus.OK);

        } catch (RuntimeException e) {
            // 3. Handle authentication failure (e.g., InvalidCredentialsException)
            // Note: In a real app, you would use a global exception handler for a clean response.
            // For now, we return a 401 Unauthorized status.
            return new ResponseEntity<>(
                    new LoginResponse(null, "Authentication failed: " + e.getMessage()),
                    HttpStatus.UNAUTHORIZED
            );
        }
    }
}