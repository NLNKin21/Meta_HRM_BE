package com.metahrms.employee_management.dto.request.payroll;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateConfigRequest {

    @NotNull(message = "Config value is required")
    @DecimalMin(value = "0", message = "Value must be >= 0")
    private BigDecimal configValue;

    private String description;

    private Boolean isActive;
}