package com.eduelevate.lms.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class SecurityUtils {

    public static UserPrincipal getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof UserPrincipal) {
            return (UserPrincipal) authentication.getPrincipal();
        }
        throw new RuntimeException("No authenticated user found");
    }

    public static Integer getCurrentUserId() {
        return getCurrentUser().getUserId();
    }

    public static String getCurrentUsername() {
        return getCurrentUser().getUsername();
    }

    public static String getCurrentUserRole() {
        return getCurrentUser().getRole();
    }

    public static boolean isAdmin() {
        return "ADMIN".equals(getCurrentUserRole());
    }

    public static boolean isStudent() {
        return "STUDENT".equals(getCurrentUserRole());
    }

    public static boolean isInstructor() {
        return "INSTRUCTOR".equals(getCurrentUserRole());
    }

    public static boolean canAccessUserData(Integer requestedUserId, String requestedUserType) {
        UserPrincipal currentUser = getCurrentUser();
        String currentRole = currentUser.getRole();
        Integer currentUserId = currentUser.getUserId();

        // Admins can access everything
        if ("ADMIN".equals(currentRole)) {
            return true;
        }        // Users can only access their own data
        if (requestedUserType.equals(currentRole) && requestedUserId.equals(currentUserId)) {
            return true;
        }

        // Instructors can access student data
        if ("INSTRUCTOR".equals(currentRole) && "STUDENT".equals(requestedUserType)) {
            return true;
        }

        // REMOVED: Instructors can NO LONGER access other instructors' data
        // This was a privacy violation

        return false;
    }
}
