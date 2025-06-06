package com.eduelevate.lms.service;

import com.eduelevate.lms.dto.admin.AdminResponseDto;
import com.eduelevate.lms.dto.admin.CreateAdminDto;
import com.eduelevate.lms.dto.admin.UpdateAdminDto;

import java.util.List;

public interface AdminService {
    
    List<AdminResponseDto> getAllAdmins();
    AdminResponseDto getAdminById(int id);
    AdminResponseDto createAdmin(CreateAdminDto createDto);
    AdminResponseDto updateAdmin(int id, UpdateAdminDto updateDto);
    void deleteAdmin(int id);
}