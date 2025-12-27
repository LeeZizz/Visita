package com.visita.dto.response;

import java.time.LocalDate;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {
	private String userId;
	private String username;
	private String fullName;
	private String email;
	private String phone;
	private String gender;
	private LocalDate dob;
	private String address;
	private Boolean isActive;
	private java.util.Set<String> roles;
	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;
}
