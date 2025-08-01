package com.sprout.api.common.exception;

import lombok.Getter;

@Getter
public class BusinessException extends RuntimeException {

	private final int statusCode;
	private final String exceptionMessage;

	public BusinessException(int statusCode, String exceptionMessage) {
		super(exceptionMessage);
		this.statusCode = statusCode;
		this.exceptionMessage = exceptionMessage;
	}
}