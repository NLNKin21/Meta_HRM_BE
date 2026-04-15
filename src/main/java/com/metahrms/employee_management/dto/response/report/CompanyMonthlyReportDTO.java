package com.metahrms.employee_management.dto.response.report;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CompanyMonthlyReportDTO {

    private Integer year;
    private Integer month;
    private String monthName;

    // ====== Tổng công ty ======
    private Integer totalEmployees;
    private Double avgAttendanceRate;
    private Double avgPunctualityRate;
    private Double totalWorkHours;
    private Double totalOvertimeHours;

    // ====== Ngày công tổng hợp ======
    private Integer totalWorkDaysInMonth;   // Số ngày làm việc trong tháng
    private Integer totalPresentSlots;      // Tổng lượt đi làm (NV x ngày)
    private Integer totalAbsentSlots;       // Tổng lượt vắng

    // ====== Chi tiết theo phòng ban ======
    private List<DepartmentMonthlyStatDTO> byDepartment;

    // ====== Top performers ======
    private List<TopPerformerDTO> topAttendance;    // Đi làm đầy đủ nhất
    private List<TopPerformerDTO> topLate;          // Đi trễ nhiều nhất

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DepartmentMonthlyStatDTO {
        private Integer deptId;
        private String deptName;
        private Integer totalEmployees;
        private Double avgAttendanceRate;
        private Double totalWorkHours;
        private Double totalOvertimeHours;
        private Integer totalLateDays;
        private Integer totalAbsentDays;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TopPerformerDTO {
        private Integer employeeId;
        private String fullName;
        private String deptName;
        private Integer value;              // Số ngày đi làm / số ngày trễ
        private Double attendanceRate;
    }
}