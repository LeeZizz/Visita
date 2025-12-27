package com.visita.services;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.visita.dto.request.UserCreateRequest;
import com.visita.dto.request.UserUpdateRequest;
import com.visita.dto.response.UserResponse;
import com.visita.entities.RoleEntity;
import com.visita.entities.UserEntity;
import com.visita.exceptions.ErrorCode;
import com.visita.exceptions.WebException;
import com.visita.repositories.RoleRepository;
import com.visita.repositories.UserRepository;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class UserService {

	private final UserRepository userRepository;
	private final RoleRepository roleRepository;
	private final PasswordEncoder passwordEncoder;

	public UserService(UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder) {
		this.userRepository = userRepository;
		this.roleRepository = roleRepository;
		this.passwordEncoder = passwordEncoder;
	}

	public UserResponse createUserRequest(UserCreateRequest userCreateRequest) {
		if (userRepository.existsByEmail(userCreateRequest.getEmail())) {
			throw new WebException(ErrorCode.USER_EXISTED);
		}

		UserEntity userEntity = new UserEntity();
		userEntity.setFullName(userCreateRequest.getFullName());
		userEntity.setPassword(passwordEncoder.encode(userCreateRequest.getPassword()));
		userEntity.setPhone(userCreateRequest.getPhone());
		userEntity.setDob(userCreateRequest.getDob());
		userEntity.setEmail(userCreateRequest.getEmail());
		userEntity.setGender(userCreateRequest.getGender());
		userEntity.setAddress(userCreateRequest.getAddress());
		userEntity.setIsActive(true);

		// Assign default USER role
		RoleEntity userRole = roleRepository.findById("USER").orElseGet(() -> {
			return RoleEntity.builder().name("USER").description("User role").build();
		});
		var roles = new HashSet<RoleEntity>();
		roles.add(userRole);
		userEntity.setRoles(roles);

		return mapToUserResponse(userRepository.save(userEntity));
	}

	@PreAuthorize("hasRole('ADMIN')")
	public org.springframework.data.domain.Page<UserResponse> getAllUsers(int page, int size) {
		log.info("Fetching all users with page: {} and size: {}", page, size);
		org.springframework.data.domain.Pageable pageable = org.springframework.data.domain.PageRequest.of(page, size);
		return userRepository.findAllByRoles_Name("USER", pageable).map(this::mapToUserResponse);
	}

	@PostAuthorize("returnObject.isPresent() && (returnObject.get().email == authentication.name || returnObject.get().username == authentication.name) || hasRole('ADMIN')")
	public Optional<UserResponse> getUserById(String userId) {
		log.info("Fetching user with ID: {}", userId);
		return userRepository.findById(userId).map(this::mapToUserResponse);
	}

	public UserResponse getMyInfo() {
		log.info("Fetching my information");
		var context = SecurityContextHolder.getContext();
		String name = context.getAuthentication().getName(); // Can be email or username
		UserEntity userEntity = userRepository.findByUsername(name)
				.or(() -> userRepository.findByEmail(name))
				.orElseThrow(() -> new WebException(ErrorCode.USER_NOT_FOUND));
		return mapToUserResponse(userEntity);
	}

	@PostAuthorize("(returnObject.email == authentication.name || returnObject.username == authentication.name) || hasRole('ADMIN')")
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

	@PreAuthorize("hasRole('ADMIN')")
	public void deleteUser(String userId) {
		UserEntity userEntity = userRepository.findById(userId)
				.orElseThrow(() -> new WebException(ErrorCode.USER_NOT_FOUND));
		userRepository.delete(userEntity);
	}

	private UserResponse mapToUserResponse(UserEntity userEntity) {
		UserResponse userResponse = new UserResponse();
		userResponse.setUserId(userEntity.getUserId());
		userResponse.setUsername(userEntity.getUsername());
		userResponse.setFullName(userEntity.getFullName());
		userResponse.setEmail(userEntity.getEmail());
		userResponse.setPhone(userEntity.getPhone());
		userResponse.setDob(userEntity.getDob());
		userResponse.setGender(userEntity.getGender() != null ? userEntity.getGender().name() : null);
		userResponse.setAddress(userEntity.getAddress());
		userResponse.setIsActive(userEntity.getIsActive());
		userResponse.setCreatedAt(userEntity.getCreatedAt());
		userResponse.setUpdatedAt(userEntity.getUpdatedAt());

		if (userEntity.getRoles() != null) {
			userResponse.setRoles(userEntity.getRoles().stream()
					.map(RoleEntity::getName)
					.collect(Collectors.toSet()));
		}

		return userResponse;
	}
}
