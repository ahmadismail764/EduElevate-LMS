package com.eduelevate.lms.controller;

import com.eduelevate.lms.dto.student.StudentResponseDto;
import com.eduelevate.lms.dto.student.CreateStudentDto;
import com.eduelevate.lms.dto.student.UpdateStudentDto;
import com.eduelevate.lms.security.SecurityUtils;
import com.eduelevate.lms.service.StudentService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/students")
public class StudentController {
    
    private final StudentService studentService;
    
    public StudentController(StudentService studentService) {
        this.studentService = studentService;
    }
      @GetMapping // Protected: Only Admins and Instructors can see all students
    public List<StudentResponseDto> getAllStudents() {
        // Students cannot access this endpoint - only Admins and Instructors
        if (SecurityUtils.isStudent()) {
            throw new RuntimeException("Access denied: Students cannot view all student data");
        }
        return studentService.getAllStudents();
    }
    
    @GetMapping("/{id}") // Protected: Users can only access their own data (except Admins)
    public StudentResponseDto getStudentById(@PathVariable int id) {
        // Check if user can access this specific student's data
        if (!SecurityUtils.canAccessUserData(id, "STUDENT")) {
            throw new RuntimeException("Access denied: You can only access your own data");
        }
        return studentService.getStudentById(id);
    }
      @PostMapping // Protected: Only Admins can create students
    public ResponseEntity<StudentResponseDto> createStudent(@RequestBody CreateStudentDto createDto) {
        // Only Admins can create students - students and instructors cannot
        if (!SecurityUtils.isAdmin()) {
            throw new RuntimeException("Access denied: Only administrators can create students");
        }
        StudentResponseDto createdStudent = studentService.createStudent(createDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdStudent);
    }    @PutMapping("/{id}") // Protected: Users can only update their own data (except Admins)
    public ResponseEntity<StudentResponseDto> updateStudent(@PathVariable int id, @RequestBody UpdateStudentDto updateDto) {
        // Check if user can modify this specific student's data
        if (!SecurityUtils.canAccessUserData(id, "STUDENT")) {
            throw new RuntimeException("Access denied: You can only update your own data");
        }
        StudentResponseDto updatedStudent = studentService.updateStudent(id, updateDto);
        return ResponseEntity.ok(updatedStudent);
    }
    
    @DeleteMapping("/{id}") // Protected: Users can only delete their own data (except Admins)
    public ResponseEntity<Void> deleteStudent(@PathVariable int id) {
        // Check if user can delete this specific student's data
        if (!SecurityUtils.canAccessUserData(id, "STUDENT")) {
            throw new RuntimeException("Access denied: You can only delete your own data");
        }
        studentService.deleteStudent(id);
        return ResponseEntity.noContent().build();
    }
}
