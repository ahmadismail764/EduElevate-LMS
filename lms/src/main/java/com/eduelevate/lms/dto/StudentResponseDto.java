package com.eduelevate.lms.dto;
import com.eduelevate.lms.entity.Role;
import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StudentResponseDto {
    private int studentId;
    private String username;
    private String firstName;
    private String lastName;
    private String email;
    private Role role;
}