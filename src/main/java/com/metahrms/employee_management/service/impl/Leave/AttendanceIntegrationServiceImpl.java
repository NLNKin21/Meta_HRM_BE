package com.metahrms.employee_management.service.impl.Leave;

import com.metahrms.employee_management.entity.Leave.LeaveRequest;
import com.metahrms.employee_management.entity.Leave.LeaveType;
import com.metahrms.employee_management.service.Leave.AttendanceIntegrationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class AttendanceIntegrationServiceImpl implements AttendanceIntegrationService {

    @Override
    public void handleFinalApprovedLeave(LeaveRequest request) {
        LeaveType leaveType = request.getLeaveType();

        if (Boolean.FALSE.equals(leaveType.getCountsInAttendance())) {
            log.info("Skip attendance for leave request {}", request.getId());
            return;
        }

        log.info("Mark attendance for leave request {}, employeeId={}, totalDays={}",
                request.getId(), request.getEmployeeId(), request.getTotalDays());
    }
}