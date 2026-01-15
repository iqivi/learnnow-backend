package com.learnnow.user.dto;

import com.learnnow.user.model.UserRole;

public class RoleUpdateRequest {
    private UserRole newRole;

    public UserRole getNewRole() { return newRole; }
    public void setNewRole(UserRole newRole) { this.newRole = newRole; }
}
