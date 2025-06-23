package com.eduelevate.lms.dto.course;

import com.eduelevate.lms.entity.Enrollment;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EnrollmentResponseDto {
    
    private Integer enrollmentId;
    private Integer studentId;
    private String studentName;
    private String studentEmail;
    private Integer courseId;
    private String courseTitle;
    private Enrollment.EnrollmentStatus status;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime enrollmentDate;
}
