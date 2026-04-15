package com.metahrms.employee_management.dto.response.Attendance;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MyAttendanceSummaryDTO {

    private Integer year;
    private Integer month;

    // ============ Ngày công ============
    private Integer totalWorkDays;      // Tổng ngày làm việc trong tháng (theo ca)
    private Integer presentDays;        // Số ngày đi làm (PRESENT + LATE + EARLY_LEAVE)
    private Integer absentDays;         // Số ngày vắng (ABSENT)
    private Integer lateDays;           // Số ngày đi trễ (LATE)
    private Integer earlyLeaveDays;     // Số ngày về sớm (EARLY_LEAVE)
    private Integer leaveDays;          // Số ngày nghỉ phép (LEAVE)
    private Integer notCheckedDays;     // Chưa chấm công (NOT_CHECKED)

    // ============ Giờ công ============
    private Double totalWorkHours;      // Tổng giờ làm thực tế
    private Double totalOvertimeHours;  // Tổng giờ OT
    private Double totalLateMinutes;    // Tổng phút đi trễ
    private Double totalEarlyLeaveMinutes; // Tổng phút về sớm

    // ============ Tỷ lệ ============
    private Double attendanceRate;      // % đi làm = presentDays / totalWorkDays * 100
    private Double punctualityRate;     // % đúng giờ = (presentDays - lateDays) / presentDays * 100

    // ============ Trạng thái tháng hiện tại ============
    private Boolean hasCheckedInToday;
    private Boolean hasCheckedOutToday;
}