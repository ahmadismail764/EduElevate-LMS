package com.eduelevate.lms.service;

import com.eduelevate.lms.dto.auth.JwtResponse;
import com.eduelevate.lms.dto.auth.LoginRequest;

public interface AuthService {
    JwtResponse authenticateUser(LoginRequest loginRequest);
}
