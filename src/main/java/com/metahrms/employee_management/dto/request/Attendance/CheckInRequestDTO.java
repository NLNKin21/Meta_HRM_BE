package com.metahrms.employee_management.dto.request.Attendance;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


/**
 * Request DTO cho check-in
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CheckInRequestDTO {
    
    @NotNull(message = "Employee ID is required")
    @Positive
    private Long employeeId;
    
    @NotBlank(message = "Face image is required")
    private String faceImageBase64;
    
    @NotNull(message = "Location ID is required")
    private Integer locationId;
    
    @NotNull(message = "Latitude is required")
    @DecimalMin(value = "-90.0")
    @DecimalMax(value = "90.0")
    private Double latitude;
    
    @NotNull(message = "Longitude is required")
    @DecimalMin(value = "-180.0")
    @DecimalMax(value = "180.0")
    private Double longitude;
    
    /**
     * Device info (JSON string)
     * Example: {"device": "iPhone 14", "os": "iOS 17", "app_version": "1.0.0"}
     */
    private String deviceInfo;
    
    /**
     * Check-in note (optional)
     */
    @Size(max = 500)
    private String note;
}