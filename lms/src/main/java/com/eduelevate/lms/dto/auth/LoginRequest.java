package com.eduelevate.lms.dto.auth;

public class LoginRequest {
    private String username;
    private String password;
    private String userType; // "student", "admin", or "instructor"

    // Default constructor
    public LoginRequest() {}

    // Constructor with parameters
    public LoginRequest(String username, String password, String userType) {
        this.username = username;
        this.password = password;
        this.userType = userType;
    }

    // Getters and Setters
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }
}
