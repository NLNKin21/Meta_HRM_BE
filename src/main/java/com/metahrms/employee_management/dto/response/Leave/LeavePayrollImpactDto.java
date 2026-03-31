package com.metahrms.employee_management.dto.response.Leave;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class LeavePayrollImpactDto {
    private Long leaveRequestId;
    private String leaveTypeCode;
    private BigDecimal totalDays;
    private Boolean countsInCompanyPayroll;
    private Boolean deductSalary;
    private Boolean socialInsurancePaid;
    private BigDecimal salaryDeduction;
    private BigDecimal companyPayrollAmount;
    private String note;
}