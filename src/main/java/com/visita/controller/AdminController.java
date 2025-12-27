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
    private final com.visita.services.TourService tourService;
    private final com.visita.services.PromotionService promotionService;

    public AdminController(AdminService adminService, com.visita.services.UserService userService,
            com.visita.services.TourService tourService, com.visita.services.PromotionService promotionService) {
        this.adminService = adminService;
        this.userService = userService;
        this.tourService = tourService;
        this.promotionService = promotionService;
    }

    @GetMapping("/myInfo")
    ApiResponse<AdminResponse> getMyInfo() {
        ApiResponse<AdminResponse> apiResponse = new ApiResponse<>();
        apiResponse.setResult(adminService.getMyInfo());
        return apiResponse;
    }

    // --- User Management ---

    @GetMapping("/users")
    ApiResponse<org.springframework.data.domain.Page<com.visita.dto.response.UserResponse>> listUsers(
            @org.springframework.web.bind.annotation.RequestParam(defaultValue = "1") int page,
            @org.springframework.web.bind.annotation.RequestParam(defaultValue = "5") int size) {
        ApiResponse<org.springframework.data.domain.Page<com.visita.dto.response.UserResponse>> apiResponse = new ApiResponse<>();
        apiResponse.setResult(userService.getAllUsers(page - 1, size));
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

    @org.springframework.web.bind.annotation.PatchMapping("/users/{id}/status")
    ApiResponse<String> updateUserStatus(
            @org.springframework.web.bind.annotation.PathVariable String id,
            @org.springframework.web.bind.annotation.RequestParam boolean isActive) {
        userService.updateUserStatus(id, isActive);
        ApiResponse<String> apiResponse = new ApiResponse<>();
        apiResponse.setResult("User status updated successfully");
        return apiResponse;
    }

    @org.springframework.web.bind.annotation.DeleteMapping("/users/{id}")
    ApiResponse<String> deleteUser(@org.springframework.web.bind.annotation.PathVariable String id) {
        ApiResponse<String> apiResponse = new ApiResponse<>();
        userService.deleteUser(id);
        apiResponse.setResult("User deleted successfully");
        return apiResponse;
    }

    // --- Staff Management ---

    @org.springframework.web.bind.annotation.PostMapping("/staffs")
    ApiResponse<com.visita.dto.response.UserResponse> createStaff(
            @org.springframework.web.bind.annotation.RequestBody @jakarta.validation.Valid com.visita.dto.request.UserCreateRequest request) {
        ApiResponse<com.visita.dto.response.UserResponse> apiResponse = new ApiResponse<>();
        apiResponse.setResult(userService.createStaff(request));
        return apiResponse;
    }

    @org.springframework.web.bind.annotation.GetMapping("/staffs")
    ApiResponse<org.springframework.data.domain.Page<com.visita.dto.response.UserResponse>> listStaffs(
            @org.springframework.web.bind.annotation.RequestParam(defaultValue = "1") int page,
            @org.springframework.web.bind.annotation.RequestParam(defaultValue = "5") int size) {
        ApiResponse<org.springframework.data.domain.Page<com.visita.dto.response.UserResponse>> apiResponse = new ApiResponse<>();
        apiResponse.setResult(userService.getAllStaffs(page - 1, size));
        return apiResponse;
    }

    // --- Tour Management ---

    @org.springframework.web.bind.annotation.PostMapping("/tours")
    ApiResponse<com.visita.entities.TourEntity> createTour(
            @org.springframework.web.bind.annotation.RequestBody @jakarta.validation.Valid com.visita.dto.request.TourRequest request) {
        ApiResponse<com.visita.entities.TourEntity> apiResponse = new ApiResponse<>();
        apiResponse.setResult(tourService.createTour(request));
        return apiResponse;
    }

    @org.springframework.web.bind.annotation.PutMapping("/tours/{id}")
    ApiResponse<com.visita.entities.TourEntity> updateTour(
            @org.springframework.web.bind.annotation.PathVariable String id,
            @org.springframework.web.bind.annotation.RequestBody com.visita.dto.request.TourRequest request) {
        ApiResponse<com.visita.entities.TourEntity> apiResponse = new ApiResponse<>();
        apiResponse.setResult(tourService.updateTour(id, request));
        return apiResponse;
    }

    @org.springframework.web.bind.annotation.DeleteMapping("/tours/{id}")
    ApiResponse<String> deleteTour(@org.springframework.web.bind.annotation.PathVariable String id) {
        tourService.deleteTour(id);
        ApiResponse<String> apiResponse = new ApiResponse<>();
        apiResponse.setResult("Tour deleted successfully");
        return apiResponse;
    }

    @org.springframework.web.bind.annotation.PatchMapping("/tours/{id}/status")
    ApiResponse<String> updateTourStatus(
            @org.springframework.web.bind.annotation.PathVariable String id,
            @org.springframework.web.bind.annotation.RequestParam boolean isActive) {
        tourService.updateStatus(id, isActive);
        ApiResponse<String> apiResponse = new ApiResponse<>();
        apiResponse.setResult("Tour status updated successfully");
        return apiResponse;
    }

    @org.springframework.web.bind.annotation.GetMapping("/tours")
    ApiResponse<java.util.List<com.visita.entities.TourEntity>> getAllTours() {
        ApiResponse<java.util.List<com.visita.entities.TourEntity>> apiResponse = new ApiResponse<>();
        apiResponse.setResult(tourService.getAllTours());
        return apiResponse;
    }

    // --- Promotion Management ---

    @org.springframework.web.bind.annotation.PostMapping("/promotions")
    ApiResponse<com.visita.entities.PromotionEntity> createPromotion(
            @org.springframework.web.bind.annotation.RequestBody @jakarta.validation.Valid com.visita.dto.request.PromotionRequest request) {
        ApiResponse<com.visita.entities.PromotionEntity> apiResponse = new ApiResponse<>();
        apiResponse.setResult(promotionService.createPromotion(request));
        return apiResponse;
    }

    @org.springframework.web.bind.annotation.PutMapping("/promotions/{id}")
    ApiResponse<com.visita.entities.PromotionEntity> updatePromotion(
            @org.springframework.web.bind.annotation.PathVariable String id,
            @org.springframework.web.bind.annotation.RequestBody com.visita.dto.request.PromotionRequest request) {
        ApiResponse<com.visita.entities.PromotionEntity> apiResponse = new ApiResponse<>();
        apiResponse.setResult(promotionService.updatePromotion(id, request));
        return apiResponse;
    }

    @org.springframework.web.bind.annotation.DeleteMapping("/promotions/{id}")
    ApiResponse<String> deletePromotion(@org.springframework.web.bind.annotation.PathVariable String id) {
        promotionService.deletePromotion(id);
        ApiResponse<String> apiResponse = new ApiResponse<>();
        apiResponse.setResult("Promotion deleted successfully");
        return apiResponse;
    }

    @org.springframework.web.bind.annotation.PatchMapping("/promotions/{id}/status")
    ApiResponse<String> updatePromotionStatus(
            @org.springframework.web.bind.annotation.PathVariable String id,
            @org.springframework.web.bind.annotation.RequestParam boolean isActive) {
        promotionService.updateStatus(id, isActive);
        ApiResponse<String> apiResponse = new ApiResponse<>();
        apiResponse.setResult("Promotion status updated successfully");
        return apiResponse;
    }

    @org.springframework.web.bind.annotation.GetMapping("/promotions")
    ApiResponse<java.util.List<com.visita.entities.PromotionEntity>> getAllPromotions() {
        ApiResponse<java.util.List<com.visita.entities.PromotionEntity>> apiResponse = new ApiResponse<>();
        apiResponse.setResult(promotionService.getAllPromotions());
        return apiResponse;
    }
}
