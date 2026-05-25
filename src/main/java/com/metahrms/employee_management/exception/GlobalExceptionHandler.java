package com.metahrms.employee_management.exception;

import java.util.HashMap;
import java.util.Map;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import com.metahrms.employee_management.dto.response.ApiResponse;

import lombok.extern.slf4j.Slf4j;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    // ✅ Helper: tạo headers JSON để override Content-Type bất kể request gốc là gì
    private HttpHeaders jsonHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return headers;
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Map<String, String>>> handleValidationErrors(
            MethodArgumentNotValidException ex,
            WebRequest request) {

        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            errors.put(fieldName, error.getDefaultMessage());
        });

        log.warn("[VALIDATION] Validation failed: {} | Path: {}",
                errors, request.getDescription(false));

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .headers(jsonHeaders())           // ✅ Force JSON
                .body(ApiResponse.<Map<String, String>>builder()
                        .code(400)
                        .status("error")
                        .message("Validation failed")
                        .data(errors)
                        .build());
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiResponse<Void>> handleResourceNotFound(
            ResourceNotFoundException ex,
            WebRequest request) {

        log.warn("[NOT-FOUND] {} | Path: {}", ex.getMessage(), request.getDescription(false));

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .headers(jsonHeaders())
                .body(ApiResponse.<Void>builder()
                        .code(404).status("error").message(ex.getMessage()).build());
    }

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiResponse<Void>> handleBusinessException(
            BusinessException ex,
            WebRequest request) {

        log.warn("[BUSINESS] {} | Path: {}", ex.getMessage(), request.getDescription(false));

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .headers(jsonHeaders())
                .body(ApiResponse.<Void>builder()
                        .code(400).status("error").message(ex.getMessage()).build());
    }

    @ExceptionHandler(FaceRecognitionException.class)
    public ResponseEntity<ApiResponse<Void>> handleFaceRecognitionException(
            FaceRecognitionException ex,
            WebRequest request) {

        log.error("[FACE-RECOGNITION] {} | Path: {}", ex.getMessage(), request.getDescription(false), ex);

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .headers(jsonHeaders())
                .body(ApiResponse.<Void>builder()
                        .code(400).status("error").message(ex.getMessage()).build());
    }

    @ExceptionHandler(FaceValidationException.class)
    public ResponseEntity<ApiResponse<Void>> handleFaceValidationException(
            FaceValidationException ex,
            WebRequest request) {

        log.warn("[FACE-VALIDATION] {}: {} | Path: {}",
                ex.getValidationType(), ex.getMessage(), request.getDescription(false));

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .headers(jsonHeaders())
                .body(ApiResponse.<Void>builder()
                        .code(400).status("error").message(ex.getMessage()).build());
    }

    @ExceptionHandler(FaceServiceUnavailableException.class)
    public ResponseEntity<ApiResponse<Void>> handleFaceServiceUnavailable(
            FaceServiceUnavailableException ex,
            WebRequest request) {

        log.error("[SERVICE-UNAVAILABLE] {} | Path: {}", ex.getMessage(), request.getDescription(false), ex);

        return ResponseEntity
                .status(HttpStatus.SERVICE_UNAVAILABLE)
                .headers(jsonHeaders())
                .body(ApiResponse.<Void>builder()
                        .code(503).status("error").message(ex.getMessage()).build());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponse<Void>> handleIllegalArgument(
            IllegalArgumentException ex,
            WebRequest request) {

        log.warn("[INVALID-ARGUMENT] {} | Path: {}", ex.getMessage(), request.getDescription(false));

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .headers(jsonHeaders())
                .body(ApiResponse.<Void>builder()
                        .code(400).status("error").message(ex.getMessage()).build());
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ApiResponse<Void>> handleIllegalState(
            IllegalStateException ex,
            WebRequest request) {

        log.warn("[ILLEGAL-STATE] {} | Path: {}", ex.getMessage(), request.getDescription(false));

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .headers(jsonHeaders())
                .body(ApiResponse.<Void>builder()
                        .code(400).status("error").message(ex.getMessage()).build());
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<ApiResponse<Void>> handleMaxSizeException(
            MaxUploadSizeExceededException ex,
            WebRequest request) {

        log.warn("[FILE-SIZE] Upload size exceeded | Path: {}", request.getDescription(false));

        return ResponseEntity
                .status(HttpStatus.PAYLOAD_TOO_LARGE)
                .headers(jsonHeaders())
                .body(ApiResponse.<Void>builder()
                        .code(413).status("error").message("Maximum upload size exceeded").build());
    }

    @ExceptionHandler(java.io.IOException.class)
    public ResponseEntity<ApiResponse<Void>> handleIOException(
            java.io.IOException ex,
            WebRequest request) {

        log.error("[IO-ERROR] {} | Path: {}", ex.getMessage(), request.getDescription(false), ex);

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .headers(jsonHeaders())
                .body(ApiResponse.<Void>builder()
                        .code(500).status("error")
                        .message("File operation failed: " + ex.getMessage()).build());
    }

    // ✅ Handler quan trọng nhất — phải force JSON
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleGenericException(
            Exception ex,
            WebRequest request) {

        log.error("[INTERNAL-ERROR] Unexpected error occurred | Path: {} | Error: {}",
                request.getDescription(false), ex.getMessage(), ex);

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .headers(jsonHeaders())           // ✅ Force Content-Type = JSON
                .body(ApiResponse.<Void>builder()
                        .code(500).status("error")
                        .message("An unexpected error occurred. Please try again later.")
                        .build());
    }
}