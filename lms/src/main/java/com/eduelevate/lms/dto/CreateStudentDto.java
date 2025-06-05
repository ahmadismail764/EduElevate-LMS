package com.eduelevate.lms.dto;
import com.eduelevate.lms.entity.Role;
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
    private Role role;
}