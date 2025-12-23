package com.visita.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
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
import com.visita.entities.UserEntity;
import com.visita.services.UserService;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/users")
@Slf4j
public class UserController {

	private final UserService userService;

	public UserController(UserService userService) {
		this.userService = userService;
	}

	@PostMapping("/create")
	ApiResponse<UserEntity> createUser(@RequestBody @Valid UserCreateRequest userCreateRequest) {
		ApiResponse<UserEntity> apiResponse = new ApiResponse<>();
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
}
