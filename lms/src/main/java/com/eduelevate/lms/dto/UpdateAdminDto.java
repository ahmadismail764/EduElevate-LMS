package com.eduelevate.lms.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateAdminDto {
    
    private String username;
    private String email;
    private String firstName;
    private String lastName;
    // Note: Password updates should be handled separately for security
}