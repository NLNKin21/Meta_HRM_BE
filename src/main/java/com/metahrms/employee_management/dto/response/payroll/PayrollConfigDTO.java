package com.metahrms.employee_management.dto.response.payroll;

import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PayrollConfigDTO {
    private Integer id;
    private String configKey;
    private BigDecimal configValue;
    private String configGroup;
    private String description;
    private Boolean isActive;
    private LocalDateTime updatedAt;
}