package com.visita.services;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.visita.dto.response.AdminResponse;
import com.visita.entities.AdminEntity;
import com.visita.exceptions.ErrorCode;
import com.visita.exceptions.WebException;
import com.visita.repositories.AdminRepository;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class AdminService {

    private final AdminRepository adminRepository;

    public AdminService(AdminRepository adminRepository) {
        this.adminRepository = adminRepository;
    }

    public AdminResponse getMyInfo() {
        var context = SecurityContextHolder.getContext();
        String username = context.getAuthentication().getName();
        log.info("Fetching admin info for username: {}", username);
        AdminEntity adminEntity = adminRepository.findByUsername(username)
                .orElseThrow(() -> new WebException(ErrorCode.USER_NOT_FOUND)); // Or create specific ADMIN_NOT_FOUND

        return mapToAdminResponse(adminEntity);
    }

    private AdminResponse mapToAdminResponse(AdminEntity adminEntity) {
        return AdminResponse.builder().adminId(adminEntity.getAdminId()).username(adminEntity.getUsername())
                .fullName(adminEntity.getFullName()).email(adminEntity.getEmail())
                .createdAt(adminEntity.getCreatedAt()).build();
    }
}
