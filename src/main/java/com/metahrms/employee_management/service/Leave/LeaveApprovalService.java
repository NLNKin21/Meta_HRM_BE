package com.metahrms.employee_management.service.Leave;

import com.metahrms.employee_management.dto.request.Leave.LeaveApproveDto;
import com.metahrms.employee_management.dto.request.Leave.LeaveRejectDto;
import com.metahrms.employee_management.dto.response.Leave.HrLeaveDashboardSummaryDto;
import com.metahrms.employee_management.dto.response.Leave.LeaveRequestResponseDto;
import com.metahrms.employee_management.dto.response.Leave.ManagerLeaveSummaryDto;

import java.time.LocalDate;
import java.util.List;

public interface LeaveApprovalService {
    List<LeaveRequestResponseDto> getPendingForManager(Integer managerId);

    List<LeaveRequestResponseDto> getManagerHistory(
            Integer managerId,
            LocalDate startDate,
            LocalDate endDate
    );

    List<LeaveRequestResponseDto> getPendingForHr(Integer hrId);

    List<LeaveRequestResponseDto> getHrHistory(
            Integer hrId,
            LocalDate startDate,
            LocalDate endDate
    );

    HrLeaveDashboardSummaryDto getHrDashboardSummary(Integer hrId);

    LeaveRequestResponseDto approve(Long leaveRequestId, LeaveApproveDto dto);

    LeaveRequestResponseDto reject(Long leaveRequestId, LeaveRejectDto dto);

    ManagerLeaveSummaryDto getManagerSummary(
            Integer managerId,
            LocalDate startDate,
            LocalDate endDate
    );
}