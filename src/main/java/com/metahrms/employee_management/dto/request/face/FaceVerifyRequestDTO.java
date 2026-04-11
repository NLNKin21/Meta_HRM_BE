package com.metahrms.employee_management.dto.request.face;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


import java.util.List;

/**
 * Request DTO để gọi Python AI Service - Verification
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FaceVerifyRequestDTO {
    
    @NotNull(message = "Employee ID is required")
    @Positive
    @JsonProperty("employee_id")
    private Long employeeId;
    
    @NotBlank(message = "Image base64 is required")
    @JsonProperty("image_base64")
    private String imageBase64;
    
    @NotEmpty(message = "Known embeddings are required")
    @JsonProperty("known_embeddings")
    private List<List<Double>> knownEmbeddings;
    
    @JsonProperty("verification_threshold")
    private Double verificationThreshold;
}