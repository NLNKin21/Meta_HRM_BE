package com.metahrms.employee_management.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
<<<<<<< HEAD
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
=======

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
>>>>>>> 8c6be7d9d227d6c9e9aae8bb6ebeb4808ead22d3

@Data
@NoArgsConstructor
@AllArgsConstructor
<<<<<<< HEAD
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {
    
    private String status;
    private String message;
    private T data;
    private LocalDateTime timestamp;
    private String path;

    public static <T> ApiResponse<T> success(T data) {
        return ApiResponse.<T>builder()
                .status("success")
                .message("Success")
                .data(data)
                .timestamp(LocalDateTime.now())
                .build();
    }

    public static <T> ApiResponse<T> success(String message, T data) {
        return ApiResponse.<T>builder()
                .status("success")
                .message(message)
                .data(data)
                .timestamp(LocalDateTime.now())
                .build();
    }

    public static <T> ApiResponse<T> error(String message) {
        return ApiResponse.<T>builder()
                .status("error")
                .message(message)
                .timestamp(LocalDateTime.now())
                .build();
    }

    public static <T> ApiResponse<T> error(String message, T data) {
        return ApiResponse.<T>builder()
                .status("error")
                .message(message)
                .data(data)
                .timestamp(LocalDateTime.now())
                .build();
    }
}
=======
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "Generic API response wrapper for all endpoints")
public class ApiResponse<T> {
    @Schema(description = "HTTP status code", example = "200")
    int code;

    @Schema(description = "Response message describing the result", example = "Operation completed successfully")
    String message;

    @Schema(description = "Response status (success/error)", example = "success")
    String status;

    @Schema(description = "Response data payload (generic type)")
    T data;

    public static <T> Builder<T> builder() {
        return new Builder<>();
    }

    public static class Builder<T> {
        private int code;
        private String message;
        private String status;
        private T data;

        public Builder<T> code(int code) {
            this.code = code;
            return this;
        }

        public Builder<T> message(String message) {
            this.message = message;
            return this;
        }

        public Builder<T> status(String status) {
            this.status = status;
            return this;
        }

        public Builder<T> data(T data) {
            this.data = data;
            return this;
        }

        public ApiResponse<T> build() {
            ApiResponse<T> response = new ApiResponse<>();
            response.code = this.code;
            response.message = this.message;
            response.status = this.status;
            response.data = this.data;
            return response;
        }
    }
}
>>>>>>> 8c6be7d9d227d6c9e9aae8bb6ebeb4808ead22d3
