package com.eduelevate.lms.security;

import java.security.Principal;

public class UserPrincipal implements Principal {
    private final String username;
    private final Integer userId;
    private final String role;

    public UserPrincipal(String username, Integer userId, String role) {
        this.username = username;
        this.userId = userId;
        this.role = role;
    }

    @Override
    public String getName() {
        return username;
    }

    public String getUsername() {
        return username;
    }

    public Integer getUserId() {
        return userId;
    }

    public String getRole() {
        return role;
    }
}
