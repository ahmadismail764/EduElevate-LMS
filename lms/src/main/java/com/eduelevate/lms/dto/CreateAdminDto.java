package com.eduelevate.lms.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateAdminDto {
    
    private String username;
    private String email;
    private String password;
    private String firstName;
    private String lastName;
}