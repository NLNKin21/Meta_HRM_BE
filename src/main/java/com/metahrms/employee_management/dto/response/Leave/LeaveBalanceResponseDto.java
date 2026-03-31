package com.metahrms.employee_management.dto.response.Leave;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class LeaveBalanceResponseDto {
    private Long id;
    private Integer employeeId;
    private Long leaveTypeId;
    private String leaveTypeCode;
    private String leaveTypeName;
    private Integer year;
    private BigDecimal allocatedDays;
    private BigDecimal usedDays;
    private BigDecimal pendingDays;
    private BigDecimal carryForwardDays;
    private BigDecimal encashedDays;
    private BigDecimal remainingDays;
}