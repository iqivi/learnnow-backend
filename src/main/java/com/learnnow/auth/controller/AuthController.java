package com.learnnow.auth.controller;

import com.learnnow.auth.dto.LoginRequest;
import com.learnnow.auth.dto.AuthResponse;
import com.learnnow.auth.dto.SignUpRequest;
import com.learnnow.auth.security.UserPrincipal;
import com.learnnow.auth.service.AuthService;
import com.learnnow.user.model.User;
import com.learnnow.user.repository.UserRepository;
import com.learnnow.user.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.SpringVersion;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;
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
    public ResponseEntity<AuthResponse> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {

        AuthResponse response = authService.authenticateUser(loginRequest);

        // 2. Setting extra fields
        response.setMessage("Login successful");
        response.setSuccess(true);

        // 3. Just return the object. Spring defaults to a 200 OK status.
        return ResponseEntity.ok(response);
    }

    /**
     * Handles POST requests to /api/auth/register.
     * Expects a JSON payload in the body matching the SignUpRequest DTO.
     */
    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody SignUpRequest signUpRequest) {
        return new ResponseEntity<>(authService.registerUser(signUpRequest), HttpStatus.CREATED);

    }


    @GetMapping("/confirm")
    public ResponseEntity<AuthResponse> confirmUser(@RequestParam String token) {
        return new ResponseEntity<>(authService.confirmUser(token), HttpStatus.OK);
    }
}