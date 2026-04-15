package com.metahrms.employee_management.dto.response.payroll;

import lombok.*;
import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PayrollPeriodSummaryDTO {

    private Integer month;
    private Integer year;
    private String monthName;

    // Số lượng
    private Integer totalEmployees;
    private Integer draftCount;
    private Integer calculatedCount;
    private Integer approvedCount;
    private Integer paidCount;
    private Integer rejectedCount;

    // Tài chính
    private BigDecimal totalGrossSalary;
    private BigDecimal totalNetSalary;
    private BigDecimal totalInsurance;
    private BigDecimal totalPIT;
    private BigDecimal totalBonus;
    private BigDecimal totalOvertimePay;
    private BigDecimal totalCompanyCost;

    // Theo phòng ban
    private List<DeptPayrollSummaryDTO> byDepartment;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DeptPayrollSummaryDTO {
        private Integer deptId;
        private String deptName;
        private Integer employeeCount;
        private BigDecimal totalNetSalary;
        private BigDecimal totalGrossSalary;
        private BigDecimal totalCompanyCost;
    }
}