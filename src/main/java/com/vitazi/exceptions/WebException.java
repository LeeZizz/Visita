package com.vitazi.exceptions;

public class WebException extends RuntimeException {

	private final ErrorCode errorCode;

	public WebException(ErrorCode errorCode) {
		super(errorCode.getMessage());
		this.errorCode = errorCode;
	}

	public ErrorCode getErrorCode() {
		return errorCode;
	}

	public ErrorCode setErrorCode(ErrorCode errorCode) {
		return this.errorCode;
	}
}
