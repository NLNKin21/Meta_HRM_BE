package com.metahrms.employee_management.dto.request.payroll;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EditPayslipRequest {

    // Cho phép override các field
    private BigDecimal totalBonus;
    private BigDecimal otherDeductions;
    private BigDecimal overtimePay;

    @NotBlank(message = "Edit reason is required")
    private String note;
}