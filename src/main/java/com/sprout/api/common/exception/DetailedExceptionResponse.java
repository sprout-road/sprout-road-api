package com.sprout.api.common.exception;

import java.util.List;

public record DetailedExceptionResponse(List<ErrorSpot> errors, String exceptionMessage) {

	public static DetailedExceptionResponse fail(String exceptionMessage, List<ErrorSpot> errors) {
		return new DetailedExceptionResponse(errors, exceptionMessage);
	}

	public static DetailedExceptionResponse fail(String exceptionMessage, ErrorSpot error) {
		return new DetailedExceptionResponse(List.of(error), exceptionMessage);
	}
}