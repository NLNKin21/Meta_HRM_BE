package com.metahrms.employee_management.dto.response.Leave;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApiResponse<T> {
    private int code;
    private String status;
    private String message;
    private T data;

    public static <T> ApiResponse<T> success(T data, String message) {
        return ApiResponse.<T>builder()
                .code(200)
                .status("success")
                .message(message)
                .data(data)
                .build();
    }
}