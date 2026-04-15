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
public class DayRecordDTO {

    private LocalDate date;
    private String dayOfWeek;           // "Monday", "Tuesday"...
    private Boolean isWorkDay;          // Ca làm việc có ngày này không

    // Attendance data (null nếu chưa có record)
    private Integer attendanceId;
    private AttendanceStatus status;    // PRESENT, LATE, ABSENT...
    private LocalDateTime checkInTime;
    private LocalDateTime checkOutTime;
    private Double workHours;
    private Double overtimeHours;
    private Integer lateMinutes;
    private Integer earlyLeaveMinutes;

    // Verification
    private Boolean isVerified;
    private Boolean isApproved;

    // Shift info
    private String shiftName;
    private String shiftStartTime;      // "08:00"
    private String shiftEndTime;        // "17:00"
}