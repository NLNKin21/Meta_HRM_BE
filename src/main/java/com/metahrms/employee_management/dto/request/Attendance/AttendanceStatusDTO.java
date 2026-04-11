package com.metahrms.employee_management.dto.request.Attendance;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

import com.metahrms.employee_management.enums.Attendance.AttendanceStatus;

/**
 * DTO để check status check-in/out hôm nay
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AttendanceStatusDTO {
    
    private Boolean hasCheckedIn;
    
    private Boolean hasCheckedOut;
    
    private LocalDateTime checkInTime;
    
    private LocalDateTime checkOutTime;
    
    private AttendanceStatus status;
}