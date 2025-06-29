package com.eduelevate.lms.dto.auth;

import lombok.Data;

@Data
public class RegisterRequest {
    private String username;
    private String password;
    private String email;
    private String firstName;
    private String lastName;
    private String userType; // "student", "admin", or "instructor"
}
