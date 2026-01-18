package com.learnnow.auth.service;

import com.learnnow.auth.dto.AuthResponse;
import com.learnnow.auth.dto.LoginRequest;
import com.learnnow.auth.dto.SignUpRequest;
import com.learnnow.auth.jwt.JwtTokenProvider;
import com.learnnow.email.service.EmailService;
import com.learnnow.user.model.User;
import com.learnnow.user.model.UserRole;
import com.learnnow.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;
import org.thymeleaf.context.Context;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtTokenProvider tokenProvider;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private EmailService emailService;

    @InjectMocks
    private AuthService authService;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(authService, "backLink", "http://localhost:8080/");
        ReflectionTestUtils.setField(authService, "frontLink", "http://localhost:3000/");
    }

    @Test
    void authenticateUser_Success() {
        // Arrange
        LoginRequest loginRequest = new LoginRequest("test@example.com", "password123");
        Authentication authentication = mock(Authentication.class);
        String expectedToken = "jwt-token-123";

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(tokenProvider.generateToken(authentication)).thenReturn(expectedToken);

        // Act
        AuthResponse response = authService.authenticateUser(loginRequest);

        // Assert
        assertNotNull(response);
        assertEquals(expectedToken, response.getAccessToken());
        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(tokenProvider).generateToken(authentication);
    }

    @Test
    void authenticateUser_DisabledAccount_ThrowsException() {
        // Arrange
        LoginRequest loginRequest = new LoginRequest("test@example.com", "password123");

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new DisabledException("Account disabled"));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            authService.authenticateUser(loginRequest);
        });

        assertEquals("Please confirm your email before logging in.", exception.getMessage());
    }

    @Test
    void authenticateUser_BadCredentials_ThrowsException() {
        // Arrange
        LoginRequest loginRequest = new LoginRequest("test@example.com", "wrongpassword");

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("Bad credentials"));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            authService.authenticateUser(loginRequest);
        });

        assertEquals("Invalid username or password.", exception.getMessage());
    }

    @Test
    void registerUser_Success() {
        // Arrange
        SignUpRequest signUpRequest = new SignUpRequest(
                "test@example.com",
                "password123",
                "John",
                "Doe"
        );

        when(userRepository.existsByEmail(signUpRequest.getEmail())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("encoded-password");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        AuthResponse response = authService.registerUser(signUpRequest);

        // Assert
        assertTrue(response.getSuccess());
        assertEquals("User registered successfully!", response.getMessage());
        verify(userRepository, times(2)).save(any(User.class)); // Once for user, once for token
        verify(emailService).sendHtmlEmail(eq("test@example.com"), anyString(), anyString(), any(Context.class));
    }

    @Test
    void registerUser_EmailAlreadyExists_ReturnsFalse() {
        // Arrange
        SignUpRequest signUpRequest = new SignUpRequest(
                "existing@example.com",
                "password123",
                "John",
                "Doe"
        );

        when(userRepository.existsByEmail(signUpRequest.getEmail())).thenReturn(true);

        // Act
        AuthResponse response = authService.registerUser(signUpRequest);

        // Assert
        assertFalse(response.getSuccess());
        assertEquals("Username is already taken!", response.getMessage());
        verify(userRepository, never()).save(any(User.class));
        verify(emailService, never()).sendHtmlEmail(anyString(), anyString(), anyString(), any(Context.class));
    }

    @Test
    void confirmUser_ValidToken_Success() {
        // Arrange
        String token = "valid-token-123";
        User user = new User();
        user.setEmail("test@example.com");
        user.setConfirmationToken(token);
        user.setEnabled(false);

        when(userRepository.findByConfirmationToken(token)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act - ZMIANA: Odbieramy Boolean zamiast AuthResponse
        Boolean result = authService.confirmUser(token);

        // Assert - ZMIANA: Sprawdzamy bezpośrednio wartość logiczną
        assertTrue(result, "Metoda powinna zwrócić true po udanej weryfikacji");
        assertTrue(user.isEnabled(), "Użytkownik powinien zostać aktywowany");
        assertNull(user.getConfirmationToken(), "Token weryfikacyjny powinien zostać wyczyszczony");
        verify(userRepository).save(user);
    }

    @Test
    void confirmUser_InvalidToken_ThrowsException() {
        // Arrange
        String token = "invalid-token";
        when(userRepository.findByConfirmationToken(token)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            authService.confirmUser(token);
        });

        assertEquals("Invalid Token", exception.getMessage());
    }

    @Test
    void requestPasswordReset_ValidEmail_Success() {
        // Arrange
        String email = "test@example.com";
        User user = new User();
        user.setEmail(email);
        user.setFirstName("John");

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        authService.requestPasswordReset(email);

        // Assert
        assertNotNull(user.getResetPasswordToken());
        assertNotNull(user.getTokenExpiry());
        verify(userRepository).save(user);
        verify(emailService).sendHtmlEmail(eq(email), anyString(), eq("password-reset"), any(Context.class));
    }

    @Test
    void requestPasswordReset_EmailNotFound_ThrowsException() {
        // Arrange
        String email = "nonexistent@example.com";
        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            authService.requestPasswordReset(email);
        });

        assertEquals("Email not found", exception.getMessage());
    }

    @Test
    void resetPassword_ValidToken_Success() {
        // Arrange
        String token = "valid-reset-token";
        String newPassword = "newPassword123";
        User user = new User();
        user.setResetPasswordToken(token);
        user.setTokenExpiry(LocalDateTime.now().plusHours(1));

        when(userRepository.findByResetPasswordToken(token)).thenReturn(Optional.of(user));
        when(passwordEncoder.encode(newPassword)).thenReturn("encoded-new-password");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        authService.resetPassword(token, newPassword);

        // Assert
        assertEquals("encoded-new-password", user.getPassword());
        assertNull(user.getResetPasswordToken());
        assertNull(user.getTokenExpiry());
        verify(userRepository).save(user);
    }

    @Test
    void resetPassword_ExpiredToken_ThrowsException() {
        // Arrange
        String token = "expired-token";
        User user = new User();
        user.setResetPasswordToken(token);
        user.setTokenExpiry(LocalDateTime.now().minusHours(1)); // Expired

        when(userRepository.findByResetPasswordToken(token)).thenReturn(Optional.of(user));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            authService.resetPassword(token, "newPassword");
        });

        assertEquals("Token expired", exception.getMessage());
    }

    @Test
    void resetPassword_InvalidToken_ThrowsException() {
        // Arrange
        String token = "invalid-token";
        when(userRepository.findByResetPasswordToken(token)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            authService.resetPassword(token, "newPassword");
        });

        assertEquals("Invalid token", exception.getMessage());
    }
}
