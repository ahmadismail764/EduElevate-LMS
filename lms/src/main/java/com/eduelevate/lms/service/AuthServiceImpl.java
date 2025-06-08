package com.eduelevate.lms.service;

import com.eduelevate.lms.dto.auth.JwtResponse;
import com.eduelevate.lms.dto.auth.LoginRequest;
import com.eduelevate.lms.entity.Admin;
import com.eduelevate.lms.entity.Instructor;
import com.eduelevate.lms.entity.Student;
import com.eduelevate.lms.repository.AdminRepository;
import com.eduelevate.lms.repository.InstructorRepository;
import com.eduelevate.lms.repository.StudentRepository;
import com.eduelevate.lms.security.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthServiceImpl implements AuthService {

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private AdminRepository adminRepository;

    @Autowired
    private InstructorRepository instructorRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtils jwtUtils;

    @Override
    public JwtResponse authenticateUser(LoginRequest loginRequest) {
        String username = loginRequest.getUsername();
        String password = loginRequest.getPassword();
        String userType = loginRequest.getUserType().toLowerCase();

        switch (userType) {
            case "student":
                Optional<Student> student = studentRepository.findByUsername(username);
                if (student.isPresent() && passwordEncoder.matches(password, student.get().getPassword())) {
                    String token = jwtUtils.generateJwtToken(username, "STUDENT");
                    return new JwtResponse(token, username, "STUDENT", student.get().getStudentId());
                }
                break;

            case "admin":
                Optional<Admin> admin = adminRepository.findByUsername(username);
                if (admin.isPresent() && passwordEncoder.matches(password, admin.get().getPassword())) {
                    String token = jwtUtils.generateJwtToken(username, "ADMIN");
                    return new JwtResponse(token, username, "ADMIN", admin.get().getAdminId());
                }
                break;

            case "instructor":
                Optional<Instructor> instructor = instructorRepository.findByUsername(username);
                if (instructor.isPresent() && passwordEncoder.matches(password, instructor.get().getPassword())) {
                    String token = jwtUtils.generateJwtToken(username, "INSTRUCTOR");
                    return new JwtResponse(token, username, "INSTRUCTOR", instructor.get().getInstructorId());
                }
                break;

            default:
                throw new RuntimeException("Invalid user type: " + userType);
        }

        throw new RuntimeException("Invalid username or password");
    }
}
