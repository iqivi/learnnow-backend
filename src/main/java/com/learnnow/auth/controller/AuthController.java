package com.learnnow.auth.controller;

import com.learnnow.auth.dto.LoginRequest;
import com.learnnow.auth.dto.AuthResponse;
import com.learnnow.auth.dto.SignUpRequest;
import com.learnnow.auth.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    @GetMapping("/test")
    public ResponseEntity<AuthResponse> test() {
        AuthResponse response = new AuthResponse("test", "connection ok");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> authenticateUser(@RequestBody LoginRequest loginRequest) {

        try {
            // 1. Call the service layer to perform authentication
            AuthResponse response = authService.authenticateUser(loginRequest);
            response.setMessage("Login successful");
            response.setSuccess(true);

            // 2. Return a 200 OK with the token in the body
            return new ResponseEntity<>(response, HttpStatus.OK);

        } catch (RuntimeException e) {
            // 3. Handle authentication failure (e.g., InvalidCredentialsException)
            // Note: In a real app, you would use a global exception handler for a clean response.
            // For now, we return a 401 Unauthorized status.
            return new ResponseEntity<>(
                    new AuthResponse(false, "Authentication failed: " + e.getMessage()),
                    HttpStatus.UNAUTHORIZED
            );
        }
    }

    /**
     * Handles POST requests to /api/auth/register.
     * Expects a JSON payload in the body matching the SignUpRequest DTO.
     */
    @PostMapping("/register")
    public ResponseEntity<AuthResponse> registerUser(@RequestBody SignUpRequest signUpRequest) {

        AuthResponse response = authService.registerUser(signUpRequest);

        // If the service returns a failed response, return a 400 Bad Request
        if (!response.getSuccess()) {
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }

        // If registration is successful, return a 201 Created status
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
}