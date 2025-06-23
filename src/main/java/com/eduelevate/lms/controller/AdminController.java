package com.eduelevate.lms.controller;
import com.eduelevate.lms.dto.admin.*;
import com.eduelevate.lms.dto.student.StudentResponseDto;
import com.eduelevate.lms.dto.instructor.InstructorResponseDto;
import com.eduelevate.lms.security.SecurityUtils;
import com.eduelevate.lms.service.AdminService;
import com.eduelevate.lms.service.StudentService;
import com.eduelevate.lms.service.InstructorService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/admins")
public class AdminController {
    
    private final AdminService adminService;
    private final StudentService studentService;
    private final InstructorService instructorService;
    
    public AdminController(AdminService adminService, StudentService studentService, InstructorService instructorService) {
        this.adminService = adminService;
        this.studentService = studentService;
        this.instructorService = instructorService;
    }

    @GetMapping  // Protected: Only Admins can see all admins
    public List<AdminResponseDto> getAllAdmins() {
        // Already protected by Spring Security @hasRole("ADMIN") - only admins reach here
        return adminService.getAllAdmins();
    }
    
    @GetMapping("/{id}") // Protected: Admins can only access their own data
    public AdminResponseDto getAdminById(@PathVariable int id) {
        // Check if admin can access this specific admin's data
        if (!SecurityUtils.canAccessUserData(id, "ADMIN")) {
            throw new RuntimeException("Access denied: You can only access your own data");
        }
        return adminService.getAdminById(id);
    }
    
    @PostMapping // Tested and working
    public AdminResponseDto createAdmin(@RequestBody CreateAdminDto createDto) {
        return adminService.createAdmin(createDto);
    }
      @PutMapping("/{id}") // Protected: Admins can only update their own data
    public AdminResponseDto updateAdmin(@PathVariable int id, @RequestBody UpdateAdminDto updateDto) {
        // Check if admin can modify this specific admin's data
        if (!SecurityUtils.canAccessUserData(id, "ADMIN")) {
            throw new RuntimeException("Access denied: You can only update your own data");
        }
        return adminService.updateAdmin(id, updateDto);
    }
    
    @DeleteMapping("/{id}") // Protected: Admins can only delete their own data
    public ResponseEntity<Void> deleteAdmin(@PathVariable int id) {
        // Check if admin can delete this specific admin's data
        if (!SecurityUtils.canAccessUserData(id, "ADMIN")) {
            throw new RuntimeException("Access denied: You can only delete your own data");
        }
        adminService.deleteAdmin(id);
        return ResponseEntity.noContent().build();
    }
    
    // Admin-specific endpoints for managing students and instructors
    @GetMapping("/students")
    public ResponseEntity<List<StudentResponseDto>> getAllStudentsAsAdmin() {
        List<StudentResponseDto> students = studentService.getAllStudents();
        return ResponseEntity.ok(students);
    }
    
    @GetMapping("/instructors")
    public ResponseEntity<List<InstructorResponseDto>> getAllInstructorsAsAdmin() {
        List<InstructorResponseDto> instructors = instructorService.getAllInstructors();
        return ResponseEntity.ok(instructors);
    }
}
