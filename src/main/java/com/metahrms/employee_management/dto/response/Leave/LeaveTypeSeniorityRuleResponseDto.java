package com.metahrms.employee_management.dto.response.Leave;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LeaveTypeSeniorityRuleResponseDto {
    private Long id;
    private Integer minYears;
    private Integer extraDays;
}