package com.metahrms.employee_management.dto.request.payroll;

import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateAllowanceRequest {

    @NotNull(message = "Employee ID is required")
    private Integer employeeId;

    @NotBlank(message = "Allowance type is required")
    private String allowanceType;

    @NotBlank(message = "Name is required")
    @Size(max = 200)
    private String name;

    @NotNull(message = "Amount is required")
    @DecimalMin(value = "0", message = "Amount must be >= 0")
    private BigDecimal amount;

    private Boolean isTaxable = true;

    private Boolean isInsurance = false;

    @NotNull(message = "Effective date is required")
    private LocalDate effectiveDate;

    private LocalDate expiryDate;

    private String note;
}