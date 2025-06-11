package com.eduelevate.lms.service;

import com.eduelevate.lms.dto.auth.JwtResponse;
import com.eduelevate.lms.dto.auth.LoginRequest;
import com.eduelevate.lms.dto.auth.RegisterRequest;
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
        String userType = loginRequest.getUserType().toLowerCase();        switch (userType) {
            case "student":
                Optional<Student> student = studentRepository.findByUsername(username);
                if (student.isPresent() && passwordEncoder.matches(password, student.get().getPassword())) {
                    String token = jwtUtils.generateJwtToken(username, "STUDENT", student.get().getStudentId());
                    return new JwtResponse(token, username, "STUDENT", student.get().getStudentId());
                }
                break;

            case "admin":
                Optional<Admin> admin = adminRepository.findByUsername(username);
                if (admin.isPresent() && passwordEncoder.matches(password, admin.get().getPassword())) {
                    String token = jwtUtils.generateJwtToken(username, "ADMIN", admin.get().getAdminId());
                    return new JwtResponse(token, username, "ADMIN", admin.get().getAdminId());
                }
                break;

            case "instructor":
                Optional<Instructor> instructor = instructorRepository.findByUsername(username);
                if (instructor.isPresent() && passwordEncoder.matches(password, instructor.get().getPassword())) {
                    String token = jwtUtils.generateJwtToken(username, "INSTRUCTOR", instructor.get().getInstructorId());
                    return new JwtResponse(token, username, "INSTRUCTOR", instructor.get().getInstructorId());
                }
                break;

            default:
                throw new RuntimeException("Invalid user type: " + userType);
        }

        throw new RuntimeException("Invalid username or password");
    }

    @Override
    public JwtResponse registerUser(RegisterRequest registerRequest) {
        String username = registerRequest.getUsername();
        String email = registerRequest.getEmail();
        String userType = registerRequest.getUserType().toLowerCase();
        String encodedPassword = passwordEncoder.encode(registerRequest.getPassword());

        // Check if username already exists in any user type
        if (studentRepository.findByUsername(username).isPresent() ||
            adminRepository.findByUsername(username).isPresent() ||
            instructorRepository.findByUsername(username).isPresent()) {
            throw new RuntimeException("Username is already taken!");
        }

        // Check if email already exists in any user type
        if (studentRepository.findByEmail(email).isPresent() ||
            adminRepository.findByEmail(email).isPresent() ||
            instructorRepository.findByEmail(email).isPresent()) {
            throw new RuntimeException("Email is already in use!");
        }

        switch (userType) {            case "student":
                Student student = new Student();
                student.setUsername(username);
                student.setPassword(encodedPassword);
                student.setEmail(email);
                student.setFirstName(registerRequest.getFirstName());
                student.setLastName(registerRequest.getLastName());
                student = studentRepository.save(student);
                
                String studentToken = jwtUtils.generateJwtToken(username, "STUDENT", student.getStudentId());
                return new JwtResponse(studentToken, username, "STUDENT", student.getStudentId());            case "admin":
                Admin admin = new Admin();
                admin.setUsername(username);
                admin.setPassword(encodedPassword);
                admin.setEmail(email);
                admin.setFirstName(registerRequest.getFirstName());
                admin.setLastName(registerRequest.getLastName());
                admin = adminRepository.save(admin);
                
                String adminToken = jwtUtils.generateJwtToken(username, "ADMIN", admin.getAdminId());
                return new JwtResponse(adminToken, username, "ADMIN", admin.getAdminId());            case "instructor":
                Instructor instructor = new Instructor();
                instructor.setUsername(username);
                instructor.setPassword(encodedPassword);
                instructor.setEmail(email);
                instructor.setFirstName(registerRequest.getFirstName());
                instructor.setLastName(registerRequest.getLastName());
                instructor = instructorRepository.save(instructor);
                
                String instructorToken = jwtUtils.generateJwtToken(username, "INSTRUCTOR", instructor.getInstructorId());
                return new JwtResponse(instructorToken, username, "INSTRUCTOR", instructor.getInstructorId());

            default:
                throw new RuntimeException("Invalid user type: " + userType + ". Must be 'student', 'admin', or 'instructor'");
        }
    }
}
