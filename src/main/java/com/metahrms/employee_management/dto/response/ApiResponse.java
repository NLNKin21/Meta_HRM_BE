package com.metahrms.employee_management.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
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

    // ✅ Giữ nguyên Builder của bạn
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

    // ═══════════════════════════════════════════════════════════════
    // ✅ THÊM: Helper Methods (để dùng nhanh trong Controller)
    // ═══════════════════════════════════════════════════════════════

    /**
     * Success response với data và message
     */
    public static <T> ApiResponse<T> success(T data, String message) {
        return ApiResponse.<T>builder()
            .code(200)
            .status("success")
            .message(message)
            .data(data)
            .build();
    }

    /**
     * Success response với data (message mặc định)
     */
    public static <T> ApiResponse<T> success(T data) {
        return success(data, "Operation completed successfully");
    }

    /**
     * Success response không có data
     */
    public static <T> ApiResponse<T> successMessage(String message) {
        return ApiResponse.<T>builder()
            .code(200)
            .status("success")
            .message(message)
            .build();
    }

    /**
     * Error response với code và message
     */
    public static <T> ApiResponse<T> error(int code, String message) {
        return ApiResponse.<T>builder()
            .code(code)
            .status("error")
            .message(message)
            .build();
    }

    /**
     * Error response 400 Bad Request
     */
    public static <T> ApiResponse<T> badRequest(String message) {
        return error(400, message);
    }

    /**
     * Error response 404 Not Found
     */
    public static <T> ApiResponse<T> notFound(String message) {
        return error(404, message);
    }

    /**
     * Error response 500 Server Error
     */
    public static <T> ApiResponse<T> serverError(String message) {
        return error(500, message);
    }
}