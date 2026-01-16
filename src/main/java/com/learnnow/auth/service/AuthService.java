package com.learnnow.auth.service;

import com.learnnow.auth.dto.AuthResponse;
import com.learnnow.auth.dto.LoginRequest;
import com.learnnow.auth.dto.SignUpRequest;
import com.learnnow.email.service.EmailService;
import com.learnnow.user.model.User;
import com.learnnow.user.model.UserRole;
import com.learnnow.user.repository.UserRepository;
import com.learnnow.auth.jwt.JwtTokenProvider;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider tokenProvider;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    @Value("${app.backend-link}")
    private String backLink;
    @Value("${app.frontend-link}")
    private String frontLink;

    @Autowired
    public AuthService(AuthenticationManager authenticationManager,
                       JwtTokenProvider tokenProvider,
                       UserRepository userRepository,
                       PasswordEncoder passwordEncoder, EmailService emailService) {
        this.authenticationManager = authenticationManager;
        this.tokenProvider = tokenProvider;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
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
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getEmail(),
                            loginRequest.getPassword()
                    )
            );
            SecurityContextHolder.getContext().setAuthentication(authentication);
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

        user.setPassword(passwordEncoder.encode(signUpRequest.getPassword()));
        user.setRole(UserRole.USER);
        userRepository.save(user);

        String token = UUID.randomUUID().toString();
        user.setConfirmationToken(token);
        userRepository.save(user);

        sendConfirmationEmail(user);

        return new AuthResponse(true, "User registered successfully!");
    }

    public void sendConfirmationEmail(User user) {
        org.thymeleaf.context.Context context = new org.thymeleaf.context.Context();
        context.setVariable("name", user.getFirstName());
        context.setVariable("confirmationLink", backLink + "api/auth/confirm?token=" + user.getConfirmationToken());

        emailService.sendHtmlEmail(user.getEmail(), "Welcome! Confirm your account", "email-confirmation", context);
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

    public void requestPasswordReset(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Email not found"));

        String token = UUID.randomUUID().toString();
        user.setResetPasswordToken(token);
        user.setTokenExpiry(LocalDateTime.now().plusHours(1)); // Expire in 1 hour
        userRepository.save(user);

        org.thymeleaf.context.Context context = new org.thymeleaf.context.Context();
        context.setVariable("name", user.getFirstName());
        context.setVariable("resetLink", frontLink + "api/auth/reset-password?token=" + token);

        emailService.sendHtmlEmail(user.getEmail(), "Password Reset Request", "password-reset", context);
    }

    @Transactional
    public void resetPassword(String token, String newPassword) {
        User user = userRepository.findByResetPasswordToken(token)
                .orElseThrow(() -> new RuntimeException("Invalid token"));

        if (user.getTokenExpiry().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Token expired");
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        user.setResetPasswordToken(null);
        user.setTokenExpiry(null);
        userRepository.save(user);
    }
}