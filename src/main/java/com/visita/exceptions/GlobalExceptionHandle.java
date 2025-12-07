package com.visita.exceptions;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.visita.dto.response.ApiResponse;

import lombok.extern.slf4j.Slf4j;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandle {

	@ExceptionHandler(value = Exception.class)
	ResponseEntity<ApiResponse<?>> handleRuntimeException(Exception Exception) {
		ApiResponse<?> apiResponse = new ApiResponse<>();
		log.error("Unhandled exception: ", Exception);
		apiResponse.setCode(ErrorCode.NOT_DEFINE_IN_ERROR_CODE.getCode());
		apiResponse.setMessage(ErrorCode.NOT_DEFINE_IN_ERROR_CODE.getMessage());
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
		log.error("Access Denied Exception: ", accessDeniedException);
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
		ErrorCode errorCode = ErrorCode.NOT_DEFINE_IN_ERROR_CODE;
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
