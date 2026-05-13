package com.stayeasy.stayeasyspringangular.exception;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MissingServletRequestParameterException;

@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(ResourceNotFoundException.class)
  public ResponseEntity<ApiErrorResponse> handleNotFound(
    ResourceNotFoundException ex,
    HttpServletRequest request
  ) {
    return buildResponse(HttpStatus.NOT_FOUND, ex.getMessage(), request.getRequestURI(), null);
  }

  @ExceptionHandler(BadRequestException.class)
  public ResponseEntity<ApiErrorResponse> handleBadRequest(
    BadRequestException ex,
    HttpServletRequest request
  ) {
    return buildResponse(HttpStatus.BAD_REQUEST, ex.getMessage(), request.getRequestURI(), null);
  }

  @ExceptionHandler(ForbiddenActionException.class)
  public ResponseEntity<ApiErrorResponse> handleForbidden(
    ForbiddenActionException ex,
    HttpServletRequest request
  ) {
    return buildResponse(HttpStatus.FORBIDDEN, ex.getMessage(), request.getRequestURI(), null);
  }

  @ExceptionHandler(UnauthorizedActionException.class)
  public ResponseEntity<ApiErrorResponse> handleUnauthorized(
    UnauthorizedActionException ex,
    HttpServletRequest request
  ) {
    return buildResponse(HttpStatus.UNAUTHORIZED, ex.getMessage(), request.getRequestURI(), null);
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ApiErrorResponse> handleValidation(
    MethodArgumentNotValidException ex,
    HttpServletRequest request
  ) {
    Map<String, String> errors = new LinkedHashMap<>();

    ex.getBindingResult().getFieldErrors().forEach(error ->
      errors.put(error.getField(), error.getDefaultMessage())
    );

    ex.getBindingResult().getGlobalErrors().forEach(error ->
      errors.put(error.getObjectName(), error.getDefaultMessage())
    );

    return buildResponse(
      HttpStatus.BAD_REQUEST,
      "Validation failed",
      request.getRequestURI(),
      errors
    );
  }

  @ExceptionHandler(ConstraintViolationException.class)
  public ResponseEntity<ApiErrorResponse> handleConstraintViolation(
    ConstraintViolationException ex,
    HttpServletRequest request
  ) {
    Map<String, String> errors = new LinkedHashMap<>();

    ex.getConstraintViolations().forEach(violation ->
      errors.put(violation.getPropertyPath().toString(), violation.getMessage())
    );

    return buildResponse(
      HttpStatus.BAD_REQUEST,
      "Validation failed",
      request.getRequestURI(),
      errors
    );
  }

  @ExceptionHandler(MethodArgumentTypeMismatchException.class)
  public ResponseEntity<ApiErrorResponse> handleTypeMismatch(
    MethodArgumentTypeMismatchException ex,
    HttpServletRequest request
  ) {
    String message = "Invalid value for parameter: " + ex.getName();
    return buildResponse(HttpStatus.BAD_REQUEST, message, request.getRequestURI(), null);
  }

  @ExceptionHandler({BadCredentialsException.class, UsernameNotFoundException.class})
  public ResponseEntity<ApiErrorResponse> handleBadCredentials(
    Exception ex,
    HttpServletRequest request
  ) {
    return buildResponse(
      HttpStatus.UNAUTHORIZED,
      "Invalid username or password",
      request.getRequestURI(),
      null
    );
  }

  @ExceptionHandler(DataIntegrityViolationException.class)
  public ResponseEntity<ApiErrorResponse> handleDataIntegrity(
    DataIntegrityViolationException ex,
    HttpServletRequest request
  ) {
    return buildResponse(
      HttpStatus.CONFLICT,
      "Database constraint violation",
      request.getRequestURI(),
      null
    );
  }

  @ExceptionHandler(ResponseStatusException.class)
  public ResponseEntity<ApiErrorResponse> handleResponseStatus(
    ResponseStatusException ex,
    HttpServletRequest request
  ) {
    HttpStatusCode statusCode = ex.getStatusCode();
    HttpStatus status = HttpStatus.resolve(statusCode.value());

    String error = status != null ? status.getReasonPhrase() : "Error";
    String message = ex.getReason() != null ? ex.getReason() : ex.getMessage();

    ApiErrorResponse body = new ApiErrorResponse(
      LocalDateTime.now(),
      statusCode.value(),
      error,
      message,
      request.getRequestURI(),
      null
    );

    return ResponseEntity.status(statusCode).body(body);
  }

  @ExceptionHandler(HttpMessageNotReadableException.class)
  public ResponseEntity<ApiErrorResponse> handleInvalidJson(
    HttpMessageNotReadableException ex,
    HttpServletRequest request
  ) {
    return buildResponse(
      HttpStatus.BAD_REQUEST,
      "Invalid request body",
      request.getRequestURI(),
      null
    );
  }

  @ExceptionHandler(MissingServletRequestParameterException.class)
  public ResponseEntity<ApiErrorResponse> handleMissingParameter(
    MissingServletRequestParameterException ex,
    HttpServletRequest request
  ) {
    String message = "Missing required parameter: " + ex.getParameterName();

    return buildResponse(
      HttpStatus.BAD_REQUEST,
      message,
      request.getRequestURI(),
      null
    );
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ApiErrorResponse> handleGeneric(
    Exception ex,
    HttpServletRequest request
  ) {
    return buildResponse(
      HttpStatus.INTERNAL_SERVER_ERROR,
      "Unexpected server error",
      request.getRequestURI(),
      null
    );
  }

  private ResponseEntity<ApiErrorResponse> buildResponse(
    HttpStatus status,
    String message,
    String path,
    Map<String, String> validationErrors
  ) {
    ApiErrorResponse body = new ApiErrorResponse(
      LocalDateTime.now(),
      status.value(),
      status.getReasonPhrase(),
      message,
      path,
      validationErrors
    );

    return ResponseEntity.status(status).body(body);
  }
}
