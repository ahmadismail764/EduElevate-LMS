package com.eduelevate.lms.service;
import com.eduelevate.lms.dto.student.StudentResponseDto;
import com.eduelevate.lms.dto.student.CreateStudentDto;
import com.eduelevate.lms.dto.student.UpdateStudentDto;
import java.util.List;

public interface StudentService {
    // CRUD Operations
    List<StudentResponseDto> getAllStudents();
    StudentResponseDto getStudentById(int studentId);
    StudentResponseDto createStudent(CreateStudentDto createDto);
    StudentResponseDto updateStudent(int studentId, UpdateStudentDto updateDto);
    void deleteStudent(int studentId);
}