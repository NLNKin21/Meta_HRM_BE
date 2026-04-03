package com.metahrms.employee_management.service;

public interface HRNotificationHelperService {

    void notifyEmployeeSubmittedLeave(
            Integer employeeId,
            Long leaveRequestId,
            String startDate,
            String endDate
    );

    void notifyManagerNewLeaveRequest(
            Integer managerId,
            Long leaveRequestId,
            String employeeName
    );

    void notifyEmployeeLeaveApprovedByManager(
            Integer employeeId,
            Long leaveRequestId
    );

    void notifyHrLeaveWaitingForApproval(
            Integer hrId,
            Long leaveRequestId,
            String employeeName
    );

    void notifyEmployeeLeaveApprovedFinal(
            Integer employeeId,
            Long leaveRequestId
    );

    void notifyEmployeeLeaveRejected(
            Integer employeeId,
            Long leaveRequestId,
            String reason
    );

    void notifyManagerLeaveCancelled(
            Integer managerId,
            Long leaveRequestId,
            String employeeName
    );
}