package com.eduelevate.lms.service;

import com.eduelevate.lms.dto.student.*;
import com.eduelevate.lms.entity.Student;
import com.eduelevate.lms.repository.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class StudentServiceImpl implements StudentService {

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public List<StudentResponseDto> getAllStudents() {
        List<Student> students = studentRepository.findAll();
        return students.stream()
                .map(this::convertToResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public StudentResponseDto getStudentById(int studentId) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Student not found with id: " + studentId));
        return convertToResponseDto(student);
    }

    @Override
    public StudentResponseDto createStudent(CreateStudentDto createDto) {
        // Check if email already exists
        if (studentRepository.existsByEmail(createDto.getEmail())) {
            throw new RuntimeException("Student with email " + createDto.getEmail() + " already exists");
        }

        // Check if username already exists
        if (studentRepository.existsByUsername(createDto.getUsername())) {
            throw new RuntimeException("Student with username " + createDto.getUsername() + " already exists");
        }

        Student student = new Student();
        student.setFirstName(createDto.getFirstName());
        student.setLastName(createDto.getLastName());
        student.setEmail(createDto.getEmail());
        student.setUsername(createDto.getUsername());
        student.setPassword(passwordEncoder.encode(createDto.getPassword()));
        
        Student savedStudent = studentRepository.save(student);
        return convertToResponseDto(savedStudent);
    }

    @Override
    public StudentResponseDto updateStudent(int studentId, UpdateStudentDto updateDto) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Student not found with id: " + studentId));

        if (updateDto.getFirstName() != null) {
            student.setFirstName(updateDto.getFirstName());
        }
        if (updateDto.getLastName() != null) {
            student.setLastName(updateDto.getLastName());
        }
        if (updateDto.getEmail() != null) {
            // Check if new email already exists (but not for current student)
            if (studentRepository.existsByEmail(updateDto.getEmail()) && 
                !student.getEmail().equals(updateDto.getEmail())) {
                throw new RuntimeException("Student with email " + updateDto.getEmail() + " already exists");
            }
            student.setEmail(updateDto.getEmail());
        }
        if (updateDto.getUsername() != null) {
            // Check if new username already exists (but not for current student)
            if (studentRepository.existsByUsername(updateDto.getUsername()) && 
                !student.getUsername().equals(updateDto.getUsername())) {
                throw new RuntimeException("Student with username " + updateDto.getUsername() + " already exists");
            }
            student.setUsername(updateDto.getUsername());
        }
        if (updateDto.getPassword() != null) {
            student.setPassword(passwordEncoder.encode(updateDto.getPassword()));
        }

        Student savedStudent = studentRepository.save(student);
        return convertToResponseDto(savedStudent);
    }

    @Override
    public void deleteStudent(int studentId) {
        if (!studentRepository.existsById(studentId)) {
            throw new RuntimeException("Student not found with id: " + studentId);
        }
        studentRepository.deleteById(studentId);
    }

    private StudentResponseDto convertToResponseDto(Student student) {
        StudentResponseDto dto = new StudentResponseDto();
        dto.setStudentId(student.getStudentId());
        dto.setFirstName(student.getFirstName());
        dto.setLastName(student.getLastName());
        dto.setEmail(student.getEmail());
        dto.setUsername(student.getUsername());
        return dto;
    }
}