package com.metahrms.employee_management.service;

import java.util.List;

import com.metahrms.employee_management.dto.request.Position.PositionDto;
import com.metahrms.employee_management.dto.request.Position.PositionFilterDto;
import com.metahrms.employee_management.dto.response.PagedResponse;
import com.metahrms.employee_management.dto.response.Position.PositionResponse;
import com.metahrms.employee_management.dto.response.Position.PositionTreeResponse;

public interface PositionService {
    PagedResponse<PositionResponse> getPositions(PositionFilterDto filterDto);
    PositionResponse getPositionById(Integer id);
    PositionResponse createPosition(PositionDto createDto);
    PositionResponse updatePosition(Integer id, PositionDto updateDto);
    void deletePosition(Integer id);
    /**
     * Lấy toàn bộ cây tổ chức
     */
    List<PositionTreeResponse> getPositionTree();
    
    /**
     * Lấy cây tổ chức theo department
     */
    List<PositionTreeResponse> getPositionTreeByDepartment(Integer deptId);
    
    /**
     * Di chuyển position sang parent khác (Drag & Drop)
     */
    PositionTreeResponse movePosition(Integer positionId, Integer newParentId);
    
    /**
     * Lấy danh sách positions có thể làm parent (không bao gồm chính nó và con cháu của nó)
     */
    List<PositionResponse> getAvailableParents(Integer positionId);
}