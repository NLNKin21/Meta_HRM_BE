package com.metahrms.employee_management.service.Leave;

import com.metahrms.employee_management.dto.response.Leave.ManagerLeaveSummaryDto;

public interface LeaveDashboardService {
    ManagerLeaveSummaryDto getManagerSummary(Integer managerId);
}