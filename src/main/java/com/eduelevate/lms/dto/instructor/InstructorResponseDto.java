package com.eduelevate.lms.dto.instructor;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InstructorResponseDto {
    private Integer instructorId;
    private String username;
    private String email;
    private String firstName;
    private String lastName;
    private String department;
    private String bio;
    private String specialization;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
