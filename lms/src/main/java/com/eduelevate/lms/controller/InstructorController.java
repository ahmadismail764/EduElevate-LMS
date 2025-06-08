package com.eduelevate.lms.controller;

import com.eduelevate.lms.dto.instructor.CreateInstructorDto;
import com.eduelevate.lms.dto.instructor.InstructorResponseDto;
import com.eduelevate.lms.dto.instructor.UpdateInstructorDto;
import com.eduelevate.lms.security.SecurityUtils;
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
    }      @GetMapping("/{instructorId}") // Protected: Users can only access their own data (except Admins)
    public ResponseEntity<InstructorResponseDto> getInstructorById(@PathVariable Integer instructorId) {
        // Check if user can access this specific instructor's data
        if (!SecurityUtils.canAccessUserData(instructorId, "INSTRUCTOR")) {
            throw new RuntimeException("Access denied: You can only access your own data");
        }
        InstructorResponseDto instructor = instructorService.getInstructorById(instructorId);
        return new ResponseEntity<>(instructor, HttpStatus.OK);
    }
      @GetMapping // Protected: Only Admins can see all instructors
    public ResponseEntity<List<InstructorResponseDto>> getAllInstructors() {
        // Only Admins can access this endpoint - instructors and students cannot
        if (!SecurityUtils.isAdmin()) {
            throw new RuntimeException("Access denied: Only administrators can view instructor lists");
        }
        List<InstructorResponseDto> instructors = instructorService.getAllInstructors();
        return new ResponseEntity<>(instructors, HttpStatus.OK);
    }      @PutMapping("/{instructorId}") // Protected: Users can only update their own data (except Admins)
    public ResponseEntity<InstructorResponseDto> updateInstructor(
            @PathVariable Integer instructorId,
            @Valid @RequestBody UpdateInstructorDto updateInstructorDto) {
        // Check if user can modify this specific instructor's data
        if (!SecurityUtils.canAccessUserData(instructorId, "INSTRUCTOR")) {
            throw new RuntimeException("Access denied: You can only update your own data");
        }
        InstructorResponseDto updatedInstructor = instructorService.updateInstructor(instructorId, updateInstructorDto);
        return new ResponseEntity<>(updatedInstructor, HttpStatus.OK);
    }
      @DeleteMapping("/{instructorId}") // Protected: Users can only delete their own data (except Admins)
    public ResponseEntity<Void> deleteInstructor(@PathVariable Integer instructorId) {
        // Check if user can delete this specific instructor's data
        if (!SecurityUtils.canAccessUserData(instructorId, "INSTRUCTOR")) {
            throw new RuntimeException("Access denied: You can only delete your own data");
        }
        instructorService.deleteInstructor(instructorId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
