package com.metahrms.employee_management.repository;

import com.metahrms.employee_management.entity.Leave.LeaveAttachment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LeaveAttachmentRepository extends JpaRepository<LeaveAttachment, Long> {
    List<LeaveAttachment> findByLeaveRequestId(Long leaveRequestId);
    void deleteByLeaveRequestId(Long leaveRequestId);
}