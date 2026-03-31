package com.metahrms.employee_management.repository;

import com.metahrms.employee_management.entity.Leave.LeaveTypeSeniorityRule;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LeaveTypeSeniorityRuleRepository extends JpaRepository<LeaveTypeSeniorityRule, Long> {
    List<LeaveTypeSeniorityRule> findByLeaveTypeIdOrderByMinYearsAsc(Long leaveTypeId);
    void deleteByLeaveTypeId(Long leaveTypeId);
}