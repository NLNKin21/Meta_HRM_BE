package com.metahrms.employee_management.dto.request.payroll;

import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateAllowanceRequest {

    @Size(max = 200)
    private String name;

    @DecimalMin(value = "0")
    private BigDecimal amount;

    private Boolean isTaxable;

    private Boolean isInsurance;

    private LocalDate effectiveDate;

    private LocalDate expiryDate;

    private Boolean isActive;

    private String note;
}