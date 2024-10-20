package com.example.collabcode.dto;

public class RoleAssignmentRequest {
    private String userId;  // The ID of the user to assign a role
    private String role;     // The role to assign (e.g., Admin, Editor, Viewer)

    // Getters and Setters
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}

