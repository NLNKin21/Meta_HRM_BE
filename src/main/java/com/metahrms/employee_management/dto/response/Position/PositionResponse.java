package com.metahrms.employee_management.dto.response.Position;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class PositionResponse {
    private Integer id;
    private String positionCode;
    private String positionName;
    private String description;
    private BigDecimal minSalary;
    private BigDecimal maxSalary;
    private Boolean isActive;
    private Integer deptId;
    private String deptName;
}
