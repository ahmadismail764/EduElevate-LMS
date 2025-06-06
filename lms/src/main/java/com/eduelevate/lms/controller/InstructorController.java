package com.eduelevate.lms.controller;

import com.eduelevate.lms.dto.instructor.CreateInstructorDto;
import com.eduelevate.lms.dto.instructor.InstructorResponseDto;
import com.eduelevate.lms.dto.instructor.UpdateInstructorDto;
import com.eduelevate.lms.service.InstructorService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/instructors")
@RequiredArgsConstructor
public class InstructorController {
    
    private final InstructorService instructorService;
    
    @PostMapping // Tested and working
    public ResponseEntity<InstructorResponseDto> createInstructor(@Valid @RequestBody CreateInstructorDto createInstructorDto) {
        try {
            InstructorResponseDto createdInstructor = instructorService.createInstructor(createInstructorDto);
            return new ResponseEntity<>(createdInstructor, HttpStatus.CREATED);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
    }
    
    @GetMapping("/{instructorId}") // Tested and working
    public ResponseEntity<InstructorResponseDto> getInstructorById(@PathVariable Integer instructorId) {
        try {
            InstructorResponseDto instructor = instructorService.getInstructorById(instructorId);
            return new ResponseEntity<>(instructor, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }
    
    @GetMapping // Tested and working
    public ResponseEntity<List<InstructorResponseDto>> getAllInstructors() {
        List<InstructorResponseDto> instructors = instructorService.getAllInstructors();
        return new ResponseEntity<>(instructors, HttpStatus.OK);
    }
    
    @PutMapping("/{instructorId}") // Tested and working
    public ResponseEntity<InstructorResponseDto> updateInstructor(
            @PathVariable Integer instructorId,
            @Valid @RequestBody UpdateInstructorDto updateInstructorDto) {
        try {
            InstructorResponseDto updatedInstructor = instructorService.updateInstructor(instructorId, updateInstructorDto);
            return new ResponseEntity<>(updatedInstructor, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
    }
    
    @DeleteMapping("/{instructorId}") // Tested and working
    public ResponseEntity<Void> deleteInstructor(@PathVariable Integer instructorId) {
        try {
            instructorService.deleteInstructor(instructorId);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}
