package com.eduelevate.lms.dto.course;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CourseCreateDto {
    
    @NotBlank(message = "Course title is required")
    @Size(max = 200, message = "Course title must not exceed 200 characters")
    private String title;
    
    @Size(max = 1000, message = "Course description must not exceed 1000 characters")
    private String description;
    
    @Min(value = 1, message = "Duration must be at least 1 week")
    @Max(value = 52, message = "Duration must not exceed 52 weeks")
    private Integer durationWeeks;
    
    @Min(value = 1, message = "Maximum students must be at least 1")
    @Max(value = 500, message = "Maximum students must not exceed 500")
    private Integer maxStudents = 50;
    
    @NotNull(message = "Instructor ID is required")
    private Integer instructorId;
}
