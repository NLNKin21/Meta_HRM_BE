package com.metahrms.employee_management.dto.response.Leave;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class LeaveTypeResponseDto {
    private Long id;
    private String code;
    private String name;
    private Integer maxDaysPerYear;
    private Integer defaultDaysPerYear;
    private Boolean paidLeave;
    private Boolean requiresApproval;
    private Boolean requiresDocument;
    private Boolean active;
    private Boolean deductBalance;
    private Boolean deductFromAnnualLeaveBalance;
    private Boolean autoApprove;
    private Boolean allowCarryForward;
    private Boolean allowEncashment;
    private Boolean countsInAttendance;
    private Boolean countsInCompanyPayroll;
    private Boolean deductSalary;
    private Boolean socialInsurancePaid;
    private Boolean increaseBySeniority;
    private List<LeaveTypeSeniorityRuleResponseDto> seniorityRules;
}