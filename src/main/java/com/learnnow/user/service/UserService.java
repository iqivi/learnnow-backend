package com.learnnow.user.service;

import com.learnnow.user.dto.UserUpdateRequest;
import com.learnnow.user.exception.UserNotFoundException;
import com.learnnow.user.model.User;
import com.learnnow.user.model.UserRole;
import com.learnnow.user.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User getUser(Long id) {
        Optional<User> user = userRepository.findById(id);
        if (user.isEmpty())
            throw new UserNotFoundException();
        return user.get();
    }

    public User addUser(User user) {
        return userRepository.save(user);
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User findByUsername(String username) {
        return userRepository.findByEmail(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    @Transactional
    public User updateUser(Long id, UserUpdateRequest request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setEmail(request.getEmail());
        // Do NOT update password here; create a separate 'change-password' flow
        return userRepository.save(user);
    }

    @Transactional
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    @Transactional
    public User updateUserRole(Long userId, UserRole newRole) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (user.getRole() == UserRole.ADMIN) {
            if (newRole != UserRole.ADMIN) {
                long adminCount = userRepository.countByRole(UserRole.ADMIN);

                if (adminCount <= 1) {
                    throw new RuntimeException("Cannot demote the last administrator in the system.");
                }
            }
        }

        user.setRole(newRole);
        return userRepository.save(user);
    }

    @Transactional
    public User processOAuthPostLogin(String email, String name) {
        String [] nameArray = name.split(" ");
        String firstName = nameArray[0];
        String lastName = nameArray[1];
        return userRepository.findByEmail(email)
                .map(existingUser -> {
                    // Optional: Update name if it changed on Google
                    existingUser.setFirstName(firstName);
                    existingUser.setLastName(lastName);
                    return userRepository.save(existingUser);
                })
                .orElseGet(() -> {
                    // Create a new user if they don't exist
                    User newUser = new User();
                    newUser.setEmail(email);
                    newUser.setFirstName(firstName);
                    newUser.setLastName(lastName);
                    newUser.setRole(UserRole.USER);
                    newUser.setEnabled(true);
                    newUser.setPassword(UUID.randomUUID().toString());
                    return userRepository.save(newUser);
                });
    }
}
