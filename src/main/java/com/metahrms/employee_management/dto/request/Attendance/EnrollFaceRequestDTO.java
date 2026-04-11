package com.metahrms.employee_management.dto.request.Attendance;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

/**
 * Request DTO để enroll face từ mobile app
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EnrollFaceRequestDTO {
    
    @NotNull(message = "Employee ID is required")
    @Positive
    private Long employeeId;
    
    @NotBlank(message = "Image base64 is required")
    private String imageBase64;
    
    private Boolean isPrimary = true;
}