package com.vitazi.dto.response;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {

	@Column(nullable = false, unique = true)
	private String userName;
	private Long userId;
	private String fullName;
	private String passWord;
	@Column(nullable = false, length = 100)
	private String email;

	@Column(unique = true, nullable = false, length = 15)
	private String phone;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private String gender;
	private String roleName;

	private LocalDate dob;
}
