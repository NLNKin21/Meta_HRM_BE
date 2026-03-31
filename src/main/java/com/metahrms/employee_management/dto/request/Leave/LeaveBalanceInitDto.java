package com.metahrms.employee_management.dto.request.Leave;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class LeaveBalanceInitDto {

    @NotNull
    private Integer employeeId;

    @NotNull
    private Long leaveTypeId;

    @NotNull
    private Integer year;

    @NotNull
    private BigDecimal allocatedDays;

    private BigDecimal carryForwardDays = BigDecimal.ZERO;
}