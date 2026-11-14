package com.example.hostel_managment.demo.api;

import jakarta.validation.ConstraintViolationException;
import java.util.LinkedHashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

@RestControllerAdvice
public class ApiExceptionHandler {
    private static final Logger logger = LoggerFactory.getLogger(ApiExceptionHandler.class);

    @ExceptionHandler(MethodArgumentNotValidException.class)
    ResponseEntity<Map<String, Object>> validation(MethodArgumentNotValidException exception) {
        Map<String, String> errors = new LinkedHashMap<>();
        exception.getBindingResult().getFieldErrors()
                .forEach(error -> errors.put(error.getField(), error.getDefaultMessage()));
        return response(HttpStatus.BAD_REQUEST, "Request validation failed", errors);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    ResponseEntity<Map<String, Object>> unreadable(HttpMessageNotReadableException exception) {
        logger.warn("Malformed request body", exception);
        String cause = exception.getMostSpecificCause() == null
                ? exception.getMessage()
                : exception.getMostSpecificCause().getMessage();
        return response(HttpStatus.BAD_REQUEST, "Malformed request body: " + cause, null);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    ResponseEntity<Map<String, Object>> constraintViolation(ConstraintViolationException exception) {
        logger.warn("Request constraint violation", exception);
        return response(HttpStatus.BAD_REQUEST, exception.getMessage(), null);
    }

    @ExceptionHandler(ResponseStatusException.class)
    ResponseEntity<Map<String, Object>> status(ResponseStatusException exception) {
        return response(HttpStatus.valueOf(exception.getStatusCode().value()),
                exception.getReason() == null ? exception.getMessage() : exception.getReason(), null);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    ResponseEntity<Map<String, Object>> dataIntegrity(DataIntegrityViolationException exception) {
        logger.error("Database constraint failed", exception);
        return response(HttpStatus.CONFLICT, "The request conflicts with existing data.", null);
    }

    @ExceptionHandler(Exception.class)
    ResponseEntity<Map<String, Object>> unexpected(Exception exception) {
        logger.error("Unhandled API error", exception);
        return response(HttpStatus.INTERNAL_SERVER_ERROR,
                "An unexpected server error occurred.", null);
    }

    private ResponseEntity<Map<String, Object>> response(HttpStatus status, String message,
            Map<String, String> errors) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("status", status.value());
        body.put("error", status.getReasonPhrase());
        body.put("message", message);
        if (errors != null && !errors.isEmpty()) {
            body.put("errors", errors);
        }
        return ResponseEntity.status(status).body(body);
    }

    private String rootMessage(Throwable exception) {
        Throwable current = exception;
        while (current.getCause() != null) {
            current = current.getCause();
        }
        return current.getMessage() == null ? current.getClass().getSimpleName() : current.getMessage();
    }
}
