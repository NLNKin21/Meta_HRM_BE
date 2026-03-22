package com.metahrms.employee_management.exception;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import com.metahrms.employee_management.dto.response.ApiResponse;

import lombok.extern.slf4j.Slf4j;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    /**
     * Handle ResourceNotFoundException (404)
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiResponse<Void>> handleResourceNotFound(
            ResourceNotFoundException ex, 
            WebRequest request) {
        
        log.error("Resource not found: {} | Path: {}", 
                  ex.getMessage(), 
                  request.getDescription(false));
        
        ApiResponse<Void> response = ApiResponse.<Void>builder()
            .code(404)
            .status("error")
            .message(ex.getMessage())
            .build();
        
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    /**
     * Handle IllegalStateException (400)
     */
    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ApiResponse<Void>> handleIllegalState(
            IllegalStateException ex,
            WebRequest request) {
        
        log.error("Illegal state: {} | Path: {}", 
                  ex.getMessage(), 
                  request.getDescription(false));
        
        ApiResponse<Void> response = ApiResponse.<Void>builder()
            .code(400)
            .status("error")
            .message(ex.getMessage())
            .build();
        
        return ResponseEntity.badRequest().body(response);
    }

    /**
     * Handle IllegalArgumentException (400)
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponse<Void>> handleIllegalArgument(
            IllegalArgumentException ex,
            WebRequest request) {
        
        log.error("Illegal argument: {} | Path: {}", 
                  ex.getMessage(), 
                  request.getDescription(false));
        
        ApiResponse<Void> response = ApiResponse.<Void>builder()
            .code(400)
            .status("error")
            .message(ex.getMessage())
            .build();
        
        return ResponseEntity.badRequest().body(response);
    }

    /**
     * Handle Validation Errors (400)
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Map<String, String>>> handleValidationErrors(
            MethodArgumentNotValidException ex) {
        
        log.error("Validation failed: {}", ex.getMessage());
        
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        ApiResponse<Map<String, String>> response = ApiResponse.<Map<String, String>>builder()
            .code(400)
            .status("error")
            .message("Validation failed")
            .data(errors)
            .build();
        
        return ResponseEntity.badRequest().body(response);
    }

    /**
     * Handle IOException (from file upload/delete) - 500
     */
    @ExceptionHandler(java.io.IOException.class)
    public ResponseEntity<ApiResponse<Void>> handleIOException(
            java.io.IOException ex,
            WebRequest request) {
        
        log.error("IO Exception: {} | Path: {}", 
                  ex.getMessage(), 
                  request.getDescription(false));
        
        ApiResponse<Void> response = ApiResponse.<Void>builder()
            .code(500)
            .status("error")
            .message("File operation failed: " + ex.getMessage())
            .build();
        
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }

    /**
     * Handle all other exceptions (500)
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleGeneralException(
            Exception ex,
            WebRequest request) {
        
        log.error("Unexpected error: {} | Path: {} | Stack trace:", 
                  ex.getMessage(), 
                  request.getDescription(false), 
                  ex);
        
        ApiResponse<Void> response = ApiResponse.<Void>builder()
            .code(500)
            .status("error")
            .message("An unexpected error occurred. Please contact support.")
            .build();
        
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
}