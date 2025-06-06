package com.eduelevate.lms.service;

import com.eduelevate.lms.dto.instructor.CreateInstructorDto;
import com.eduelevate.lms.dto.instructor.InstructorResponseDto;
import com.eduelevate.lms.dto.instructor.UpdateInstructorDto;

import java.util.List;

public interface InstructorService {
    InstructorResponseDto createInstructor(CreateInstructorDto createInstructorDto);
    InstructorResponseDto getInstructorById(Integer instructorId);
    List<InstructorResponseDto> getAllInstructors();
    InstructorResponseDto updateInstructor(Integer instructorId, UpdateInstructorDto updateInstructorDto);
    void deleteInstructor(Integer instructorId);
    InstructorResponseDto getInstructorByUsername(String username);
    InstructorResponseDto getInstructorByEmail(String email);
}
