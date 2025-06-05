package com.eduelevate.lms.dto;
import com.eduelevate.lms.entity.Role;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateStudentDto {
    private String firstName;
    private String lastName;
    private Role role;
    // Note: We typically don't allow updating username, email, or password via this DTO
}
