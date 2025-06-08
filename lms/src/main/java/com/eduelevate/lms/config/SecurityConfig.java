package com.eduelevate.lms.config;

import com.eduelevate.lms.security.AuthTokenFilter;
import com.eduelevate.lms.security.AuthEntryPoint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {    @Autowired
    private AuthTokenFilter authTokenFilter;
    
    @Autowired
    private AuthEntryPoint authEntryPoint;
    
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
            .csrf(csrf -> csrf.disable())
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .exceptionHandling(ex -> ex.authenticationEntryPoint(authEntryPoint))
            .authorizeHttpRequests(authz -> authz
                .requestMatchers("/api/auth/**").permitAll() // Allow authentication endpoints
                
                // Allow user registration (POST only) - no authentication needed
                .requestMatchers(HttpMethod.POST, "/api/students").permitAll() // Allow student registration
                .requestMatchers(HttpMethod.POST, "/api/admins").permitAll() // Allow admin registration  
                .requestMatchers(HttpMethod.POST, "/api/instructors").permitAll() // Allow instructor registration
                
                // Protect other student endpoints (GET, PUT, DELETE)
                .requestMatchers("/api/students/**").hasAnyRole("STUDENT", "ADMIN") // Students and Admins can access student endpoints
                
                // Protect admin endpoints (only Admins, except POST which is public above)
                .requestMatchers("/api/admins/**").hasRole("ADMIN") // Only Admins can access admin endpoints
                
                // Protect instructor endpoints (Instructors and Admins, except POST which is public above)
                .requestMatchers("/api/instructors/**").hasAnyRole("INSTRUCTOR", "ADMIN") // Instructors and Admins can access instructor endpoints
                
                .anyRequest().authenticated() // All other requests need authentication
            )
            .addFilterBefore(authTokenFilter, UsernamePasswordAuthenticationFilter.class)
            .build();
    }
}
