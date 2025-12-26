package com.visita.services;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.visita.dto.request.UserCreateRequest;
import com.visita.dto.request.UserUpdateRequest;
import com.visita.dto.response.UserResponse;
import com.visita.entities.UserEntity;
import com.visita.exceptions.ErrorCode;
import com.visita.exceptions.WebException;
import com.visita.repositories.UserRepository;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class UserService {

	private final UserRepository userRepository;

	public UserService(UserRepository userRepository) {
		this.userRepository = userRepository;
	}

	public UserEntity createUserRequest(UserCreateRequest userCreateRequest) {
		if (userRepository.existsByEmail(userCreateRequest.getEmail())) {
			throw new WebException(ErrorCode.USER_EXISTED);
		}
		PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
		UserEntity userEntity = new UserEntity();
		userEntity.setFullName(userCreateRequest.getFullName());
		userEntity.setPassword(passwordEncoder.encode(userCreateRequest.getPassword()));
		userEntity.setPhone(userCreateRequest.getPhone());
		userEntity.setDob(userCreateRequest.getDob());
		userEntity.setEmail(userCreateRequest.getEmail());
		userEntity.setGender(userCreateRequest.getGender());
		userEntity.setAddress(userCreateRequest.getAddress());
		userEntity.setIsActive(true);
		userEntity.setCreatedAt(LocalDateTime.now());
		userEntity.setUpdatedAt(LocalDateTime.now());

		return userRepository.save(userEntity);
	}

	@PreAuthorize("hasAuthority('SCOPE_ADMIN')")
	public List<UserResponse> getAllUsers() {
		log.info("Fetching all users from the database");
		return userRepository.findAll().stream().map(this::mapToUserResponse).collect(Collectors.toList());
	}

	@PostAuthorize("returnObject.isPresent() && returnObject.get().email == authentication.name or hasAuthority('SCOPE_ADMIN')")
	public Optional<UserResponse> getUserById(String userId) {
		log.info("Fetching user with ID: {}", userId);
		return userRepository.findById(userId).map(this::mapToUserResponse);
	}

	public UserResponse getMyInfo() {
		log.info("Fetching my information");
		var context = SecurityContextHolder.getContext();
		String email = context.getAuthentication().getName();
		UserEntity userEntity = userRepository.findByEmail(email)
				.orElseThrow(() -> new WebException(ErrorCode.USER_NOT_FOUND));
		return mapToUserResponse(userEntity);
	}

	@PostAuthorize("returnObject.email == authentication.name or hasAuthority('SCOPE_ADMIN')")
	public UserResponse updateUser(String userId, UserUpdateRequest userCreateRequest) {
		UserEntity userEntity = userRepository.findById(userId)
				.orElseThrow(() -> new WebException(ErrorCode.USER_NOT_FOUND));

		userEntity.setFullName(userCreateRequest.getFullName());
		userEntity.setPhone(userCreateRequest.getPhone());
		userEntity.setDob(userCreateRequest.getDob());
		userEntity.setGender(userCreateRequest.getGender());
		userEntity.setAddress(userCreateRequest.getAddress());
		userEntity.setUpdatedAt(LocalDateTime.now());

		UserEntity updatedUser = userRepository.save(userEntity);

		return mapToUserResponse(updatedUser);
	}

	@PreAuthorize("hasAuthority('SCOPE_ADMIN')")
	public void deleteUser(String userId) {
		UserEntity userEntity = userRepository.findById(userId)
				.orElseThrow(() -> new WebException(ErrorCode.USER_NOT_FOUND));
		userRepository.delete(userEntity);
	}

	private UserResponse mapToUserResponse(UserEntity userEntity) {
		UserResponse userResponse = new UserResponse();
		userResponse.setUserId(userEntity.getUserId());
		userResponse.setFullName(userEntity.getFullName());
		userResponse.setEmail(userEntity.getEmail());
		userResponse.setPhone(userEntity.getPhone());
		userResponse.setDob(userEntity.getDob());
		userResponse.setGender(userEntity.getGender().name());
		userResponse.setAddress(userEntity.getAddress());
		userResponse.setIsActive(userEntity.getIsActive());
		userResponse.setCreatedAt(userEntity.getCreatedAt());
		userResponse.setUpdatedAt(userEntity.getUpdatedAt());
		return userResponse;
	}
}
