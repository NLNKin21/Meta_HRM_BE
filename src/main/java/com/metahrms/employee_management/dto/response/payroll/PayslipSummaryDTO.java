package com.metahrms.employee_management.dto.response.payroll;

import lombok.*;
import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PayslipSummaryDTO {

    private Integer id;
    private Integer employeeId;
    private String employeeName;
    private String deptName;
    private String positionName;
    private String shiftName;

    // Ngày công
    private Integer standardWorkDays;
    private Integer actualWorkDays;
    private BigDecimal paidLeaveDays;
    private BigDecimal unpaidLeaveDays;
    private Integer absentDays;
    private BigDecimal totalOvertimeHours;
    private Integer totalLateTimes;

    // Thu nhập
    private BigDecimal basicSalary;
    private BigDecimal actualBasicSalary;
    private BigDecimal totalAllowances;
    private BigDecimal overtimePay;
    private BigDecimal totalBonus;
    private BigDecimal grossSalary;

    // Khấu trừ
    private BigDecimal totalInsurance;
    private BigDecimal personalIncomeTax;
    private BigDecimal latePenalty;
    private BigDecimal otherDeductions;
    private BigDecimal totalDeductionAmount;

    // Kết quả
    private BigDecimal netSalary;
    private BigDecimal totalCompanyCost;

    // Meta
    private Integer month;
    private Integer year;
    private String status;
    private String paymentMethod;
    private String note;
}
