package com.metahrms.employee_management.dto.request.Position;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class PositionDto {
    @NotBlank(message = "Position code is required")
    @Size(max = 20, message = "Position code must be less than 20 characters")
    private String positionCode;

    @NotBlank(message = "Position name is required")
    @Size(max = 100, message = "Position name must be less than 100 characters")
    private String positionName;

    private String description;

    private BigDecimal minSalary;

    private BigDecimal maxSalary;

    @NotNull(message = "Department ID is required")
    private Integer deptId;

    private Integer parentPositionId;
    
    private Integer levelOrder;
    
    private Integer sortOrder;
}
