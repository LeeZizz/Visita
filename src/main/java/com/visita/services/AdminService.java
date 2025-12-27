package com.visita.services;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.visita.dto.response.AdminResponse;
import com.visita.entities.UserEntity;
import com.visita.exceptions.ErrorCode;
import com.visita.exceptions.WebException;
import com.visita.repositories.UserRepository;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class AdminService {

    private final UserRepository userRepository;

    public AdminService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @PreAuthorize("hasRole('ADMIN')")
    public AdminResponse getMyInfo() {
        var context = SecurityContextHolder.getContext();
        String username = context.getAuthentication().getName();
        log.info("Fetching admin info for username: {}", username);

        // Admin is now a User with ADMIN role
        UserEntity userEntity = userRepository.findByUsername(username)
                .orElseThrow(() -> new WebException(ErrorCode.USER_NOT_FOUND));

        return mapToAdminResponse(userEntity);
    }

    private AdminResponse mapToAdminResponse(UserEntity userEntity) {
        return AdminResponse.builder()
                .adminId(userEntity.getUserId()) // Map userId to adminId
                .username(userEntity.getUsername())
                .fullName(userEntity.getFullName())
                .email(userEntity.getEmail())
                .createdAt(userEntity.getCreatedAt())
                .build();
    }
}
