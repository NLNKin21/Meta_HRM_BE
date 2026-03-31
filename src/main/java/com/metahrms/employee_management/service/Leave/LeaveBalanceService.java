package com.metahrms.employee_management.service.Leave;

import com.metahrms.employee_management.dto.request.Leave.LeaveBalanceInitDto;
import com.metahrms.employee_management.dto.response.Leave.LeaveBalanceResponseDto;

import java.math.BigDecimal;
import java.util.List;

public interface LeaveBalanceService {
    LeaveBalanceResponseDto initBalance(LeaveBalanceInitDto dto);
    List<LeaveBalanceResponseDto> getEmployeeBalances(Integer employeeId, Integer year);
    void addPendingDays(Integer employeeId, Long leaveTypeId, Integer year, BigDecimal days);
    void approveDays(Integer employeeId, Long leaveTypeId, Integer year, BigDecimal days);
    void rollbackPendingDays(Integer employeeId, Long leaveTypeId, Integer year, BigDecimal days);
    void rollbackUsedDays(Integer employeeId, Long leaveTypeId, Integer year, BigDecimal days);
    void validateEnoughBalance(Integer employeeId, Long leaveTypeId, Integer year, BigDecimal days);
    void syncBalancesForYear(Integer year);

    void syncBalancesForLeaveType(Long leaveTypeId, Integer year);

    void initBalancesForEmployee(Integer employeeId, Integer year);
}