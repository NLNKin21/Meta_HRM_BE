package com.metahrms.employee_management.dto.request.Attendance;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.*;

/**
 * Request DTO cho check-out
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CheckOutRequestDTO {
    
    @NotNull(message = "Employee ID is required")
    @Positive
    private Long employeeId;
    
    @NotBlank(message = "Face image is required")
    private String faceImageBase64;
    
    @NotNull(message = "Location ID is required")
    private Integer locationId;
    
    @NotNull(message = "Latitude is required")
    private Double latitude;
    
    @NotNull(message = "Longitude is required")
    private Double longitude;
    
    private String deviceInfo;
    
    @Size(max = 500)
    private String note;
}
