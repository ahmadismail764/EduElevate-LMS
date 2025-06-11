package com.eduelevate.lms.dto.course;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CourseResponseDto {
      private Integer courseId;
    private String title;
    private String description;
    private Integer durationWeeks;
    
    @JsonProperty("maxStudents")
    private Integer maxStudents;
    
    private Integer currentEnrollments;
    private Integer availableSpots;
    
    // Instructor information
    private Integer instructorId;
    private String instructorName;
    private String instructorEmail;
    private String instructorDepartment;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;
      // Computed properties
    public Integer getAvailableSpots() {
        if (maxStudents != null && currentEnrollments != null) {
            return Math.max(0, maxStudents - currentEnrollments);
        }
        return 0;
    }
    
    public boolean hasAvailableSpots() {
        return getAvailableSpots() > 0;
    }
    
    // Alias methods for capacity (for backward compatibility with tests)
    @JsonProperty("capacity")
    public Integer getCapacity() {
        return maxStudents;
    }
    
    public void setCapacity(Integer capacity) {
        this.maxStudents = capacity;
    }
}
