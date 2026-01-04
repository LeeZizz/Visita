package com.visita.exceptions;

import org.springframework.http.ResponseEntity;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.visita.dto.response.ApiResponse;

import jakarta.persistence.OptimisticLockException;
import lombok.extern.slf4j.Slf4j;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

	@ExceptionHandler(value = { OptimisticLockException.class, ObjectOptimisticLockingFailureException.class })
	ResponseEntity<ApiResponse<?>> handleOptimisticLockException(Exception exception) {
		log.warn("Optimistic lock exception: {}", exception.getMessage());
		ErrorCode errorCode = ErrorCode.CONCURRENT_UPDATE;
		ApiResponse<?> apiResponse = new ApiResponse<>();
		apiResponse.setCode(errorCode.getCode());
		apiResponse.setMessage(errorCode.getMessage());
		return ResponseEntity.status(errorCode.getStatusCode()).body(apiResponse);
	}

	@ExceptionHandler(value = Exception.class)
	ResponseEntity<ApiResponse<?>> handleRuntimeException(Exception exception) {
		ApiResponse<?> apiResponse = new ApiResponse<>();
		log.error("Unhandled exception: ", exception);
		apiResponse.setCode(ErrorCode.UNKNOWN_ERROR.getCode());
		apiResponse.setMessage(ErrorCode.UNKNOWN_ERROR.getMessage() + ": " + exception.getMessage());
		return ResponseEntity.badRequest().body(apiResponse);
	}

	@ExceptionHandler(value = WebException.class)
	ResponseEntity<ApiResponse<?>> handleWebException(WebException webException) {
		ErrorCode errorCode = webException.getErrorCode();
		ApiResponse<?> apiResponse = new ApiResponse<>();
		apiResponse.setCode(errorCode.getCode());
		apiResponse.setMessage(errorCode.getMessage());
		return ResponseEntity.status(errorCode.getStatusCode()).body(apiResponse);
	}

	@ExceptionHandler(value = AccessDeniedException.class)
	ResponseEntity<ApiResponse<?>> handleAccessDeniedException(AccessDeniedException accessDeniedException) {
		log.error("Access denied exception: ", accessDeniedException);
		ApiResponse<?> apiResponse = new ApiResponse<>();
		ErrorCode errorCode = ErrorCode.UNAUTHORIZED;
		apiResponse.setCode(errorCode.getCode());
		apiResponse.setMessage(errorCode.getMessage());
		return ResponseEntity.status(errorCode.getStatusCode()).body(apiResponse);
	}

	@ExceptionHandler(value = MethodArgumentNotValidException.class)
	ResponseEntity<ApiResponse<?>> handleMethodArgumentNotValidException(
			MethodArgumentNotValidException methodArgumentNotValidException) {
		String enumKey = "";
		if (methodArgumentNotValidException.getFieldError() != null) {
			enumKey = methodArgumentNotValidException.getFieldError().getDefaultMessage();
		}
		ErrorCode errorCode = ErrorCode.UNKNOWN_ERROR;
		if (enumKey != null && !enumKey.isEmpty()) {
			try {
				errorCode = ErrorCode.valueOf(enumKey);
			} catch (IllegalArgumentException e) {
				log.error("Invalid error code key: {}", enumKey);
			}
		}
		ApiResponse<?> apiResponse = new ApiResponse<>();
		apiResponse.setCode(errorCode.getCode());
		apiResponse.setMessage(errorCode.getMessage());
		return ResponseEntity.badRequest().body(apiResponse);
	}
}
