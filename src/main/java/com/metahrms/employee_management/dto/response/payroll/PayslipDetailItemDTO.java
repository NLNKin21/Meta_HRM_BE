package com.metahrms.employee_management.dto.response.payroll;

import lombok.*;
import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PayslipDetailItemDTO {
    private Integer id;
    private String itemType;    // EARNING or DEDUCTION
    private String itemCode;
    private String itemName;
    private BigDecimal amount;
    private BigDecimal quantity;
    private BigDecimal rate;
    private String note;
    private Integer sortOrder;
}