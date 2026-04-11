package com.metahrms.employee_management.dto.request.Attendance;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.metahrms.employee_management.enums.Attendance.AttendanceStatus;

/**
 * DTO để return attendance record information
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AttendanceRecordDTO {
    
    private Integer id;
    
    private Long employeeId;
    
    private LocalDate date;
    
    private Integer shiftId;
    
    private String shiftName;
    
    private LocalDateTime checkInTime;
    
    private LocalDateTime checkOutTime;
    
    private String checkInPhotoUrl;
    
    private String checkOutPhotoUrl;
    
    private Double checkInFaceMatchScore;
    
    private Double checkOutFaceMatchScore;
    
    private AttendanceStatus status;
    
    private Double workHours;
    
    private Double overtimeHours;
    
    private Integer lateMinutes;
    
    private Integer earlyLeaveMinutes;
    
    private Boolean isVerified;
    
    private Boolean isApproved;
}