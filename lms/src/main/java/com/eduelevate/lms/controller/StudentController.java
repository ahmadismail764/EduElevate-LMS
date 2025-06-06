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
    
    @GetMapping // Tested and working 
    public List<StudentResponseDto> getAllStudents() {
        return studentService.getAllStudents();
    }
    
    @GetMapping("/{id}") // Tested and working
    public StudentResponseDto getStudentById(@PathVariable int id) {
        return studentService.getStudentById(id);
    }
    
    @PostMapping // Tested and working
    public StudentResponseDto createStudent(@RequestBody CreateStudentDto createDto) {
        return studentService.createStudent(createDto);
    }
    @PutMapping("/{id}") // Tested and working
    public StudentResponseDto updateStudent(@PathVariable int id, @RequestBody UpdateStudentDto updateDto) {
        return studentService.updateStudent(id, updateDto);
    }
    @DeleteMapping("/{id}") // Tested and working
    public void deleteStudent(@PathVariable int id) {
        studentService.deleteStudent(id);    }
}
