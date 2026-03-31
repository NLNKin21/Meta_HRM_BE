package com.metahrms.employee_management.service.Leave;

import com.metahrms.employee_management.dto.request.Leave.LeaveTypeCreateDto;
import com.metahrms.employee_management.dto.request.Leave.LeaveTypeUpdateDto;
import com.metahrms.employee_management.dto.response.Leave.LeaveTypeResponseDto;

import java.util.List;

public interface LeaveTypeService {
    LeaveTypeResponseDto create(LeaveTypeCreateDto dto);
    LeaveTypeResponseDto update(Long id, LeaveTypeUpdateDto dto);
    LeaveTypeResponseDto getById(Long id);
    List<LeaveTypeResponseDto> getAll();
}