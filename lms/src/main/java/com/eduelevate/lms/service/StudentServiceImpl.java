package com.eduelevate.lms.service;
import com.eduelevate.lms.dto.StudentResponseDto;
import com.eduelevate.lms.dto.CreateStudentDto;
import com.eduelevate.lms.dto.UpdateStudentDto;
import com.eduelevate.lms.entity.Student;
import com.eduelevate.lms.repository.StudentRepository;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class StudentServiceImpl implements StudentService {
    
    private final StudentRepository studentRepository;
    
    // Constructor injection (best practice)
    public StudentServiceImpl(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }
    
    @Override
    public List<StudentResponseDto> getAllStudents() {
        return studentRepository.findAll()
                .stream()
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
        
        // Convert DTO to Entity
        Student student = convertToEntity(createDto);
        student.setCreatedAt(LocalDateTime.now());
        student.setUpdatedAt(LocalDateTime.now());
        
        // Save and return as DTO
        Student savedStudent = studentRepository.save(student);
        return convertToResponseDto(savedStudent);
    }
    
    @Override
    public StudentResponseDto updateStudent(int studentId, UpdateStudentDto updateDto) {        Student existingStudent = studentRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Student not found with id: " + studentId));
        
        // Update only the fields that can be updated
        existingStudent.setFirstName(updateDto.getFirstName());
        existingStudent.setLastName(updateDto.getLastName());
        existingStudent.setUpdatedAt(LocalDateTime.now());
        
        Student updatedStudent = studentRepository.save(existingStudent);
        return convertToResponseDto(updatedStudent);
    }
    
    @Override
    public void deleteStudent(int studentId) {
        if (!studentRepository.existsById(studentId)) {
            throw new RuntimeException("Student not found with id: " + studentId);
        }
        studentRepository.deleteById(studentId);
    }
    
    @Override
    public StudentResponseDto getStudentByEmail(String email) {
        Student student = studentRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Student not found with email: " + email));
        return convertToResponseDto(student);
    }
    
    @Override
    public boolean existsByEmail(String email) {
        return studentRepository.existsByEmail(email);
    }
    
    // Helper Methods for DTO Mapping
    private StudentResponseDto convertToResponseDto(Student student) {
        return new StudentResponseDto(
                student.getStudentId(),
                student.getUsername(),
                student.getFirstName(),
                student.getLastName(),
                student.getEmail()        );
    }
    
    private Student convertToEntity(CreateStudentDto createDto) {
        return new Student(
                0, // studentId will be auto-generated
                createDto.getUsername(),
                createDto.getEmail(),
                createDto.getPassword(),
                createDto.getFirstName(),
                createDto.getLastName(),
                null, // createdAt will be set in service
                null  // updatedAt will be set in service
        );
    }
}