package com.learnnow.user.controller;

import com.learnnow.auth.dto.AuthResponse;
import com.learnnow.auth.security.UserPrincipal;
import com.learnnow.user.dto.RoleUpdateRequest;
import com.learnnow.user.dto.UserUpdateRequest;
import com.learnnow.user.model.User;
import com.learnnow.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/user")
public class UserController {
    //get all users
    //put edit (/user/{id}) - admit
    //put edit (/user/{id}) - user
    //delete user (id) - admin
    //delete user (id) - user

    @Autowired
    private UserService userService;

    // 1. Get all users (ADMIN ONLY)
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public List<User> listAll() {
        return userService.getAllUsers();
    }

    // 2. Edit details of ANY user (ADMIN ONLY)
    @PutMapping("/{id}/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<User> adminUpdateUser(@PathVariable Long id, @RequestBody UserUpdateRequest request) {
        return ResponseEntity.ok(userService.updateUser(id, request));
    }

    // 3. Edit details of the SELF (Authenticated User)
    @PutMapping("/me")
    public ResponseEntity<User> updateSelf(@AuthenticationPrincipal UserPrincipal currentUser,
                                           @RequestBody UserUpdateRequest request) {
        return ResponseEntity.ok(userService.updateUser(currentUser.getId(), request));
    }

    // 4. Delete ANY user (ADMIN ONLY)
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<AuthResponse> adminDeleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.ok(new AuthResponse(true, "User deleted by admin"));
    }

    // 5. Delete SELF account
    @DeleteMapping("/me")
    public ResponseEntity<AuthResponse> deleteSelf(@AuthenticationPrincipal UserPrincipal currentUser) {
        userService.deleteUser(currentUser.getId());
        return ResponseEntity.ok(new AuthResponse(true, "Account deleted"));
    }

    @PatchMapping("/{id}/role")
    @PreAuthorize("hasRole('ADMIN')")
    public User changeRole(@PathVariable Long id, @RequestBody RoleUpdateRequest request) {
        // If 'id' is not found, Service throws RuntimeException, handled by Global Handler
        return userService.updateUserRole(id, request.getNewRole());
    }

    @GetMapping("/debug/me")
    public ResponseEntity<AuthResponse> showUser(@AuthenticationPrincipal UserPrincipal currentUser) {
        String name = currentUser.getEmail();
        return ResponseEntity.ok(new AuthResponse(true, name));
    }
    @GetMapping("/debug/role")
    public ResponseEntity<?> getMyRole(@AuthenticationPrincipal UserPrincipal currentUser) {
        if (currentUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("No user is logged in");
        }
        return ResponseEntity.ok(Map.of(
                "username", currentUser.getEmail(),
                "id", currentUser.getId(),
                "authorities", currentUser.getAuthorities(), // Returns the list of roles with ROLE_ prefix
                "rawRole", currentUser.getRole() // Assuming you kept the enum field in UserPrincipal
        ));
    }

}
