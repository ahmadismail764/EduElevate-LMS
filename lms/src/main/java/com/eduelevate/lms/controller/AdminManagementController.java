package com.eduelevate.lms.controller;

import com.eduelevate.lms.dto.student.StudentResponseDto;
import com.eduelevate.lms.dto.instructor.InstructorResponseDto;
import com.eduelevate.lms.service.StudentService;
import com.eduelevate.lms.service.InstructorService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminManagementController {
    
    private final StudentService studentService;
    private final InstructorService instructorService;
    
    public AdminManagementController(StudentService studentService, InstructorService instructorService) {
        this.studentService = studentService;
        this.instructorService = instructorService;
    }

    @GetMapping("/students")
    public ResponseEntity<List<StudentResponseDto>> getAllStudents() {
        List<StudentResponseDto> students = studentService.getAllStudents();
        return ResponseEntity.ok(students);
    }
    
    @GetMapping("/instructors")
    public ResponseEntity<List<InstructorResponseDto>> getAllInstructors() {
        List<InstructorResponseDto> instructors = instructorService.getAllInstructors();
        return ResponseEntity.ok(instructors);
    }
}
