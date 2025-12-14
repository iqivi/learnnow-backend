package com.learnnow.auth.service;

import com.learnnow.auth.dto.AuthResponse;
import com.learnnow.auth.dto.LoginRequest;
import com.learnnow.auth.dto.SignUpRequest;
import com.learnnow.user.model.User; // Assuming you have a User entity
import com.learnnow.user.repository.UserRepository; // Assuming you have a User Repository
import com.learnnow.auth.security.JwtTokenProvider;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider tokenProvider;
    private final UserRepository userRepository; // Needed for registration and login
    private final PasswordEncoder passwordEncoder; // Needed for hashing passwords

    @Autowired
    public AuthService(AuthenticationManager authenticationManager,
                       JwtTokenProvider tokenProvider,
                       UserRepository userRepository,
                       PasswordEncoder passwordEncoder) {
        this.authenticationManager = authenticationManager;
        this.tokenProvider = tokenProvider;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Authenticates the user and generates a JWT.
     * * @param loginRequest DTO with username/email and password.
     * @return AuthResponse containing the access token.
     * * @throws AuthenticationException (e.g., BadCredentialsException)
     * if credentials are invalid. Spring Security handles throwing this.
     */
    public AuthResponse authenticateUser(LoginRequest loginRequest) {

        // 1. Attempt to authenticate the user
        // The AuthenticationManager uses the configured UserDetailsService and
        // PasswordEncoder to validate the credentials against the database.
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getEmail(),
                        loginRequest.getPassword()
                )
        );

        // 2. Set the Authentication object in the SecurityContext
        // This is good practice, especially if the current thread needs the user details.
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // 3. Generate a JWT token using the authenticated object
        String jwt = tokenProvider.generateToken(authentication);

        // 4. Return the response DTO
        return new AuthResponse(jwt);
    }

    /**
     * Handles the creation of a new user account.
     *
     * @param signUpRequest DTO containing new user details.
     * @return AuthResponse indicating success or failure.
     */
    @Transactional
    public AuthResponse registerUser(SignUpRequest signUpRequest) {

        // 1. Validation Checks
        if (userRepository.existsByEmail((signUpRequest.getEmail()))) {
            return new AuthResponse(false, "Username is already taken!");
        }

        // 2. Create User object (assuming User is a JPA Entity)
        User user = new User(signUpRequest.getEmail(),
                signUpRequest.getPassword(),
                signUpRequest.getFirstName(),
                signUpRequest.getLastName(),
                LocalDateTime.now(),
                LocalDateTime.now())
                ;

        // 3. Encode Password and set it on the user object
        user.setPassword(passwordEncoder.encode(signUpRequest.getPassword()));

        // Note: You would typically set default roles here (e.g., ROLE_USER)
        System.out.println(user.getFirstName() + " " + user.getLastName());
        // 4. Save the new user to the database
        userRepository.save(user);

        return new AuthResponse(true, "User registered successfully!");
    }
}