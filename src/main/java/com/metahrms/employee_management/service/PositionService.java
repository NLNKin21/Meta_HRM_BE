package com.metahrms.employee_management.service;

import com.metahrms.employee_management.dto.request.Position.PositionDto;
import com.metahrms.employee_management.dto.request.Position.PositionFilterDto;
import com.metahrms.employee_management.dto.response.PagedResponse;
import com.metahrms.employee_management.dto.response.Position.PositionResponse;

public interface PositionService {
    PagedResponse<PositionResponse> getPositions(PositionFilterDto filterDto);
    PositionResponse getPositionById(Integer id);
    PositionResponse createPosition(PositionDto createDto);
    PositionResponse updatePosition(Integer id, PositionDto updateDto);
    void deletePosition(Integer id);
}