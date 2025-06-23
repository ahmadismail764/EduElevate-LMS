package com.eduelevate.lms.service;

import com.eduelevate.lms.dto.instructor.CreateInstructorDto;
import com.eduelevate.lms.dto.instructor.InstructorResponseDto;
import com.eduelevate.lms.dto.instructor.UpdateInstructorDto;
import com.eduelevate.lms.entity.Instructor;
import com.eduelevate.lms.repository.InstructorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class InstructorServiceImpl implements InstructorService {
    
    private final InstructorRepository instructorRepository;
    private final PasswordEncoder passwordEncoder;
    
    @Override
    public InstructorResponseDto createInstructor(CreateInstructorDto createInstructorDto) {
        // Check if username already exists
        if (instructorRepository.existsByUsername(createInstructorDto.getUsername())) {
            throw new RuntimeException("Username already exists: " + createInstructorDto.getUsername());
        }
        
        // Check if email already exists
        if (instructorRepository.existsByEmail(createInstructorDto.getEmail())) {
            throw new RuntimeException("Email already exists: " + createInstructorDto.getEmail());
        }
        
        try {
            Instructor instructor = mapToEntity(createInstructorDto);
            Instructor savedInstructor = instructorRepository.save(instructor);
            return mapToResponseDto(savedInstructor);
        } catch (DataIntegrityViolationException e) {
            throw new RuntimeException("Failed to create instructor: " + e.getMessage());
        }
    }
    
    @Override
    public InstructorResponseDto getInstructorById(Integer instructorId) {
        Instructor instructor = instructorRepository.findById(instructorId)
                .orElseThrow(() -> new RuntimeException("Instructor not found with id: " + instructorId));
        return mapToResponseDto(instructor);
    }
    
    @Override
    public List<InstructorResponseDto> getAllInstructors() {
        List<Instructor> instructors = instructorRepository.findAll();
        return instructors.stream()
                .map(this::mapToResponseDto)
                .collect(Collectors.toList());
    }
    
    @Override
    public InstructorResponseDto updateInstructor(Integer instructorId, UpdateInstructorDto updateInstructorDto) {
        Instructor existingInstructor = instructorRepository.findById(instructorId)
                .orElseThrow(() -> new RuntimeException("Instructor not found with id: " + instructorId));
        
        // Check for duplicate username if it's being updated
        if (updateInstructorDto.getUsername() != null && 
            !updateInstructorDto.getUsername().equals(existingInstructor.getUsername())) {
            if (instructorRepository.existsByUsername(updateInstructorDto.getUsername())) {
                throw new RuntimeException("Username already exists: " + updateInstructorDto.getUsername());
            }
        }
        
        // Check for duplicate email if it's being updated
        if (updateInstructorDto.getEmail() != null && 
            !updateInstructorDto.getEmail().equals(existingInstructor.getEmail())) {
            if (instructorRepository.existsByEmail(updateInstructorDto.getEmail())) {
                throw new RuntimeException("Email already exists: " + updateInstructorDto.getEmail());
            }
        }
        
        updateInstructorFields(existingInstructor, updateInstructorDto);
        Instructor updatedInstructor = instructorRepository.save(existingInstructor);
        return mapToResponseDto(updatedInstructor);
    }
    
    @Override
    public void deleteInstructor(Integer instructorId) {
        if (!instructorRepository.existsById(instructorId)) {
            throw new RuntimeException("Instructor not found with id: " + instructorId);
        }
        instructorRepository.deleteById(instructorId);
    }
    
    @Override
    public InstructorResponseDto getInstructorByUsername(String username) {
        Optional<Instructor> instructorOpt = instructorRepository.findByUsername(username);
        if (instructorOpt.isPresent()) {
            return mapToResponseDto(instructorOpt.get());
        }
        throw new RuntimeException("Instructor not found with username: " + username);
    }
    
    @Override
    public InstructorResponseDto getInstructorByEmail(String email) {
        Optional<Instructor> instructorOpt = instructorRepository.findByEmail(email);
        if (instructorOpt.isPresent()) {
            return mapToResponseDto(instructorOpt.get());
        }
        throw new RuntimeException("Instructor not found with email: " + email);
    }
      private Instructor mapToEntity(CreateInstructorDto createInstructorDto) {
        Instructor instructor = new Instructor();
        instructor.setUsername(createInstructorDto.getUsername());
        instructor.setEmail(createInstructorDto.getEmail());
        instructor.setPassword(passwordEncoder.encode(createInstructorDto.getPassword()));
        instructor.setFirstName(createInstructorDto.getFirstName());
        instructor.setLastName(createInstructorDto.getLastName());
        instructor.setDepartment(createInstructorDto.getDepartment());
        instructor.setBio(createInstructorDto.getBio());
        instructor.setSpecialization(createInstructorDto.getSpecialization());
        return instructor;
    }
    
    private InstructorResponseDto mapToResponseDto(Instructor instructor) {
        InstructorResponseDto responseDto = new InstructorResponseDto();
        responseDto.setInstructorId(instructor.getInstructorId());
        responseDto.setUsername(instructor.getUsername());
        responseDto.setEmail(instructor.getEmail());
        responseDto.setFirstName(instructor.getFirstName());
        responseDto.setLastName(instructor.getLastName());
        responseDto.setDepartment(instructor.getDepartment());
        responseDto.setBio(instructor.getBio());
        responseDto.setSpecialization(instructor.getSpecialization());
        responseDto.setCreatedAt(instructor.getCreatedAt());
        responseDto.setUpdatedAt(instructor.getUpdatedAt());
        return responseDto;
    }
    
    private void updateInstructorFields(Instructor instructor, UpdateInstructorDto updateDto) {
        if (updateDto.getUsername() != null) {
            instructor.setUsername(updateDto.getUsername());
        }
        if (updateDto.getEmail() != null) {
            instructor.setEmail(updateDto.getEmail());
        }        if (updateDto.getPassword() != null) {
            instructor.setPassword(passwordEncoder.encode(updateDto.getPassword()));
        }
        if (updateDto.getFirstName() != null) {
            instructor.setFirstName(updateDto.getFirstName());
        }
        if (updateDto.getLastName() != null) {
            instructor.setLastName(updateDto.getLastName());
        }
        if (updateDto.getDepartment() != null) {
            instructor.setDepartment(updateDto.getDepartment());
        }
        if (updateDto.getBio() != null) {
            instructor.setBio(updateDto.getBio());
        }
        if (updateDto.getSpecialization() != null) {
            instructor.setSpecialization(updateDto.getSpecialization());
        }
    }
}
