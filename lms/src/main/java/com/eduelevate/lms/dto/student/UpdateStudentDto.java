package com.eduelevate.lms.dto.student;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateStudentDto {
    private String username;
    private String email;
    private String firstName;
    private String lastName;
    // Note: Password updates should be handled separately for security reasons
}
