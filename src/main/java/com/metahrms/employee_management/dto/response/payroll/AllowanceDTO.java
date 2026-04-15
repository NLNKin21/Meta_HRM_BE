package com.metahrms.employee_management.dto.response.payroll;

import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AllowanceDTO {
    private Integer id;
    private Integer employeeId;
    private String employeeName;
    private String allowanceType;
    private String allowanceTypeName;
    private String name;
    private BigDecimal amount;
    private Boolean isTaxable;
    private Boolean isInsurance;
    private LocalDate effectiveDate;
    private LocalDate expiryDate;
    private Boolean isActive;
    private String note;
    private LocalDateTime createdAt;
}