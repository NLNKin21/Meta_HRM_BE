<<<<<<< HEAD
package com.metahrms.employee_management.exception;

import com.metahrms.employee_management.dto.response.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

//@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiResponse<Void>> handleResourceNotFoundException(
            ResourceNotFoundException ex, HttpServletRequest request) {
        log.error("Resource not found: {}", ex.getMessage());
        ApiResponse<Void> response = ApiResponse.<Void>builder()
                .status("error")
                .message(ex.getMessage())
                .timestamp(LocalDateTime.now())
                .path(request.getRequestURI())
                .build();
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ApiResponse<Void>> handleBadRequestException(
            BadRequestException ex, HttpServletRequest request) {
        log.error("Bad request: {}", ex.getMessage());
        ApiResponse<Void> response = ApiResponse.<Void>builder()
                .status("error")
                .message(ex.getMessage())
                .timestamp(LocalDateTime.now())
                .path(request.getRequestURI())
                .build();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Map<String, String>>> handleValidationExceptions(
            MethodArgumentNotValidException ex, HttpServletRequest request) {
        log.error("Validation error: {}", ex.getMessage());
        
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        ApiResponse<Map<String, String>> response = ApiResponse.<Map<String, String>>builder()
                .status("error")
                .message("Validation failed")
                .data(errors)
                .timestamp(LocalDateTime.now())
                .path(request.getRequestURI())
                .build();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ApiResponse<Void>> handleBadCredentialsException(
            BadCredentialsException ex, HttpServletRequest request) {
        log.error("Authentication failed: {}", ex.getMessage());
        ApiResponse<Void> response = ApiResponse.<Void>builder()
                .status("error")
                .message("Invalid username or password")
                .timestamp(LocalDateTime.now())
                .path(request.getRequestURI())
                .build();
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiResponse<Void>> handleAccessDeniedException(
            AccessDeniedException ex, HttpServletRequest request) {
        log.error("Access denied: {}", ex.getMessage());
        ApiResponse<Void> response = ApiResponse.<Void>builder()
                .status("error")
                .message("Access denied. You don't have permission to access this resource.")
                .timestamp(LocalDateTime.now())
                .path(request.getRequestURI())
                .build();
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleGenericException(
            Exception ex, HttpServletRequest request) {
        log.error("Unexpected error occurred: ", ex);
        ApiResponse<Void> response = ApiResponse.<Void>builder()
                .status("error")
                .message("An unexpected error occurred. Please try again later.")
                .timestamp(LocalDateTime.now())
                .path(request.getRequestURI())
                .build();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
}
=======
// package com.metahrms.employee_management.exception;

// import com.metahrms.employee_management.dto.response.ApiResponse;
// import jakarta.servlet.http.HttpServletRequest;
// import lombok.extern.slf4j.Slf4j;
// import org.springframework.http.HttpStatus;
// import org.springframework.http.ResponseEntity;
// import org.springframework.security.access.AccessDeniedException;
// import org.springframework.security.authentication.BadCredentialsException;
// import org.springframework.validation.FieldError;
// import org.springframework.web.bind.MethodArgumentNotValidException;
// import org.springframework.web.bind.annotation.ExceptionHandler;
// import org.springframework.web.bind.annotation.RestControllerAdvice;

// import java.time.LocalDateTime;
// import java.util.HashMap;
// import java.util.Map;

// @RestControllerAdvice
// @Slf4j
// public class GlobalExceptionHandler {

//     @ExceptionHandler(ResourceNotFoundException.class)
//     public ResponseEntity<ApiResponse<Void>> handleResourceNotFoundException(
//             ResourceNotFoundException ex, HttpServletRequest request) {
//         log.error("Resource not found: {}", ex.getMessage());
//         ApiResponse<Void> response = ApiResponse.<Void>builder()
//                 .status("error")
//                 .message(ex.getMessage())
//                 .timestamp(LocalDateTime.now())
//                 .path(request.getRequestURI())
//                 .build();
//         return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
//     }

//     @ExceptionHandler(BadRequestException.class)
//     public ResponseEntity<ApiResponse<Void>> handleBadRequestException(
//             BadRequestException ex, HttpServletRequest request) {
//         log.error("Bad request: {}", ex.getMessage());
//         ApiResponse<Void> response = ApiResponse.<Void>builder()
//                 .status("error")
//                 .message(ex.getMessage())
//                 .timestamp(LocalDateTime.now())
//                 .path(request.getRequestURI())
//                 .build();
//         return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
//     }

//     @ExceptionHandler(MethodArgumentNotValidException.class)
//     public ResponseEntity<ApiResponse<Map<String, String>>> handleValidationExceptions(
//             MethodArgumentNotValidException ex, HttpServletRequest request) {
//         log.error("Validation error: {}", ex.getMessage());
        
//         Map<String, String> errors = new HashMap<>();
//         ex.getBindingResult().getAllErrors().forEach(error -> {
//             String fieldName = ((FieldError) error).getField();
//             String errorMessage = error.getDefaultMessage();
//             errors.put(fieldName, errorMessage);
//         });

//         ApiResponse<Map<String, String>> response = ApiResponse.<Map<String, String>>builder()
//                 .status("error")
//                 .message("Validation failed")
//                 .data(errors)
//                 .timestamp(LocalDateTime.now())
//                 .path(request.getRequestURI())
//                 .build();
//         return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
//     }

//     @ExceptionHandler(BadCredentialsException.class)
//     public ResponseEntity<ApiResponse<Void>> handleBadCredentialsException(
//             BadCredentialsException ex, HttpServletRequest request) {
//         log.error("Authentication failed: {}", ex.getMessage());
//         ApiResponse<Void> response = ApiResponse.<Void>builder()
//                 .status("error")
//                 .message("Invalid username or password")
//                 .timestamp(LocalDateTime.now())
//                 .path(request.getRequestURI())
//                 .build();
//         return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
//     }

//     @ExceptionHandler(AccessDeniedException.class)
//     public ResponseEntity<ApiResponse<Void>> handleAccessDeniedException(
//             AccessDeniedException ex, HttpServletRequest request) {
//         log.error("Access denied: {}", ex.getMessage());
//         ApiResponse<Void> response = ApiResponse.<Void>builder()
//                 .status("error")
//                 .message("Access denied. You don't have permission to access this resource.")
//                 .timestamp(LocalDateTime.now())
//                 .path(request.getRequestURI())
//                 .build();
//         return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
//     }

//     @ExceptionHandler(Exception.class)
//     public ResponseEntity<ApiResponse<Void>> handleGenericException(
//             Exception ex, HttpServletRequest request) {
//         log.error("Unexpected error occurred: ", ex);
//         ApiResponse<Void> response = ApiResponse.<Void>builder()
//                 .status("error")
//                 .message("An unexpected error occurred. Please try again later.")
//                 .timestamp(LocalDateTime.now())
//                 .path(request.getRequestURI())
//                 .build();
//         return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
//     }
// }
>>>>>>> 8c6be7d9d227d6c9e9aae8bb6ebeb4808ead22d3
