package com.metahrms.employee_management.dto.request.payroll;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RejectPayslipRequest {

    @NotBlank(message = "Reject reason is required")
    private String reason;
}