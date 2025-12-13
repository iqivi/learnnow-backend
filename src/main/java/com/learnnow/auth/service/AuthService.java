package com.learnnow.auth.service;

import com.learnnow.user.exception.UserNotFoundException;
import com.learnnow.user.model.User;
import com.learnnow.user.repository.UserRepository;
import com.learnnow.user.service.UserService;

import java.util.Optional;


import com.learnnow.auth.dto.LoginRequest;
import com.learnnow.auth.dto.LoginResponse;
import org.springframework.stereotype.Service;
//TODO proper auth with spring security

@Service
public class AuthService {
    private final UserRepository userRepository;
    public AuthService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
    /**
     * Attempts to authenticate a user and returns a token upon success.
     *
     * @param loginRequest The DTO containing username/email and password.
     * @return A LoginResponse DTO containing a JWT/access token.
     * @throws RuntimeException (or a custom exception like InvalidCredentialsException) if authentication fails.
     */
    public LoginResponse authenticateUser(LoginRequest loginRequest) {
        // --- Core Authentication Logic Goes Here ---

        // 1. Fetch user from database by Email
        User user = userRepository.findByEmail(loginRequest.getEmail())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        // 2. Verify password (using a PasswordEncoder)
        if (!passwordEncoder.matches(loginRequest.getPassword(), user.getHashedPassword())) {
            throw new BadCredentialsException("Invalid password");
        }

        // 3. If authentication is successful, generate a JWT token
        String token = "token"; //TODO: jwtTokenProvider.generateToken(user);

        return new LoginResponse(token, "Sign in successful");
    }
}
