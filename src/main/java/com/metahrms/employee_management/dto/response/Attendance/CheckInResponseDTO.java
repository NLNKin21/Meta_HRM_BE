package com.metahrms.employee_management.dto.response.Attendance;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

import com.metahrms.employee_management.enums.Attendance.AttendanceStatus;

/**
 * Response DTO cho check-in
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CheckInResponseDTO {
    
    private Boolean success;
    
    private String message;
    
    private CheckInDataDTO data;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CheckInDataDTO {
        
        private Integer attendanceId;
        
        private Long employeeId;
        
        private LocalDateTime checkInTime;
        
        private AttendanceStatus status;
        
        private Integer lateMinutes;
        
        private Double faceMatchScore;
        
        private String checkInPhotoUrl;
        
        private Boolean isVerified;
        
        private List<String> warnings;
        
        private List<AnomalyInfo> anomalies;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AnomalyInfo {
        private String type;
        private String severity;
        private String message;
    }
}