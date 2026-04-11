package com.metahrms.employee_management.dto.response.Attendance;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Response DTO sau khi enroll face thành công
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EnrollFaceResponseDTO {
    
    private Integer faceId;
    
    private Long employeeId;
    
    private String faceImageUrl;
    
    private Double confidenceScore;
    
    private Boolean isPrimary;
    
    private String message;
}