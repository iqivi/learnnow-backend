package com.learnnow.auth.service;

import com.learnnow.auth.dto.AuthResponse;
import com.learnnow.auth.dto.LoginRequest;
import com.learnnow.auth.dto.SignUpRequest;
import com.learnnow.auth.security.UserPrincipal;
import com.learnnow.user.model.User;
import com.learnnow.user.model.UserRole;
import com.learnnow.user.repository.UserRepository;
import com.learnnow.auth.jwt.JwtTokenProvider;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider tokenProvider;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

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
     * @param loginRequest DTO with username/email and password.
     * @return AuthResponse containing the access token.
     * * @throws AuthenticationException (e.g., BadCredentialsException) //TODO add the custom exceptions
     * if credentials are invalid. Spring Security handles throwing this.
     */
    public AuthResponse authenticateUser(LoginRequest loginRequest) {

        try{
            //Attempt to authenticate the user against the database
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getEmail(),
                            loginRequest.getPassword()
                    )
            );

            //Set the Authentication object in the SecurityContext - good for persisting session, but not required right now
            SecurityContextHolder.getContext().setAuthentication(authentication);

            //Generate a JWT token using the authenticated object
            String jwt = tokenProvider.generateToken(authentication);
            return new AuthResponse(jwt);
        }catch (DisabledException ex) {
            throw new RuntimeException("Please confirm your email before logging in.");
        } catch (BadCredentialsException ex) {
            throw new RuntimeException("Invalid username or password.");
        }
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

        User user = new User(signUpRequest.getEmail(),
                signUpRequest.getPassword(),  //only held in plaintext during creation, will be overwritten in next step
                signUpRequest.getFirstName(),
                signUpRequest.getLastName(),
                LocalDateTime.now(),
                LocalDateTime.now())
                ;

        //Encode Password and set it on the user object
        user.setPassword(passwordEncoder.encode(signUpRequest.getPassword()));
        user.setRole(UserRole.USER);
        //Save the new user to the database - without @Transactional gets race conditioned and doesnt fire
        userRepository.save(user);

        String token = UUID.randomUUID().toString();
        user.setConfirmationToken(token);
        userRepository.save(user);

        String link = "http://localhost:8080/api/auth/confirm?token=" + token;
        System.out.println(link);
        //emailService.sendEmail(user.getEmail(), "Confirm your account", "Click here: " + link);

        return new AuthResponse(true, "User registered successfully!");
    }

    @Transactional
    public AuthResponse confirmUser(String token) {
        User user = userRepository.findByConfirmationToken(token)
                .orElseThrow(() -> new RuntimeException("Invalid Token"));

        user.setEnabled(true);
        user.setConfirmationToken(null);
        userRepository.save(user);
        return new AuthResponse(true, "Account verified successfully!");
    }

}