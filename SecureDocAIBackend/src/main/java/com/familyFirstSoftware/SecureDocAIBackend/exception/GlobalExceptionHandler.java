package com.familyFirstSoftware.SecureDocAIBackend.exception;

import com.familyFirstSoftware.SecureDocAIBackend.dto.response.HttpResponse;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ApiException.class)
    public ResponseEntity<HttpResponse> handleApiException(ApiException ex, WebRequest request) {
        log.error("API Exception: {}", ex.getMessage(), ex);
        return new ResponseEntity<>(
                HttpResponse.error(ex.getMessage(), null),
                ex.getStatus()
        );
    }

        @ExceptionHandler(DocumentOperationException.class)
    public ResponseEntity<HttpResponse> handleDocumentOperationException(DocumentOperationException ex, WebRequest request) {
        log.error("Document Operation Failed: {}", ex.getMessage(), ex);
        return new ResponseEntity<>(
                HttpResponse.error(ex.getMessage(), null),
                ex.getStatus()
        );
    }

        @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<HttpResponse> handleMaxSizeException(MaxUploadSizeExceededException ex, WebRequest request) {
        String message = "File too large. Maximum size allowed is " + ex.getMaxUploadSize() + " bytes";
        log.warn("File upload size exceeded: {}", message);
        return new ResponseEntity<>(
                HttpResponse.error(message, null),
                HttpStatus.PAYLOAD_TOO_LARGE
        );
    }

        @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<HttpResponse> handleValidationExceptions(MethodArgumentNotValidException ex, WebRequest request) {
        Map<String, String> errors = ex.getBindingResult()
            .getFieldErrors()
            .stream()
            .collect(Collectors.toMap(
                fieldError -> fieldError.getField(),
                fieldError -> fieldError.getDefaultMessage()
            ));
        
        log.warn("Validation failed: {}", errors);
        return new ResponseEntity<>(
                HttpResponse.error("Validation failed", errors),
                HttpStatus.BAD_REQUEST
        );
    }

        @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<HttpResponse> handleConstraintViolation(ConstraintViolationException ex, WebRequest request) {
        Map<String, String> errors = new HashMap<>();
        ex.getConstraintViolations().forEach(violation -> {
            String field = violation.getPropertyPath().toString();
            errors.put(field, violation.getMessage());
        });
        
        log.warn("Constraint violation: {}", errors);
        return new ResponseEntity<>(
                HttpResponse.error("Constraint violation", errors),
                HttpStatus.BAD_REQUEST
        );
    }

        @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<HttpResponse> handleAccessDenied(AccessDeniedException ex, WebRequest request) {
        log.warn("Access denied: {}", ex.getMessage());
        return new ResponseEntity<>(
                HttpResponse.error("Access Denied", null),
                HttpStatus.FORBIDDEN
        );
    }

        @ExceptionHandler(Exception.class)
    public ResponseEntity<HttpResponse> handleAllUncaughtException(Exception ex, WebRequest request) {
        log.error("Unexpected error occurred: {}", ex.getMessage(), ex);
        return new ResponseEntity<>(
                HttpResponse.error("An unexpected error occurred", null),
                HttpStatus.INTERNAL_SERVER_ERROR
        );
    }
}
