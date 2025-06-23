package com.eduelevate.lms.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;

@Component
public class AuthTokenFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtils jwtUtils;    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        try {
            System.out.println("AuthTokenFilter: Processing request to " + request.getRequestURI());
            String jwt = parseJwt(request);
            System.out.println("AuthTokenFilter: JWT extracted: " + (jwt != null ? jwt.substring(0, Math.min(jwt.length(), 20)) + "..." : "null"));
            
            if (jwt != null && jwtUtils.validateJwtToken(jwt)) {
                System.out.println("AuthTokenFilter: JWT validation successful");
                String username = jwtUtils.getUsernameFromJwtToken(jwt);
                String role = jwtUtils.getRoleFromJwtToken(jwt);
                Integer userId = jwtUtils.getUserIdFromJwtToken(jwt);
                
                System.out.println("AuthTokenFilter: Username: " + username + ", Role: " + role + ", UserId: " + userId);

                // Create a custom principal that includes both username and userId
                UserPrincipal userPrincipal = new UserPrincipal(username, userId, role);

                UsernamePasswordAuthenticationToken authentication = 
                    new UsernamePasswordAuthenticationToken(
                        userPrincipal, 
                        null, 
                        Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + role))
                    );
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                SecurityContextHolder.getContext().setAuthentication(authentication);
                System.out.println("AuthTokenFilter: Authentication set successfully for user: " + username);
            } else {
                System.out.println("AuthTokenFilter: JWT validation failed or JWT is null");
            }
        } catch (Exception e) {
            System.err.println("AuthTokenFilter: Cannot set user authentication: " + e.getMessage());
            logger.error("Cannot set user authentication: {}", e);
        }

        filterChain.doFilter(request, response);
    }

    private String parseJwt(HttpServletRequest request) {
        String headerAuth = request.getHeader("Authorization");

        if (StringUtils.hasText(headerAuth) && headerAuth.startsWith("Bearer ")) {
            return headerAuth.substring(7);
        }

        return null;
    }
}
