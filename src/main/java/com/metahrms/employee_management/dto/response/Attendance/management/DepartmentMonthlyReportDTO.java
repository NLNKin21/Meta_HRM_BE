package com.metahrms.employee_management.dto.response.Attendance.management;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DepartmentMonthlyReportDTO {

    private Integer deptId;
    private String deptName;
    private Integer year;
    private Integer month;
    private String monthName;
    private Integer totalEmployees;

    // Tổng hợp toàn phòng
    private Double avgAttendanceRate;
    private Double totalWorkHours;
    private Double totalOvertimeHours;

    // Chi tiết từng nhân viên
    private List<MonthlyEmployeeReportDTO> employees;
}