package com.ravensoft.backend.exceptions;


import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ErrorResponse> handleValidationExceptions(
    MethodArgumentNotValidException ex, WebRequest request) {

    Map<String, String> errors = new HashMap<>();
    ex.getBindingResult().getAllErrors().forEach((error) -> {
      String fieldName = ((FieldError) error).getField();
      String errorMessage = error.getDefaultMessage();
      errors.put(fieldName, errorMessage);
    });

    ErrorResponse errorResponse = new ErrorResponse(
      HttpStatus.BAD_REQUEST.value(),
      "Validation failed",
      errors,
      request.getDescription(false)
    );

    return ResponseEntity.badRequest().body(errorResponse);
  }

  @ExceptionHandler(ConstraintViolationException.class)
  public ResponseEntity<ErrorResponse> handleConstraintViolationException(
    ConstraintViolationException ex, WebRequest request) {

    Map<String, String> errors = new HashMap<>();
    for (ConstraintViolation<?> violation : ex.getConstraintViolations()) {
      String fieldName = violation.getPropertyPath().toString();
      String errorMessage = violation.getMessage();
      errors.put(fieldName, errorMessage);
    }

    ErrorResponse errorResponse = new ErrorResponse(
      HttpStatus.BAD_REQUEST.value(),
      "Constraint violation",
      errors,
      request.getDescription(false)
    );

    return ResponseEntity.badRequest().body(errorResponse);
  }

  @ExceptionHandler(PlaceNotFoundException.class)
  public ResponseEntity<ErrorResponse> handlePlaceNotFoundException(
    PlaceNotFoundException ex, WebRequest request) {

    ErrorResponse errorResponse = new ErrorResponse(
      HttpStatus.NOT_FOUND.value(),
      ex.getMessage(),
      null,
      request.getDescription(false)
    );

    return ResponseEntity.notFound().build();
  }

  @ExceptionHandler(CategoryNotFoundException.class)
  public ResponseEntity<ErrorResponse> handleCategoryNotFoundException(
    CategoryNotFoundException ex, WebRequest request) {

    ErrorResponse errorResponse = new ErrorResponse(
      HttpStatus.NOT_FOUND.value(),
      ex.getMessage(),
      null,
      request.getDescription(false)
    );

    return ResponseEntity.notFound().build();
  }

  @ExceptionHandler(DuplicateGoogleIdException.class)
  public ResponseEntity<ErrorResponse> handleDuplicateGoogleIdException(
    DuplicateGoogleIdException ex, WebRequest request) {

    ErrorResponse errorResponse = new ErrorResponse(
      HttpStatus.CONFLICT.value(),
      ex.getMessage(),
      null,
      request.getDescription(false)
    );

    return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ErrorResponse> handleGlobalException(
    Exception ex, WebRequest request) {

    ErrorResponse errorResponse = new ErrorResponse(
      HttpStatus.INTERNAL_SERVER_ERROR.value(),
      "Internal server error occurred",
      null,
      request.getDescription(false)
    );

    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
  }


  @Setter
  @Getter
  public static class ErrorResponse {
    private int status;
    private String message;
    private Map<String, String> errors;
    private String path;
    private LocalDateTime timestamp;

    public ErrorResponse(int status, String message, Map<String, String> errors, String path) {
      this.status = status;
      this.message = message;
      this.errors = errors;
      this.path = path;
      this.timestamp = LocalDateTime.now();
    }

  }
}


