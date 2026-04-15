package com.metahrms.employee_management.dto.response.payroll;

import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeTaxInfoDTO {
    private Integer id;
    private Integer employeeId;
    private String employeeName;

    // Thuế
    private String taxCode;
    private Integer numberOfDependents;
    private String socialInsuranceNo;
    private BigDecimal socialInsuranceSalary;

    // Ngân hàng
    private String bankName;
    private String bankBranch;
    private String bankAccountNumber;
    private String bankAccountHolder;

    private String note;
    private LocalDateTime updatedAt;
}