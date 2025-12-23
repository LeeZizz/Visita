package com.visita.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.visita.dto.response.AdminResponse;
import com.visita.dto.response.ApiResponse;
import com.visita.services.AdminService;

import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/admins")
@Slf4j
public class AdminController {

    private final AdminService adminService;
    private final com.visita.services.UserService userService;

    public AdminController(AdminService adminService, com.visita.services.UserService userService) {
        this.adminService = adminService;
        this.userService = userService;
    }

    @GetMapping("/myInfo")
    ApiResponse<AdminResponse> getMyInfo() {
        ApiResponse<AdminResponse> apiResponse = new ApiResponse<>();
        apiResponse.setResult(adminService.getMyInfo());
        return apiResponse;
    }

    @GetMapping("/users")
    ApiResponse<java.util.List<com.visita.dto.response.UserResponse>> listUsers() {
        var authentication = org.springframework.security.core.context.SecurityContextHolder.getContext()
                .getAuthentication();
        log.info("Username: {}", authentication.getName());
        authentication.getAuthorities().forEach(grantedAuthority -> log.info(grantedAuthority.getAuthority()));

        ApiResponse<java.util.List<com.visita.dto.response.UserResponse>> apiResponse = new ApiResponse<>();
        apiResponse.setResult(userService.getAllUsers());
        return apiResponse;
    }

    @GetMapping("/users/{id:[0-9a-f\\\\-]{36}}")
    ApiResponse<java.util.Optional<com.visita.dto.response.UserResponse>> getUserById(
            @org.springframework.web.bind.annotation.PathVariable String id) {
        ApiResponse<java.util.Optional<com.visita.dto.response.UserResponse>> apiResponse = new ApiResponse<>();
        apiResponse.setResult(userService.getUserById(id));
        return apiResponse;
    }

    @org.springframework.web.bind.annotation.PutMapping("/users/{id}")
    ApiResponse<com.visita.dto.response.UserResponse> updateUser(
            @org.springframework.web.bind.annotation.PathVariable String id,
            @org.springframework.web.bind.annotation.RequestBody com.visita.dto.request.UserUpdateRequest userUpdateRequest) {
        ApiResponse<com.visita.dto.response.UserResponse> apiResponse = new ApiResponse<>();
        apiResponse.setResult(userService.updateUser(id, userUpdateRequest));
        return apiResponse;
    }

    @org.springframework.web.bind.annotation.DeleteMapping("/users/{id}")
    ApiResponse<String> deleteUser(@org.springframework.web.bind.annotation.PathVariable String id) {
        ApiResponse<String> apiResponse = new ApiResponse<>();
        userService.deleteUser(id);
        apiResponse.setResult("User deleted successfully");
        return apiResponse;
    }
}
