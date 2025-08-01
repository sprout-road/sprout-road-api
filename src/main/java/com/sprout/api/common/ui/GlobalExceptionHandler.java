package com.sprout.api.common.ui;

import com.sprout.api.common.exception.BusinessException;
import com.sprout.api.common.exception.DetailedExceptionResponse;
import com.sprout.api.common.exception.ErrorSpot;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

	// 이유를 알 수 없는 에러
	@ExceptionHandler
	public ResponseEntity<String> handleException(Exception exception) {
		log.error("{} : {}", exception.getClass(), exception.toString());
		return buildExceptionResponse(500, "알 수 없는 서버 에러가 발생");
	}

	// 존재 하지 않는 End-Point로 접근 시 발생하는 에러
	@ExceptionHandler
	public ResponseEntity<String> handleNoResourceFoundException(NoResourceFoundException exception) {
		log.warn("{} : {}", exception.getClass(), exception.getMessage());
		return buildExceptionResponse(404, "존재하지 않는 경로");
	}

	// BeanValidation(jakarta.validation.constraints) 유효성 검증 에러 처리
	@ExceptionHandler
	public ResponseEntity<DetailedExceptionResponse> handleMethodArgumentNotValidException(
		MethodArgumentNotValidException exception
	) {
		List<ErrorSpot> errorSpots = extractErrorSpots(exception);
		String exceptionMessage = extractExceptionMessage(exception);
		log.warn("[{}] : {}", exception.getClass(), errorSpots);

		return ResponseEntity.status(400)
			.body(DetailedExceptionResponse.fail(exceptionMessage, errorSpots));
	}

	// RequestParam, PathVariable Type Mismatch 에러 처리
	@ExceptionHandler
	public ResponseEntity<DetailedExceptionResponse> handleMethodArgumentTypeMismatchException(
		MethodArgumentTypeMismatchException exception
	) {
		final String type = exception.getRequiredType().getSimpleName();
		final String customMessage = " (으)로 변환할 수 없는 요청입니다.";
		ErrorSpot errorSpot = new ErrorSpot(exception.getName(), type + customMessage);
		log.warn("{} : {}", exception.getClass(), errorSpot);
		return ResponseEntity.status(400)
			.body(DetailedExceptionResponse.fail("경로 변수 또는 쿼리 파라미터의 타입이 잘못됨", errorSpot));
	}

	// RequestParam 이 누락된 경우 에러 처리
	@ExceptionHandler
	public ResponseEntity<DetailedExceptionResponse> handleMissingServletRequestParameterException(
		MissingServletRequestParameterException exception
	) {
		ErrorSpot errorSpot = new ErrorSpot(exception.getParameterName(), exception.getParameterType());
		log.warn("{} : {}", exception.getClass(), errorSpot);
		return ResponseEntity.status(404)
			.body(DetailedExceptionResponse.fail("필수 쿼리 파라미터가 누락", errorSpot));
	}

	// 잘못된 Dto 정보에 대해 에러 처리
	@ExceptionHandler
	public ResponseEntity<String> handleHttpMessageNotReadableException(
		HttpMessageNotReadableException exception
	) {
		log.warn("{} : {}", exception.getClass(), exception.getMessage());
		return buildExceptionResponse(400, "json 형식이 서버에서 요구하는 형식이 아님.");
	}

	// contentType 이 잘못된 경우 발생하는 예외
	@ExceptionHandler
	public ResponseEntity<String> handleHttpMediaTypeNotSupportedException(
		HttpMediaTypeNotSupportedException exception
	) {
		log.warn("[{}] : {}", exception.getClass(), exception.getMessage());
		return buildExceptionResponse(400, "지원되지 않는 content-type");
	}

	@ExceptionHandler
	public ResponseEntity<String> handleBusinessException(BusinessException exception) {
		return buildExceptionResponse(exception.getStatusCode(), exception.getMessage());
	}

	private ResponseEntity<String> buildExceptionResponse(int statusCode, String message) {
		return ResponseEntity.status(statusCode)
			.body(message);
	}

	private List<ErrorSpot> extractErrorSpots(MethodArgumentNotValidException exception) {
		return exception.getBindingResult()
			.getFieldErrors()
			.stream()
			.map(fieldError -> new ErrorSpot(fieldError.getField(), fieldError.getDefaultMessage()))
			.toList();
	}

	private String extractExceptionMessage(MethodArgumentNotValidException exception) {
		boolean hasTypeMismatch = exception.getBindingResult()
			.getFieldErrors()
			.stream()
			.anyMatch(FieldError::isBindingFailure);

		if (hasTypeMismatch) {
			return "경로 변수 또는 쿼리 파라미터의 타입이 잘못됨";
		}
		return "응답 데이터의 유효성 검증이 실패";
	}
}