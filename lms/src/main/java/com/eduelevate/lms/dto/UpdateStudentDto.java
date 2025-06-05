package com.eduelevate.lms.dto;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateStudentDto {
    private String firstName;
    private String lastName;
    // Note: We typically don't allow updating username, email, or password via this DTO
}
