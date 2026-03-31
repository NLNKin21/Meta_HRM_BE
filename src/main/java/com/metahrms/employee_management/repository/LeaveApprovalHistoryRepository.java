package com.metahrms.employee_management.repository;

import com.metahrms.employee_management.entity.Leave.LeaveApprovalHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LeaveApprovalHistoryRepository extends JpaRepository<LeaveApprovalHistory, Long> {
    List<LeaveApprovalHistory> findByLeaveRequestIdOrderByActionAtAsc(Long leaveRequestId);
}