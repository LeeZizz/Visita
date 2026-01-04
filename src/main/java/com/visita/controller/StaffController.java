package com.visita.controller;

import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.visita.dto.response.ApiResponse;
import com.visita.dto.response.BookingDetailResponse;
import com.visita.services.BookingService;
import com.visita.services.TourService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/staffs")
@RequiredArgsConstructor
public class StaffController {

    private final TourService tourService;
    private final BookingService bookingService;

    private final com.visita.services.UserService userService;

    @GetMapping("/{id}/tours")
    public ApiResponse<Page<com.visita.dto.response.TourResponse>> getStaffTours(
            @PathVariable String id,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        ApiResponse<Page<com.visita.dto.response.TourResponse>> apiResponse = new ApiResponse<>();
        apiResponse.setResult(tourService.getToursByStaffId(id, page, size));
        return apiResponse;
    }

    @GetMapping("/{id}/bookings")
    public ApiResponse<Page<BookingDetailResponse>> getStaffBookings(
            @PathVariable String id,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        ApiResponse<Page<BookingDetailResponse>> apiResponse = new ApiResponse<>();
        apiResponse.setResult(bookingService.getBookingsByStaffId(id, page, size));
        return apiResponse;
    }

    @org.springframework.web.bind.annotation.PostMapping("/customers")
    public ApiResponse<com.visita.dto.response.UserResponse> createCustomer(
            @org.springframework.web.bind.annotation.RequestBody @jakarta.validation.Valid com.visita.dto.request.UserCreateRequest request) {
        ApiResponse<com.visita.dto.response.UserResponse> apiResponse = new ApiResponse<>();
        apiResponse.setResult(userService.createUserRequest(request));
        return apiResponse;
    }

    @org.springframework.web.bind.annotation.PostMapping("/booking")
    public ApiResponse<com.visita.dto.response.BookingResponse> createBookingForUser(
            @org.springframework.web.bind.annotation.RequestBody @jakarta.validation.Valid com.visita.dto.request.StaffBookingRequest request) {
        ApiResponse<com.visita.dto.response.BookingResponse> apiResponse = new ApiResponse<>();
        apiResponse.setResult(bookingService.createBookingForUser(request));
        return apiResponse;
    }
}
