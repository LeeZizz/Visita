package com.visita.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.visita.dto.request.UserCreateRequest;
import com.visita.dto.request.UserUpdateRequest;
import com.visita.dto.response.ApiResponse;
import com.visita.dto.response.UserResponse;
import com.visita.services.UserService;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/users")
@Slf4j
public class UserController {

	private final UserService userService;
	private final com.visita.services.BookingService bookingService;

	public UserController(UserService userService, com.visita.services.BookingService bookingService) {
		this.userService = userService;
		this.bookingService = bookingService;
	}

	@PostMapping("/create")
	ApiResponse<UserResponse> createUser(@RequestBody @Valid UserCreateRequest userCreateRequest) {
		ApiResponse<UserResponse> apiResponse = new ApiResponse<>();
		apiResponse.setResult(userService.createUserRequest(userCreateRequest));
		return apiResponse;
	}

	@GetMapping("/myInfo")
	ApiResponse<UserResponse> getMyInfo() {
		ApiResponse<UserResponse> apiResponse = new ApiResponse<>();
		apiResponse.setResult(userService.getMyInfo());
		return apiResponse;
	}

	@PutMapping("/update/{id}")
	ApiResponse<UserResponse> updateUser(@PathVariable String id, @RequestBody UserUpdateRequest userUpdateRequest) {
		ApiResponse<UserResponse> apiResponse = new ApiResponse<>();
		apiResponse.setResult(userService.updateUser(id, userUpdateRequest));
		return apiResponse;
	}

	@GetMapping("/bookings/active")
	ApiResponse<org.springframework.data.domain.Page<com.visita.dto.response.BookingDetailResponse>> getActiveBookings(
			@org.springframework.web.bind.annotation.RequestParam(defaultValue = "1") int page,
			@org.springframework.web.bind.annotation.RequestParam(defaultValue = "5") int size) {
		ApiResponse<org.springframework.data.domain.Page<com.visita.dto.response.BookingDetailResponse>> apiResponse = new ApiResponse<>();
		apiResponse.setResult(bookingService.getMyActiveBookings(page - 1, size));
		return apiResponse;
	}

	@GetMapping("/bookings/history")
	ApiResponse<org.springframework.data.domain.Page<com.visita.dto.response.BookingDetailResponse>> getCompletedBookings(
			@org.springframework.web.bind.annotation.RequestParam(defaultValue = "1") int page,
			@org.springframework.web.bind.annotation.RequestParam(defaultValue = "5") int size) {
		ApiResponse<org.springframework.data.domain.Page<com.visita.dto.response.BookingDetailResponse>> apiResponse = new ApiResponse<>();
		apiResponse.setResult(bookingService.getMyCompletedBookings(page - 1, size));
		return apiResponse;
	}
}
