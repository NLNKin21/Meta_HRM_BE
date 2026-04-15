package com.metahrms.employee_management.dto.response.payroll;

import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PayslipFullDTO {

    // Header
    private Integer id;
    private Integer employeeId;
    private String employeeName;
    private String deptName;
    private String positionName;
    private Integer month;
    private Integer year;
    private String status;

    // Bank info
    private String bankName;
    private String bankAccountNumber;
    private String bankAccountHolder;

    // Ngày công
    private Integer standardWorkDays;
    private Integer actualWorkDays;
    private BigDecimal paidLeaveDays;
    private BigDecimal unpaidLeaveDays;
    private Integer absentDays;
    private BigDecimal overtimeHoursWeekday;
    private BigDecimal overtimeHoursWeekend;
    private BigDecimal overtimeHoursHoliday;
    private BigDecimal totalOvertimeHours;
    private Integer totalLateTimes;
    private Integer totalLateMinutes;

    // Thu nhập tổng hợp
    private BigDecimal basicSalary;
    private BigDecimal actualBasicSalary;
    private BigDecimal totalAllowances;
    private BigDecimal overtimePay;
    private BigDecimal totalBonus;
    private BigDecimal grossSalary;

    // Bảo hiểm
    private BigDecimal insuranceSalary;
    private BigDecimal socialInsurance;
    private BigDecimal healthInsurance;
    private BigDecimal unemploymentInsurance;
    private BigDecimal totalInsurance;

    // Thuế
    private BigDecimal taxableIncome;
    private BigDecimal personalDeduction;
    private BigDecimal dependentDeduction;
    private BigDecimal personalIncomeTax;

    // Khấu trừ
    private BigDecimal latePenalty;
    private BigDecimal otherDeductions;
    private BigDecimal totalDeductionAmount;

    // Kết quả
    private BigDecimal netSalary;

    // Chi phí công ty
    private BigDecimal companySocialInsurance;
    private BigDecimal companyHealthInsurance;
    private BigDecimal companyUnemployment;
    private BigDecimal totalCompanyCost;

    // Chi tiết từng khoản
    private List<PayslipDetailItemDTO> earnings;    // EARNING items
    private List<PayslipDetailItemDTO> deductions;  // DEDUCTION items

    // Meta
    private String note;
    private LocalDateTime calculatedAt;
    private LocalDateTime approvedAt;
    private LocalDateTime paidAt;
}