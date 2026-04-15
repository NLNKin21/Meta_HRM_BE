package com.metahrms.employee_management.dto.response.payroll;

import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BonusDTO {
    private Integer id;
    private Integer employeeId;
    private String employeeName;
    private String deptName;
    private String bonusType;
    private String bonusTypeName;
    private String name;
    private BigDecimal amount;
    private Boolean isTaxable;
    private Integer month;
    private Integer year;
    private String reason;
    private Boolean isApproved;
    private Integer approvedBy;
    private LocalDateTime approvedAt;
    private LocalDateTime createdAt;
}