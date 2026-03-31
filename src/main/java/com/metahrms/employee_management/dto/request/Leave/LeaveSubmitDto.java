package com.metahrms.employee_management.dto.request.Leave;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class LeaveSubmitDto {
    @NotNull
    private Integer employeeId;
}