package com.eduelevate.lms.controller;

import com.eduelevate.lms.dto.AdminResponseDto;
import com.eduelevate.lms.dto.CreateAdminDto;
import com.eduelevate.lms.dto.UpdateAdminDto;
import com.eduelevate.lms.service.AdminService;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/admins")
public class AdminController {
    
    private final AdminService adminService;
    
    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }
    
    @GetMapping  // Tested and working
    public List<AdminResponseDto> getAllAdmins() {
        return adminService.getAllAdmins();
    }
    
    @GetMapping("/{id}") // Tested and working
    public AdminResponseDto getAdminById(@PathVariable int id) {
        return adminService.getAdminById(id);
    }
    
    @PostMapping // Tested and working
    public AdminResponseDto createAdmin(@RequestBody CreateAdminDto createDto) {
        return adminService.createAdmin(createDto);
    }
    
    @PutMapping("/{id}") // Tested and working
    public AdminResponseDto updateAdmin(@PathVariable int id, @RequestBody UpdateAdminDto updateDto) {
        return adminService.updateAdmin(id, updateDto);
    }
    
    @DeleteMapping("/{id}") // Tested and working
    public void deleteAdmin(@PathVariable int id) {
        adminService.deleteAdmin(id);
    }
}
