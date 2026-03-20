package com.metahrms.employee_management.dto.response.Position;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PositionTreeResponse {
    
    private Integer id;
    private String positionCode;
    private String positionName;
    private String description;
    private BigDecimal minSalary;
    private BigDecimal maxSalary;
    private Boolean isActive;
    
    private Integer deptId;
    private String deptName;
    
    private Integer parentPositionId;
    private String parentPositionName;
    
    private Integer levelOrder;
    private Integer sortOrder;
    
    // ===== THÔNG TIN TREE =====
    
    @Builder.Default
    private List<PositionTreeResponse> children = new ArrayList<>();
    
    private Integer employeeCount;
    
    private Boolean hasChildren;
    
    // ===== COMPUTED FIELDS CHO MUI TREE VIEW =====
    
    /**
     * Node ID cho MUI TreeView (dạng String)
     */
    @JsonProperty("nodeId")
    public String getNodeId() {
        return String.valueOf(id);
    }
    
    /**
     * Label cho MUI TreeView
     */
    @JsonProperty("label")
    public String getLabel() {
        return positionName;
    }
    
    /**
     * Title hiển thị đầy đủ
     */
    @JsonProperty("title")
    public String getTitle() {
        return positionCode + " - " + positionName;
    }
}