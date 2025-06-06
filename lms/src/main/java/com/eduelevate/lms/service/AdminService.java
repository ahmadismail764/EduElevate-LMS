package com.eduelevate.lms.service;

import com.eduelevate.lms.dto.AdminResponseDto;
import com.eduelevate.lms.dto.CreateAdminDto;
import com.eduelevate.lms.dto.UpdateAdminDto;

import java.util.List;

public interface AdminService {
    
    List<AdminResponseDto> getAllAdmins();
    AdminResponseDto getAdminById(int id);
    AdminResponseDto createAdmin(CreateAdminDto createDto);
    AdminResponseDto updateAdmin(int id, UpdateAdminDto updateDto);
    void deleteAdmin(int id);
}