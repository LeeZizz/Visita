package com.vitazi.services;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.vitazi.dto.request.UserCreateRequest;
import com.vitazi.dto.request.UserUpdateRequest;
import com.vitazi.dto.response.UserResponse;
import com.vitazi.entities.Gender;
import com.vitazi.entities.RoleEntity;
import com.vitazi.entities.UserEntity;
import com.vitazi.exceptions.ErrorCode;
import com.vitazi.exceptions.WebException;
import com.vitazi.repositories.RoleRepository;
import com.vitazi.repositories.UserRepository;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class UserService {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private RoleRepository roleRepository;

	public UserEntity createUserRequest(UserCreateRequest userCreateRequest) {
		RoleEntity userRole = roleRepository.findById(2L).orElseThrow(() -> new WebException(ErrorCode.ROLE_NOT_FOUND));
		UserEntity userEntity = new UserEntity();
		if (userRepository.existsByUserName(userCreateRequest.getUserName())) {
			throw new WebException(ErrorCode.USER_EXISTED);
		}
		PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
		userEntity.setRole(userRole);
		userEntity.setUserName(userCreateRequest.getUserName());
		userEntity.setFullName(userCreateRequest.getFullName());
		userEntity.setPassWord(passwordEncoder.encode(userCreateRequest.getPassWord()));
		userEntity.setPhone(userCreateRequest.getPhone());
		userEntity.setDob(userCreateRequest.getDob());
		userEntity.setEmail(userCreateRequest.getEmail());
		userEntity.setGender(Gender.valueOf(userCreateRequest.getGender()));

//        RoleEntity userRole = roleRepository.findById(2L)
//                .orElseThrow(() -> new RuntimeException("Role not found"));
//
//        if(userRepository.existsByUserName(userCreateRequest.getUserName())){
//            throw new RuntimeException("Username already exists");
//        }
//        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
//
//        UserEntity userEntity = UserEntity.builder()
//                .role(userRole)
//                .userName(userCreateRequest.getUserName())
//                .fullName(userCreateRequest.getFullName())
//                .passWord(passwordEncoder.encode(userCreateRequest.getPassWord()))
//                .phone(userCreateRequest.getPhone())
//                .dob(userCreateRequest.getDob())
//                .email(userCreateRequest.getEmail())
//                .gender(Gender.valueOf(userCreateRequest.getGender()))
//                .build();
		return userRepository.save(userEntity);
	}

	@PreAuthorize("hasAuthority('SCOPE_ADMIN')")
	public List<UserResponse> getAllUsers() {
		log.info("Fetching all users from the database");
		return userRepository.findAll().stream().map(this::mapToUserResponse).collect(Collectors.toList());

	}

	@PostAuthorize("returnObject.isPresent() && returnObject.get().userName == authentication.name or hasAuthority('SCOPE_ADMIN')")
	public Optional<UserResponse> getUserById(Long userId) {
		log.info("Fetching user with ID: {}", userId);
		return userRepository.findById(userId).map(this::mapToUserResponse);
	}

	@PostAuthorize("returnObject.isPresent() && returnObject.get().userName == authentication.name or hasAuthority('SCOPE_ADMIN')")
	public UserResponse getMyInfor() {
		log.info("Fetching my information");
		var context = SecurityContextHolder.getContext();
		String name = context.getAuthentication().getName();
		UserEntity userEntity = userRepository.findByUserName(name)
				.orElseThrow(() -> new WebException(ErrorCode.USER_NOT_FOUND));
		return mapToUserResponse(userEntity);
	}

	@PostAuthorize("returnObject.isPresent() && returnObject.get().userName == authentication.name or hasAuthority('SCOPE_ADMIN')")
	public UserResponse updateUser(Long userId, UserUpdateRequest userCreateRequest) {
		UserEntity userEntity = userRepository.findById(userId)
				.orElseThrow(() -> new WebException(ErrorCode.USER_NOT_FOUND));

		userEntity.setFullName(userCreateRequest.getFullName());
		PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
		userEntity.setPassWord(passwordEncoder.encode(userCreateRequest.getPassWord()));
		userEntity.setPhone(userCreateRequest.getPhone());
		userEntity.setDob(userCreateRequest.getDob());
		userEntity.setEmail(userCreateRequest.getEmail());
		userEntity.setGender(Gender.valueOf(userCreateRequest.getGender().toUpperCase()));
		userEntity.setUpdatedAt(LocalDate.now());

		UserEntity updatedUser = userRepository.save(userEntity);

		return mapToUserResponse(updatedUser);
	}

	@PostAuthorize("returnObject.isPresent() && returnObject.get().userName == authentication.name or hasAuthority('SCOPE_ADMIN')")
	public void deleteUser(Long userId) {
		UserEntity userEntity = userRepository.findById(userId)
				.orElseThrow(() -> new WebException(ErrorCode.USER_NOT_FOUND));
		userRepository.delete(userEntity);

	}

	private UserResponse mapToUserResponse(UserEntity userEntity) {
		UserResponse userResponse = new UserResponse();
		userResponse.setUserId(userEntity.getUserId());
		userResponse.setUserName(userEntity.getUserName());
		userResponse.setPassWord(userEntity.getPassWord());
		userResponse.setFullName(userEntity.getFullName());
		userResponse.setPhone(userEntity.getPhone());
		userResponse.setDob(userEntity.getDob());
		userResponse.setEmail(userEntity.getEmail());
		userResponse.setGender(userEntity.getGender().name());
		userResponse.setRoleName(userEntity.getRole().getRoleName());
		return userResponse;
	}
}
