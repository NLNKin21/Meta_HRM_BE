package com.metahrms.employee_management.service.Leave;

import com.metahrms.employee_management.entity.Leave.LeaveRequest;

public interface AttendanceIntegrationService {
    void handleFinalApprovedLeave(LeaveRequest request);
}