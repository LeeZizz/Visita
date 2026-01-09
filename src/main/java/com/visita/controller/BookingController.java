package com.visita.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.visita.dto.request.BookingRequest;
import com.visita.dto.response.ApiResponse;
import com.visita.dto.response.BookingResponse;
import com.visita.services.BookingService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;

    @PostMapping
    public ApiResponse<BookingResponse> createBooking(@RequestBody @Valid BookingRequest request) {
        ApiResponse<BookingResponse> apiResponse = new ApiResponse<>();
        apiResponse.setResult(bookingService.createBooking(request));
        return apiResponse;
    }

    @org.springframework.web.bind.annotation.PutMapping("/{id}/cancel")
    public ApiResponse<Void> cancelBooking(@org.springframework.web.bind.annotation.PathVariable String id) {
        bookingService.cancelBooking(id);
        ApiResponse<Void> apiResponse = new ApiResponse<>();
        apiResponse.setMessage("Booking cancelled successfully");
        return apiResponse;
    }

    @org.springframework.web.bind.annotation.GetMapping("/active")
    public ApiResponse<org.springframework.data.domain.Page<com.visita.dto.response.BookingDetailResponse>> getActiveBookings(
            @org.springframework.web.bind.annotation.RequestParam(defaultValue = "1") int page,
            @org.springframework.web.bind.annotation.RequestParam(defaultValue = "5") int size) {
        ApiResponse<org.springframework.data.domain.Page<com.visita.dto.response.BookingDetailResponse>> apiResponse = new ApiResponse<>();
        apiResponse.setResult(bookingService.getMyActiveBookings(page - 1, size));
        return apiResponse;
    }

    @org.springframework.web.bind.annotation.GetMapping("/history")
    public ApiResponse<org.springframework.data.domain.Page<com.visita.dto.response.BookingDetailResponse>> getCompletedBookings(
            @org.springframework.web.bind.annotation.RequestParam(defaultValue = "1") int page,
            @org.springframework.web.bind.annotation.RequestParam(defaultValue = "5") int size) {
        ApiResponse<org.springframework.data.domain.Page<com.visita.dto.response.BookingDetailResponse>> apiResponse = new ApiResponse<>();
        apiResponse.setResult(bookingService.getMyCompletedBookings(page - 1, size));
        return apiResponse;
    }
}
