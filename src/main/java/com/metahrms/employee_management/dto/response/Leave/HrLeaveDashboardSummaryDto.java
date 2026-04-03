package com.metahrms.employee_management.dto.response.Leave;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HrLeaveDashboardSummaryDto {
    private long employeesOnLeave;
    private long pendingRequests;
    private double employeesOnLeaveChangePercent;
    private double pendingRequestsChangePercent;
}