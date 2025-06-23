package com.eduelevate.lms.dto.auth;

public class JwtResponse {
    private String token;
    private String type = "Bearer";
    private String username;
    private String role;
    private int userId;

    // Default constructor
    public JwtResponse() {}

    // Constructor with parameters
    public JwtResponse(String accessToken, String username, String role, int userId) {
        this.token = accessToken;
        this.username = username;
        this.role = role;
        this.userId = userId;
    }

    // Getters and Setters
    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    // Alias for backward compatibility with tests
    public String getUserType() {
        return role;
    }

    public void setUserType(String userType) {
        this.role = userType;
    }
}
