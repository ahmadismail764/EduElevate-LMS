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
        
        // Check if email is being updated and if it already exists for another student
        if (updateDto.getEmail() != null && !updateDto.getEmail().equals(existingStudent.getEmail())) {
            if (studentRepository.existsByEmail(updateDto.getEmail())) {
                throw new RuntimeException("Student with email " + updateDto.getEmail() + " already exists");
            }
        }
        
        // Check if username is being updated and if it already exists for another student
        if (updateDto.getUsername() != null && !updateDto.getUsername().equals(existingStudent.getUsername())) {
            if (studentRepository.existsByUsername(updateDto.getUsername())) {
                throw new RuntimeException("Student with username " + updateDto.getUsername() + " already exists");
            }
        }
        
        // Update the fields that can be updated (only if not null)
        if (updateDto.getUsername() != null) {
            existingStudent.setUsername(updateDto.getUsername());
        }
        if (updateDto.getEmail() != null) {
            existingStudent.setEmail(updateDto.getEmail());
        }
        if (updateDto.getFirstName() != null) {
            existingStudent.setFirstName(updateDto.getFirstName());
        }
        if (updateDto.getLastName() != null) {
            existingStudent.setLastName(updateDto.getLastName());
        }
        existingStudent.setUpdatedAt(LocalDateTime.now());
        
        Student updatedStudent = studentRepository.save(existingStudent);
        return convertToResponseDto(updatedStudent);
    }
    
    @Override
    public void deleteStudent(int studentId) {
        if (!studentRepository.existsById(studentId)) {
            throw new RuntimeException("Student not found with id: " + studentId);
        }
        studentRepository.deleteById(studentId);    }
    
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