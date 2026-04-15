package com.metahrms.employee_management.dto.request.payroll;

import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateTaxInfoRequest {

    @Size(max = 20)
    private String taxCode;

    @Min(value = 0, message = "Dependents must be >= 0")
    @Max(value = 20, message = "Dependents must be <= 20")
    private Integer numberOfDependents;

    @Size(max = 30)
    private String socialInsuranceNo;

    @DecimalMin(value = "0")
    private BigDecimal socialInsuranceSalary;

    @Size(max = 100)
    private String bankName;

    @Size(max = 200)
    private String bankBranch;

    @Size(max = 30)
    private String bankAccountNumber;

    @Size(max = 150)
    private String bankAccountHolder;

    private String note;
}