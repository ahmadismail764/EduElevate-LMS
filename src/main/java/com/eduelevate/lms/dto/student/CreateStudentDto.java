package com.eduelevate.lms.dto.student;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateStudentDto {
    private String username;
    private String email;
    private String password;
    private String firstName;
    private String lastName;
}
