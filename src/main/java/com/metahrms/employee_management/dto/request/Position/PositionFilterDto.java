package com.metahrms.employee_management.dto.request.Position;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PositionFilterDto {
    private int page;
    private int pageSize;
    private String search;
    private Integer deptId;
    private Boolean isActive;
}