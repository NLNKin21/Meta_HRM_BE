package com.metahrms.employee_management.dto.response.report;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

import com.metahrms.employee_management.dto.response.Attendance.management.DepartmentSummaryDTO;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CompanyDailyReportDTO {

    private LocalDate date;
    private String dayOfWeek;

    // ====== Tổng công ty ======
    private Integer totalEmployees;
    private Integer presentCount;
    private Integer absentCount;
    private Integer lateCount;
    private Integer earlyLeaveCount;
    private Integer leaveCount;
    private Integer notCheckedCount;
    private Double attendanceRate;
    private Double punctualityRate;
    private Double totalWorkHours;
    private Double totalOvertimeHours;

    // ====== Chi tiết theo phòng ban ======
    private List<DepartmentSummaryDTO> byDepartment;
}