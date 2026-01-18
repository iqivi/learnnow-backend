package com.learnnow.user.service;

import com.learnnow.user.dto.UserUpdateRequest;
import com.learnnow.user.exception.UserNotFoundException;
import com.learnnow.user.model.User;
import com.learnnow.user.model.UserRole;
import com.learnnow.user.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @Test
    void getUser_ExistingId_ReturnsUser() {
        // Arrange
        Long userId = 1L;
        User expectedUser = new User();
        expectedUser.setId(userId);
        expectedUser.setEmail("test@example.com");
        expectedUser.setFirstName("John");
        expectedUser.setLastName("Doe");

        when(userRepository.findById(userId)).thenReturn(Optional.of(expectedUser));

        // Act
        User result = userService.getUser(userId);

        // Assert
        assertNotNull(result);
        assertEquals(userId, result.getId());
        assertEquals("test@example.com", result.getEmail());
        verify(userRepository).findById(userId);
    }

    @Test
    void getUser_NonExistingId_ThrowsException() {
        // Arrange
        Long userId = 999L;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(UserNotFoundException.class, () -> {
            userService.getUser(userId);
        });
        verify(userRepository).findById(userId);
    }

    @Test
    void addUser_Success() {
        // Arrange
        User newUser = new User();
        newUser.setEmail("new@example.com");
        newUser.setFirstName("Jane");
        newUser.setLastName("Smith");
        newUser.setPassword("encoded-password");
        newUser.setRole(UserRole.USER);

        when(userRepository.save(any(User.class))).thenReturn(newUser);

        // Act
        User result = userService.addUser(newUser);

        // Assert
        assertNotNull(result);
        assertEquals("new@example.com", result.getEmail());
        assertEquals("Jane", result.getFirstName());
        verify(userRepository).save(newUser);
    }

    @Test
    void getAllUsers_ReturnsListOfUsers() {
        // Arrange
        User user1 = new User();
        user1.setId(1L);
        user1.setEmail("user1@example.com");

        User user2 = new User();
        user2.setId(2L);
        user2.setEmail("user2@example.com");

        List<User> expectedUsers = Arrays.asList(user1, user2);
        when(userRepository.findAll()).thenReturn(expectedUsers);

        // Act
        List<User> result = userService.getAllUsers();

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("user1@example.com", result.get(0).getEmail());
        assertEquals("user2@example.com", result.get(1).getEmail());
        verify(userRepository).findAll();
    }

    @Test
    void findByUsername_ExistingEmail_ReturnsUser() {
        // Arrange
        String email = "test@example.com";
        User expectedUser = new User();
        expectedUser.setEmail(email);
        expectedUser.setFirstName("John");

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(expectedUser));

        // Act
        User result = userService.findByUsername(email);

        // Assert
        assertNotNull(result);
        assertEquals(email, result.getEmail());
        verify(userRepository).findByEmail(email);
    }

    @Test
    void findByUsername_NonExistingEmail_ThrowsException() {
        // Arrange
        String email = "nonexistent@example.com";
        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(UserNotFoundException.class, () -> {
            userService.findByUsername(email);
        });

        verify(userRepository).findByEmail(email);
    }

    @Test
    void updateUser_Success() {
        // Arrange
        Long userId = 1L;
        User existingUser = new User();
        existingUser.setId(userId);
        existingUser.setEmail("old@example.com");
        existingUser.setFirstName("OldFirst");
        existingUser.setLastName("OldLast");

        UserUpdateRequest updateRequest = new UserUpdateRequest();
        updateRequest.setEmail("new@example.com");
        updateRequest.setFirstName("NewFirst");
        updateRequest.setLastName("NewLast");

        when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        User result = userService.updateUser(userId, updateRequest);

        // Assert
        assertNotNull(result);
        assertEquals("new@example.com", result.getEmail());
        assertEquals("NewFirst", result.getFirstName());
        assertEquals("NewLast", result.getLastName());
        verify(userRepository).findById(userId);
        verify(userRepository).save(existingUser);
    }

    @Test
    void updateUser_NonExistingUser_ThrowsException() {
        // Arrange
        Long userId = 999L;
        UserUpdateRequest updateRequest = new UserUpdateRequest();
        updateRequest.setEmail("new@example.com");

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(UserNotFoundException.class, () -> {
            userService.updateUser(userId, updateRequest);
        });
        verify(userRepository).findById(userId);
        verify(userRepository, never()).save(any(User.class));
    }
}
