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

    @Autowired
    private UserService userService;

    //Get all users (ADMIN ONLY)
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public List<User> listAll() {
        return userService.getAllUsers();
    }

    //Edit details of ANY user (ADMIN ONLY)
    @PutMapping("/{id}/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<User> adminUpdateUser(@PathVariable Long id, @RequestBody UserUpdateRequest request) {
        return ResponseEntity.ok(userService.updateUser(id, request));
    }

    //Edit details of the SELF (Authenticated User)
    @PutMapping("/me")
    public ResponseEntity<User> updateSelf(@AuthenticationPrincipal UserPrincipal currentUser,
                                           @RequestBody UserUpdateRequest request) {
        return ResponseEntity.ok(userService.updateUser(currentUser.getId(), request));
    }

    // Delete ANY user (ADMIN ONLY)
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<AuthResponse> adminDeleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.ok(new AuthResponse(true, "User deleted by admin"));
    }

    //Delete SELF account
    @DeleteMapping("/me")
    public ResponseEntity<AuthResponse> deleteSelf(@AuthenticationPrincipal UserPrincipal currentUser) {
        userService.deleteUser(currentUser.getId());
        return ResponseEntity.ok(new AuthResponse(true, "Account deleted"));
    }

    @PatchMapping("/{id}/role")
    @PreAuthorize("hasRole('ADMIN')")
    public User changeRole(@PathVariable Long id, @RequestBody RoleUpdateRequest request) {
        return userService.updateUserRole(id, request.getNewRole());
    }


}
