package com.metahrms.employee_management.dto.request.payroll;

import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateDeductionRequest {

    @NotNull(message = "Employee ID is required")
    private Integer employeeId;

    @NotBlank(message = "Deduction type is required")
    private String deductionType;

    @NotBlank(message = "Name is required")
    @Size(max = 200)
    private String name;

    @NotNull(message = "Amount is required")
    @DecimalMin(value = "0", message = "Amount must be > 0")
    private BigDecimal amount;

    @NotNull(message = "Month is required")
    @Min(1) @Max(12)
    private Integer month;

    @NotNull(message = "Year is required")
    @Min(2020)
    private Integer year;

    @NotBlank(message = "Reason is required")
    private String reason;
}