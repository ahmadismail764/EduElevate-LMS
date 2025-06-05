package com.eduelevate.lms.controller;

import com.eduelevate.lms.dto.StudentResponseDto;
import com.eduelevate.lms.dto.CreateStudentDto;
import com.eduelevate.lms.dto.UpdateStudentDto;
import com.eduelevate.lms.service.StudentService;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/students")
public class StudentController {
    
    private final StudentService studentService;
    
    public StudentController(StudentService studentService) {
        this.studentService = studentService;
    }
    
    @GetMapping
    public List<StudentResponseDto> getAllStudents() {
        return studentService.getAllStudents();
    }
    
    @GetMapping("/{id}")
    public StudentResponseDto getStudentById(@PathVariable int id) {
        return studentService.getStudentById(id);
    }
    
    @PostMapping
    public StudentResponseDto createStudent(@RequestBody CreateStudentDto createDto) {
        return studentService.createStudent(createDto);
    }
    
    @PutMapping("/{id}")
    public StudentResponseDto updateStudent(@PathVariable int id, @RequestBody UpdateStudentDto updateDto) {
        return studentService.updateStudent(id, updateDto);
    }
    
    @DeleteMapping("/{id}")
    public void deleteStudent(@PathVariable int id) {
        studentService.deleteStudent(id);    }
    
    @GetMapping("/email/{email}")
    public StudentResponseDto getStudentByEmail(@PathVariable String email) {
        return studentService.getStudentByEmail(email);
    }
    
    @GetMapping("/exists/{email}")
    public boolean checkEmailExists(@PathVariable String email) {
        return studentService.existsByEmail(email);
    }
}
