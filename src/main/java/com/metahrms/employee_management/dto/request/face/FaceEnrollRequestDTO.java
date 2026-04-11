package com.metahrms.employee_management.dto.request.face;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;



/**
 * Request DTO để gọi Python AI Service - Enrollment
 * 
 * Mapping tới Python Pydantic model: FaceEnrollRequest
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FaceEnrollRequestDTO {
    
    @NotNull(message = "Employee ID is required")
    @Positive(message = "Employee ID must be positive")
    @JsonProperty("employee_id")
    private Long employeeId;
    
    @NotBlank(message = "Image base64 is required")
    @JsonProperty("image_base64")
    private String imageBase64;
    
    @JsonProperty("is_primary")
    private Boolean isPrimary = true;
    
    @JsonProperty("note")
    private String note;
}