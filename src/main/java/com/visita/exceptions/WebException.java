package com.visita.exceptions;

import lombok.Getter;

@Getter
public class WebException extends RuntimeException {

	private final ErrorCode errorCode;

	public WebException(ErrorCode errorCode) {
		super(errorCode.getMessage());
		this.errorCode = errorCode;
	}
}
