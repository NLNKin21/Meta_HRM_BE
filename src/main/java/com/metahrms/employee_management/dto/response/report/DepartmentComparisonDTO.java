package com.metahrms.employee_management.dto.response.report;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DepartmentComparisonDTO {

    private LocalDate startDate;
    private LocalDate endDate;
    private Integer totalDepartments;

    private List<DeptCompareItemDTO> departments;

    // Xếp hạng
    private String bestDepartment;          // Dept có attendance rate cao nhất
    private String worstDepartment;         // Dept có attendance rate thấp nhất

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DeptCompareItemDTO {
        private Integer deptId;
        private String deptName;
        private Integer totalEmployees;
        private Integer rank;               // Xếp hạng (1 = tốt nhất)

        // Thống kê
        private Double attendanceRate;
        private Double punctualityRate;
        private Integer totalPresentDays;
        private Integer totalAbsentDays;
        private Integer totalLateDays;
        private Double totalWorkHours;
        private Double totalOvertimeHours;
        private Double avgWorkHoursPerDay;
    }
}