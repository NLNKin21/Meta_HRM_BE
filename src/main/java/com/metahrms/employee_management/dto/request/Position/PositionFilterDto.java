package com.metahrms.employee_management.dto.request.Position;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PositionFilterDto {
   
    private String search;
    private Integer deptId;
    private Boolean isActive;
    private Integer parentPositionId; // Lọc theo parent
    private Integer levelOrder;       // Lọc theo cấp bậc
    // Pagination
    @Builder.Default
    private int page = 0;
    
    @Builder.Default
    private int pageSize = 10;
}