package com.visita.dto.request;

import java.time.LocalDate;

import com.visita.entities.Gender;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserCreateRequest {

	@Email(message = "INVALID_EMAIL")
	private String email;

	@Size(min = 8, message = "INVALID_PASSWORD")
	private String password;

	private String fullName;

	@Size(max = 15, message = "INVALID_PHONE")
	private String phone;

	private Gender gender;

	private LocalDate dob;

	private String address;
}
