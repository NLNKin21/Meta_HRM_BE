package com.metahrms.employee_management.dto.request.payroll;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApprovePayslipRequest {
    private String note;
}