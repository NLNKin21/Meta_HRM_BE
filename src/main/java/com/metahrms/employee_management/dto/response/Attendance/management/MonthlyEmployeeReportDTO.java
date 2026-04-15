package com.metahrms.employee_management.dto.response.Attendance.management;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

import com.metahrms.employee_management.dto.response.Attendance.DayRecordDTO;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MonthlyEmployeeReportDTO {

    // Employee info
    private Integer employeeId;
    private String fullName;
    private String positionName;
    private String shiftName;

    // Thống kê tháng
    private Integer totalWorkDays;
    private Integer presentDays;
    private Integer absentDays;
    private Integer lateDays;
    private Integer earlyLeaveDays;
    private Integer leaveDays;
    private Double totalWorkHours;
    private Double totalOvertimeHours;
    private Double attendanceRate;

    // Chi tiết từng ngày
    private List<DayRecordDTO> days;
}