package com.eduelevate.lms.service;

import com.eduelevate.lms.dto.admin.AdminResponseDto;
import com.eduelevate.lms.dto.admin.CreateAdminDto;
import com.eduelevate.lms.dto.admin.UpdateAdminDto;
import com.eduelevate.lms.entity.Admin;
import com.eduelevate.lms.repository.AdminRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AdminServiceImpl implements AdminService {

    private final AdminRepository adminRepository;
    private final PasswordEncoder passwordEncoder;

    public AdminServiceImpl(AdminRepository adminRepository, PasswordEncoder passwordEncoder) {
        this.adminRepository = adminRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public List<AdminResponseDto> getAllAdmins() {
        List<Admin> admins = adminRepository.findAll();
        return admins.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    public AdminResponseDto getAdminById(int id) {
        Admin admin = adminRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Admin not found with id: " + id));
        return convertToDto(admin);
    }

    @Override
    public AdminResponseDto createAdmin(CreateAdminDto createDto) {
        // Check for duplicate email
        if (adminRepository.existsByEmail(createDto.getEmail())) {
            throw new RuntimeException("Admin with email " + createDto.getEmail() + " already exists");
        }
        
        // Check for duplicate username
        if (adminRepository.existsByUsername(createDto.getUsername())) {
            throw new RuntimeException("Admin with username " + createDto.getUsername() + " already exists");
        }        Admin admin = new Admin();
        admin.setUsername(createDto.getUsername());
        admin.setEmail(createDto.getEmail());
        admin.setPassword(passwordEncoder.encode(createDto.getPassword()));
        admin.setFirstName(createDto.getFirstName());
        admin.setLastName(createDto.getLastName());

        Admin savedAdmin = adminRepository.save(admin);
        return convertToDto(savedAdmin);
    }

    @Override
    public AdminResponseDto updateAdmin(int id, UpdateAdminDto updateDto) {
        Admin admin = adminRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Admin not found with id: " + id));

        // Check for duplicate email if email is being updated
        if (updateDto.getEmail() != null && !updateDto.getEmail().equals(admin.getEmail())) {
            if (adminRepository.existsByEmail(updateDto.getEmail())) {
                throw new RuntimeException("Admin with email " + updateDto.getEmail() + " already exists");
            }
            admin.setEmail(updateDto.getEmail());
        }

        // Check for duplicate username if username is being updated
        if (updateDto.getUsername() != null && !updateDto.getUsername().equals(admin.getUsername())) {
            if (adminRepository.existsByUsername(updateDto.getUsername())) {
                throw new RuntimeException("Admin with username " + updateDto.getUsername() + " already exists");
            }
            admin.setUsername(updateDto.getUsername());
        }        // Update other fields if provided
        if (updateDto.getFirstName() != null) {
            admin.setFirstName(updateDto.getFirstName());
        }
        if (updateDto.getLastName() != null) {
            admin.setLastName(updateDto.getLastName());
        }
        if (updateDto.getPassword() != null) {
            admin.setPassword(passwordEncoder.encode(updateDto.getPassword()));
        }

        Admin updatedAdmin = adminRepository.save(admin);
        return convertToDto(updatedAdmin);
    }

    @Override
    public void deleteAdmin(int id) {
        if (!adminRepository.existsById(id)) {
            throw new RuntimeException("Admin not found with id: " + id);
        }
        adminRepository.deleteById(id);
    }

    // Helper method to convert Admin entity to AdminResponseDto
    private AdminResponseDto convertToDto(Admin admin) {
        AdminResponseDto dto = new AdminResponseDto();
        dto.setAdminId(admin.getAdminId());
        dto.setUsername(admin.getUsername());
        dto.setEmail(admin.getEmail());
        dto.setFirstName(admin.getFirstName());
        dto.setLastName(admin.getLastName());
        dto.setCreatedAt(admin.getCreatedAt());
        dto.setUpdatedAt(admin.getUpdatedAt());
        return dto;
    }
}
