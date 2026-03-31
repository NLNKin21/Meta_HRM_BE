package com.metahrms.employee_management.service.Leave;

import com.metahrms.employee_management.dto.request.Leave.LeaveApproveDto;
import com.metahrms.employee_management.dto.request.Leave.LeaveRejectDto;
import com.metahrms.employee_management.dto.response.Leave.LeaveRequestResponseDto;

import java.util.List;

public interface LeaveApprovalService {
    List<LeaveRequestResponseDto> getPendingForManager(Integer managerId);
    List<LeaveRequestResponseDto> getPendingForHr(Integer hrId);
    LeaveRequestResponseDto approve(Long leaveRequestId, LeaveApproveDto dto);
    LeaveRequestResponseDto reject(Long leaveRequestId, LeaveRejectDto dto);
}