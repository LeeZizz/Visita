package com.vitazi.dto.request;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserUpdateRequest {

	@Column(nullable = false)
	private String passWord;

	private String fullName;

	@Column(nullable = false, length = 100)
	private String email;

	@Column(unique = true, nullable = false, length = 15)
	private String phone;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private String gender;

	private LocalDate dob;
}
