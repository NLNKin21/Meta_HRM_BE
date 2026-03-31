package com.metahrms.employee_management.dto.request.Leave;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class LeaveCancelDto {

    @NotNull
    private Integer employeeId;

    @NotBlank
    private String cancelReason;
}