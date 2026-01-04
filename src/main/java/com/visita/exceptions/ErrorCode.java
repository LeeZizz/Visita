package com.visita.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorCode {

	UNKNOWN_ERROR(9999, "Unchecked error occurred", HttpStatus.INTERNAL_SERVER_ERROR),
	USER_EXISTED(1001, "User already exists", HttpStatus.BAD_REQUEST),
	USER_NOT_FOUND(1002, "User not found", HttpStatus.NOT_FOUND),
	ROLE_NOT_FOUND(1004, "Role not found", HttpStatus.NOT_FOUND),
	INVALID_USERNAME(1005, "Invalid username - must be at least 5 characters", HttpStatus.BAD_REQUEST),
	INVALID_PASSWORD(1006, "Invalid password - must be at least 8 characters", HttpStatus.BAD_REQUEST),
	UNAUTHORIZED(1007, "You do not have permission", HttpStatus.FORBIDDEN),
	UNAUTHENTICATED(1008, "You are not authenticated", HttpStatus.UNAUTHORIZED),
	TOUR_NOT_FOUND(1009, "Tour not found", HttpStatus.NOT_FOUND),
	PROMOTION_EXISTED(1010, "Promotion code already exists", HttpStatus.BAD_REQUEST),
	PROMOTION_NOT_FOUND(1011, "Promotion not found", HttpStatus.NOT_FOUND),
	END_DATE_AFTER_START_DATE(1012, "End date must be after start date", HttpStatus.BAD_REQUEST),
	STAFF_ID_REQUIRED(1013, "Staff ID is required", HttpStatus.BAD_REQUEST),
	USERNAME_INVALID(1014, "Username must be at least 3 characters", HttpStatus.BAD_REQUEST),
	IMAGE_NOT_FOUND(1015, "Image not found", HttpStatus.NOT_FOUND),
	PROMOTION_EXPIRED(1016, "Promotion is expired or not yet started", HttpStatus.BAD_REQUEST),
	PROMOTION_OUT_OF_STOCK(1017, "Promotion is out of stock", HttpStatus.BAD_REQUEST),
	PROMOTION_INACTIVE(1018, "Promotion is inactive", HttpStatus.BAD_REQUEST),
	TOUR_UNAVAILABLE(1019, "Tour is not available", HttpStatus.BAD_REQUEST),
	PROMOTION_UNAVAILABLE(1020, "Promotion is unavailable", HttpStatus.BAD_REQUEST),
	CONCURRENT_UPDATE(1021, "Data was modified by another user. Please try again.", HttpStatus.CONFLICT);

	private final int code;
	private final String message;
	private final HttpStatusCode statusCode;
}
