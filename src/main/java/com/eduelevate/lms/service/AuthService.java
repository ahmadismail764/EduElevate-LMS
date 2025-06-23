package com.eduelevate.lms.service;

import com.eduelevate.lms.dto.auth.JwtResponse;
import com.eduelevate.lms.dto.auth.LoginRequest;
import com.eduelevate.lms.dto.auth.RegisterRequest;

public interface AuthService {
    JwtResponse authenticateUser(LoginRequest loginRequest);
    JwtResponse registerUser(RegisterRequest registerRequest);
}
