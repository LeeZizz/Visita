package com.vitazi.dto.request;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserCreateRequest {

	@Column(nullable = false, unique = true)
	@Size(min = 5, message = "INVALID_USERNAME")
	private String userName;

	@Column(nullable = false)
	@Size(min = 8, message = "INVALID_PASSWORD")
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
