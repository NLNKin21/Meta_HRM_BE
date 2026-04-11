package com.metahrms.employee_management.dto.request.Attendance;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO để return thông tin face (không bao gồm embedding)
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeFaceDTO {
    
    private Integer id;
    
    private Long employeeId;
    
    private String faceImageUrl;
    
    private Double confidenceScore;
    
    private Boolean isPrimary;
    
    private Boolean isActive;
    
    private LocalDateTime createdAt;
}