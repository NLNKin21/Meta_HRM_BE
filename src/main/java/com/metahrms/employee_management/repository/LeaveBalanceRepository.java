package com.metahrms.employee_management.repository;

import com.metahrms.employee_management.entity.Leave.LeaveBalance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface LeaveBalanceRepository extends JpaRepository<LeaveBalance, Long> {

    @Query("""
        select lb
        from LeaveBalance lb
        join fetch lb.leaveType lt
        where lb.employeeId = :employeeId
          and lt.id = :leaveTypeId
          and lb.year = :year
    """)
    Optional<LeaveBalance> findByEmployeeIdAndLeaveTypeIdAndYear(
            @Param("employeeId") Integer employeeId,
            @Param("leaveTypeId") Long leaveTypeId,
            @Param("year") Integer year
    );

    @Query("""
        select lb
        from LeaveBalance lb
        join fetch lb.leaveType
        where lb.employeeId = :employeeId
          and lb.year = :year
    """)
    List<LeaveBalance> findByEmployeeIdAndYear(
            @Param("employeeId") Integer employeeId,
            @Param("year") Integer year
    );
}