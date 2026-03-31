package com.metahrms.employee_management.service.Leave;

import com.metahrms.employee_management.dto.response.Leave.LeavePayrollImpactDto;
import com.metahrms.employee_management.entity.Leave.LeaveRequest;

import java.math.BigDecimal;

public interface PayrollIntegrationService {
    LeavePayrollImpactDto calculateImpact(LeaveRequest request, BigDecimal dailySalary);
    void handleFinalApprovedLeave(LeaveRequest request);
}