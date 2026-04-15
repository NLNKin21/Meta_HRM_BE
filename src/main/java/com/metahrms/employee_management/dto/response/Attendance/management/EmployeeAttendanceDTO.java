package com.metahrms.employee_management.dto.response.Attendance.management;

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
public class EmployeeAttendanceDTO {

    // ====== Employee Info ======
    private Integer employeeId;
    private String fullName;
    private String positionName;
    private Integer deptId;
    private String deptName;

    // ====== Shift Info ======
    private Integer shiftId;
    private String shiftName;
    private String shiftStartTime;
    private String shiftEndTime;

    // ====== Attendance Data ======
    private Integer attendanceId;       // null nếu chưa có record
    private LocalDate date;
    private AttendanceStatus status;
    private LocalDateTime checkInTime;
    private LocalDateTime checkOutTime;
    private Double workHours;
    private Double overtimeHours;
    private Integer lateMinutes;
    private Integer earlyLeaveMinutes;

    // ====== Verification / Approval ======
    private Boolean isVerified;
    private Boolean isApproved;
    private String approvalNote;

    // ====== Photos ======
    private String checkInPhotoUrl;
    private String checkOutPhotoUrl;

    // ====== Face Score ======
    private Double checkInFaceMatchScore;
    private Double checkOutFaceMatchScore;
}