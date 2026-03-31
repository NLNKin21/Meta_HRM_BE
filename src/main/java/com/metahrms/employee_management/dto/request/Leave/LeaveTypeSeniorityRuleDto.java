package com.metahrms.employee_management.dto.request.Leave;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class LeaveTypeSeniorityRuleDto {

    private Long id;

    @NotNull
    @Min(0)
    private Integer minYears;

    @NotNull
    @Min(0)
    private Integer extraDays;
}