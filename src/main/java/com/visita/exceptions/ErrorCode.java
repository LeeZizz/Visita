package com.visita.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorCode {

	NOT_DEFINE_IN_ERROR_CODE(9999, "Unchecked error occurred", HttpStatus.INTERNAL_SERVER_ERROR),
	USER_EXISTED(1001, "User already exists", HttpStatus.BAD_REQUEST),
	USER_NOT_FOUND(1002, "User not found", HttpStatus.NOT_FOUND),
	ROLE_NOT_FOUND(1004, "Role not found", HttpStatus.NOT_FOUND),
	INVALID_USERNAME(1005, "Invalid username - must be at least 5 characters", HttpStatus.BAD_REQUEST),
	INVALID_PASSWORD(1006, "Invalid password - must be at least 8 characters", HttpStatus.BAD_REQUEST),
	UNAUTHORIZED(1007, "You do not have permission", HttpStatus.FORBIDDEN),
	UNAUTHENTICATED(1008, "You are not authenticated", HttpStatus.UNAUTHORIZED),;

	private final int code;
	private final String message;
	private final HttpStatusCode statusCode;
}
