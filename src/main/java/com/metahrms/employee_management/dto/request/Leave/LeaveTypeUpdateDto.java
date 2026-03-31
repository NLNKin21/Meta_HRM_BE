package com.metahrms.employee_management.dto.request.Leave;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.util.List;

@Data
public class LeaveTypeUpdateDto {

    @NotBlank
    private String name;

    @NotNull
    @Min(0)
    private Integer maxDaysPerYear;

    @NotNull
    @Min(0)
    private Integer defaultDaysPerYear;

    @NotNull
    private Boolean paidLeave;

    @NotNull
    private Boolean requiresApproval;

    @NotNull
    private Boolean requiresDocument;

    @NotNull
    private Boolean active;

    @NotNull
    private Boolean deductBalance;

    @NotNull
    private Boolean deductFromAnnualLeaveBalance;

    @NotNull
    private Boolean autoApprove;

    @NotNull
    private Boolean allowCarryForward;

    @NotNull
    private Boolean allowEncashment;

    @NotNull
    private Boolean countsInAttendance;

    @NotNull
    private Boolean countsInCompanyPayroll;

    @NotNull
    private Boolean deductSalary;

    @NotNull
    private Boolean socialInsurancePaid;

    @NotNull
    private Boolean increaseBySeniority;

    @Valid
    private List<LeaveTypeSeniorityRuleDto> seniorityRules;
}