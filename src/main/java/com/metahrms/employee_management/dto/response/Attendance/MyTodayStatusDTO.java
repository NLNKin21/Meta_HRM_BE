package com.metahrms.employee_management.dto.response.Attendance;

import com.metahrms.employee_management.enums.Attendance.AttendanceStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MyTodayStatusDTO {

    private LocalDate date;

    // Check-in/out status
    private Boolean hasCheckedIn;
    private Boolean hasCheckedOut;
    private LocalDateTime checkInTime;
    private LocalDateTime checkOutTime;
    private AttendanceStatus status;

    // Shift info
    private String shiftName;
    private String shiftStartTime;
    private String shiftEndTime;

    // Nếu đã check-in
    private Integer lateMinutes;
    private Double workHoursUntilNow;   // Giờ làm tính đến hiện tại (chưa check-out)
    private Double workHours;           // Giờ làm thực tế (đã check-out)

    // Location
    private String checkInLocationName;
    private String checkOutLocationName;

    // Verification
    private Boolean isVerified;
    private Boolean isApproved;
}