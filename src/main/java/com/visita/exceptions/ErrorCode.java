package com.visita.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
/**
 * Error codes for API responses.
 * Code ranges:
 * - 1000: Success
 * - 1001-1999: Client/Business errors
 * - 9999: Unknown/System errors
 */
public enum ErrorCode {

	UNKNOWN_ERROR(9999, "Unchecked error occurred", HttpStatus.INTERNAL_SERVER_ERROR),
	USER_EXISTED(1001, "User already exists", HttpStatus.BAD_REQUEST),
	USER_NOT_FOUND(1002, "User not found", HttpStatus.NOT_FOUND),
	ROLE_NOT_FOUND(1003, "Role not found", HttpStatus.NOT_FOUND),
	INVALID_USERNAME(1004, "Invalid username - must be at least 5 characters", HttpStatus.BAD_REQUEST),
	INVALID_PASSWORD(1005, "Invalid password - must be at least 8 characters", HttpStatus.BAD_REQUEST),
	UNAUTHORIZED(1006, "You do not have permission", HttpStatus.FORBIDDEN),
	UNAUTHENTICATED(1007, "You are not authenticated", HttpStatus.UNAUTHORIZED),
	TOUR_NOT_FOUND(1008, "Tour not found", HttpStatus.NOT_FOUND),
	PROMOTION_EXISTED(1009, "Promotion code already exists", HttpStatus.BAD_REQUEST),
	PROMOTION_NOT_FOUND(1010, "Promotion not found", HttpStatus.NOT_FOUND),
	END_DATE_AFTER_START_DATE(1011, "End date must be after start date", HttpStatus.BAD_REQUEST),
	STAFF_ID_REQUIRED(1012, "Staff ID is required", HttpStatus.BAD_REQUEST),
	USERNAME_INVALID(1013, "Username must be at least 3 characters", HttpStatus.BAD_REQUEST),
	IMAGE_NOT_FOUND(1014, "Image not found", HttpStatus.NOT_FOUND),
	PROMOTION_EXPIRED(1015, "Promotion is expired or not yet started", HttpStatus.BAD_REQUEST),
	PROMOTION_OUT_OF_STOCK(1016, "Promotion is out of stock", HttpStatus.BAD_REQUEST),
	PROMOTION_INACTIVE(1017, "Promotion is inactive", HttpStatus.BAD_REQUEST),
	TOUR_UNAVAILABLE(1018, "Tour is not available", HttpStatus.BAD_REQUEST),
	PROMOTION_UNAVAILABLE(1019, "Promotion is unavailable", HttpStatus.BAD_REQUEST),
	CONCURRENT_UPDATE(1020, "Data was modified by another user. Please try again.", HttpStatus.CONFLICT),
	BOOKING_NOT_FOUND(1021, "Booking not found", HttpStatus.NOT_FOUND),
	PAYMENT_REQUIRED(1022, "Payment is required before completing booking", HttpStatus.BAD_REQUEST),
	BOOKING_CONFLICT(1023, "Cannot book tours within 1 week of another tour", HttpStatus.CONFLICT),
	INVALID_BOOKING_STATUS(1024, "Không thể hủy đặt tour, hãy liên hệ với nhân viên để được giải quyết",
			HttpStatus.BAD_REQUEST);

	private final int code;
	private final String message;
	private final HttpStatusCode statusCode;
}
