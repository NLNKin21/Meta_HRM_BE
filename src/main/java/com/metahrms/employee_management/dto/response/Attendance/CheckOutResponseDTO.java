package com.metahrms.employee_management.dto.response.Attendance;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import com.metahrms.employee_management.enums.Attendance.AttendanceStatus;

/**
 * Response DTO cho check-out
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CheckOutResponseDTO {
    
    private Boolean success;
    
    private String message;
    
    private CheckOutDataDTO data;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CheckOutDataDTO {
        
        private Integer attendanceId;
        
        private Long employeeId;
        
        private LocalDateTime checkInTime;
        
        private LocalDateTime checkOutTime;
        
        private AttendanceStatus status;
        
        private BigDecimal workHours;
        
        private BigDecimal overtimeHours;
        
        private Integer earlyLeaveMinutes;
        
        private Double faceMatchScore;
        
        private String checkOutPhotoUrl;
        
        private Boolean isVerified;
        
        private List<String> warnings;
        
        private List<CheckInResponseDTO.AnomalyInfo> anomalies;
    }
}