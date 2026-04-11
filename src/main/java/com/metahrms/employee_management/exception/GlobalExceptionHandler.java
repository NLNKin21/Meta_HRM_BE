package com.metahrms.employee_management.exception;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import com.metahrms.employee_management.dto.response.ApiResponse;

import lombok.extern.slf4j.Slf4j;

/**
 * Global Exception Handler for Employee Management System
 * Handles all exceptions and returns consistent API responses
 * 
 * @author MetaHRMS Team
 * @version 1.0
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    /**
     * Handle validation errors from @Valid annotation (400 BAD REQUEST)
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Map<String, String>>> handleValidationErrors(
            MethodArgumentNotValidException ex,
            WebRequest request) {
        
        Map<String, String> errors = new HashMap<>();
        
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        
        log.warn("[VALIDATION] Validation failed: {} | Path: {}", 
                 errors, 
                 request.getDescription(false));
        
        ApiResponse<Map<String, String>> response = ApiResponse.<Map<String, String>>builder()
            .code(400)
            .status("error")
            .message("Validation failed")
            .data(errors)
            .build();
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }
    
    /**
     * Handle ResourceNotFoundException (404 NOT FOUND)
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiResponse<Void>> handleResourceNotFound(
            ResourceNotFoundException ex,
            WebRequest request) {
        
        log.warn("[NOT-FOUND] {} | Path: {}", 
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
     * Handle BusinessException (400 BAD REQUEST)
     */
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiResponse<Void>> handleBusinessException(
            BusinessException ex,
            WebRequest request) {
        
        log.warn("[BUSINESS] {} | Path: {}", 
                 ex.getMessage(), 
                 request.getDescription(false));
        
        ApiResponse<Void> response = ApiResponse.<Void>builder()
            .code(400)
            .status("error")
            .message(ex.getMessage())
            .build();
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }
    
    /**
     * Handle FaceRecognitionException (400 BAD REQUEST)
     */
    @ExceptionHandler(FaceRecognitionException.class)
    public ResponseEntity<ApiResponse<Void>> handleFaceRecognitionException(
            FaceRecognitionException ex,
            WebRequest request) {
        
        log.error("[FACE-RECOGNITION] {} | Path: {}", 
                  ex.getMessage(), 
                  request.getDescription(false), 
                  ex);
        
        ApiResponse<Void> response = ApiResponse.<Void>builder()
            .code(400)
            .status("error")
            .message(ex.getMessage())
            .build();
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }
    
    /**
     * Handle FaceValidationException (400 BAD REQUEST)
     */
    @ExceptionHandler(FaceValidationException.class)
    public ResponseEntity<ApiResponse<Void>> handleFaceValidationException(
            FaceValidationException ex,
            WebRequest request) {
        
        log.warn("[FACE-VALIDATION] {}: {} | Path: {}", 
                 ex.getValidationType(), 
                 ex.getMessage(), 
                 request.getDescription(false));
        
        ApiResponse<Void> response = ApiResponse.<Void>builder()
            .code(400)
            .status("error")
            .message(ex.getMessage())
            .build();
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }
    
    /**
     * Handle FaceServiceUnavailableException (503 SERVICE UNAVAILABLE)
     */
    @ExceptionHandler(FaceServiceUnavailableException.class)
    public ResponseEntity<ApiResponse<Void>> handleFaceServiceUnavailable(
            FaceServiceUnavailableException ex,
            WebRequest request) {
        
        log.error("[SERVICE-UNAVAILABLE] {} | Path: {}", 
                  ex.getMessage(), 
                  request.getDescription(false), 
                  ex);
        
        ApiResponse<Void> response = ApiResponse.<Void>builder()
            .code(503)
            .status("error")
            .message(ex.getMessage())
            .build();
        
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(response);
    }
    
    /**
     * Handle IllegalArgumentException (400 BAD REQUEST)
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponse<Void>> handleIllegalArgument(
            IllegalArgumentException ex,
            WebRequest request) {
        
        log.warn("[INVALID-ARGUMENT] {} | Path: {}", 
                 ex.getMessage(), 
                 request.getDescription(false));
        
        ApiResponse<Void> response = ApiResponse.<Void>builder()
            .code(400)
            .status("error")
            .message(ex.getMessage())
            .build();
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    /**
     * Handle IllegalStateException (400 BAD REQUEST)
     */
    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ApiResponse<Void>> handleIllegalState(
            IllegalStateException ex,
            WebRequest request) {
        
        log.warn("[ILLEGAL-STATE] {} | Path: {}", 
                 ex.getMessage(), 
                 request.getDescription(false));
        
        ApiResponse<Void> response = ApiResponse.<Void>builder()
            .code(400)
            .status("error")
            .message(ex.getMessage())
            .build();
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }
    
    /**
     * Handle MaxUploadSizeExceededException (413 PAYLOAD TOO LARGE)
     */
    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<ApiResponse<Void>> handleMaxSizeException(
            MaxUploadSizeExceededException ex,
            WebRequest request) {
        
        log.warn("[FILE-SIZE] Upload size exceeded | Path: {}", 
                 request.getDescription(false));
        
        ApiResponse<Void> response = ApiResponse.<Void>builder()
            .code(413)
            .status("error")
            .message("Maximum upload size exceeded")
            .build();
        
        return ResponseEntity.status(HttpStatus.PAYLOAD_TOO_LARGE).body(response);
    }

    /**
     * Handle IOException (500 INTERNAL SERVER ERROR)
     */
    @ExceptionHandler(java.io.IOException.class)
    public ResponseEntity<ApiResponse<Void>> handleIOException(
            java.io.IOException ex,
            WebRequest request) {
        
        log.error("[IO-ERROR] {} | Path: {}", 
                  ex.getMessage(), 
                  request.getDescription(false), 
                  ex);
        
        ApiResponse<Void> response = ApiResponse.<Void>builder()
            .code(500)
            .status("error")
            .message("File operation failed: " + ex.getMessage())
            .build();
        
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
    
    /**
     * Handle all other exceptions (500 INTERNAL SERVER ERROR)
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleGenericException(
            Exception ex,
            WebRequest request) {
        
        log.error("[INTERNAL-ERROR] Unexpected error occurred | Path: {} | Error: {}", 
                  request.getDescription(false), 
                  ex.getMessage(), 
                  ex);
        
        ApiResponse<Void> response = ApiResponse.<Void>builder()
            .code(500)
            .status("error")
            .message("An unexpected error occurred. Please try again later.")
            .build();
        
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
}