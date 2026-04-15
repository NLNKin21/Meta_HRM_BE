package com.metahrms.employee_management.dto.response.Attendance.management;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DepartmentSummaryDTO {

    private Integer deptId;
    private String deptName;
    private LocalDate date;

    // Số nhân viên
    private Integer totalEmployees;
    private Integer presentCount;
    private Integer absentCount;
    private Integer lateCount;
    private Integer earlyLeaveCount;
    private Integer leaveCount;
    private Integer notCheckedInCount;   // Chưa check-in
    private Integer notCheckedOutCount;  // Đã check-in nhưng chưa check-out

    // Tỷ lệ
    private Double attendanceRate;       // %
    private Double punctualityRate;      // %

    // Giờ công
    private Double totalWorkHours;
    private Double totalOvertimeHours;
}