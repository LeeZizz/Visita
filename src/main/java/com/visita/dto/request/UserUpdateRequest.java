package com.visita.dto.request;

import java.time.LocalDate;

import com.visita.entities.Gender;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserUpdateRequest {

	private String fullName;
	private String phone;
	private Gender gender;
	private LocalDate dob;
	private String address;
}
