package com.vitazi.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.vitazi.dto.request.UserCreateRequest;
import com.vitazi.dto.request.UserUpdateRequest;
import com.vitazi.dto.response.ApiResponse;
import com.vitazi.dto.response.UserResponse;
import com.vitazi.entities.UserEntity;
import com.vitazi.services.UserService;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/users")
@Slf4j
public class UserController {

	@Autowired
	private UserService userService;

	@PostMapping("/create")
	ApiResponse<UserEntity> createUser(@RequestBody @Valid UserCreateRequest userCreateRequest) {
		ApiResponse<UserEntity> apiResponse = new ApiResponse<>();
		apiResponse.setResult(userService.createUserRequest(userCreateRequest));
		return apiResponse;
	}

	@GetMapping("/listUsers")
	ApiResponse<List<UserResponse>> listUsers() {
		var authentication = SecurityContextHolder.getContext().getAuthentication();
		log.info("Username: " + authentication.getName());
		authentication.getAuthorities().forEach(grantedAuthority -> log.info(grantedAuthority.getAuthority()));

		ApiResponse<List<UserResponse>> apiResponse = new ApiResponse<>();
		apiResponse.setResult(userService.getAllUsers());
		return apiResponse;
	}

	@GetMapping("/{id}")
	ApiResponse<Optional<UserResponse>> getUserById(@PathVariable Long id) {
		ApiResponse<Optional<UserResponse>> apiResponse = new ApiResponse<>();
		apiResponse.setResult(userService.getUserById(id));
		return apiResponse;
	}
//    Optional<UserResponse> getUserById(@PathVariable Long id){
//        return userService.getUserById(id);
//    }

	@GetMapping("/myInfor")
	ApiResponse<UserResponse> getMyInfor() {
		ApiResponse<UserResponse> apiResponse = new ApiResponse<>();
		apiResponse.setResult(userService.getMyInfor());
		return apiResponse;
	}
//    UserResponse getMyInfor(){
//        return userService.getMyInfor();
//    }

	@PutMapping("/update/{id}")
	ApiResponse<UserResponse> updateUser(@PathVariable Long id, @RequestBody UserUpdateRequest userUpdateRequest) {
		ApiResponse<UserResponse> apiResponse = new ApiResponse<>();
		apiResponse.setResult(userService.updateUser(id, userUpdateRequest));
		return apiResponse;
	}
//    UserResponse updateUser(@PathVariable Long id, @RequestBody UserUpdateRequest userUpdateRequest) {
//        return userService.updateUser(id, userUpdateRequest);
//    }

	@DeleteMapping("/delete/{id}")
	ApiResponse<String> deleteUser(@PathVariable Long id) {
		ApiResponse<String> apiResponse = new ApiResponse<>();
		userService.deleteUser(id);
		apiResponse.setResult("User đã bị xoá");
		return apiResponse;
	}
//    String deleteUser(@PathVariable Long id) {
//        userService.deleteUser(id);
//        return "User đã bị xoá";
//    }
}
