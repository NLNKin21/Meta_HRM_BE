package com.metahrms.employee_management.repository;

import com.metahrms.employee_management.entity.Leave.LeaveRequest;
import com.metahrms.employee_management.enums.Leave.LeaveApprovalStage;
import com.metahrms.employee_management.enums.Leave.LeaveStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface LeaveRequestRepository extends JpaRepository<LeaveRequest, Long> {

    @Query("""
        SELECT lr
        FROM LeaveRequest lr
        JOIN FETCH lr.leaveType
        WHERE lr.id = :id
    """)
    Optional<LeaveRequest> findByIdWithLeaveType(@Param("id") Long id);

    @Query("""
        SELECT lr
        FROM LeaveRequest lr
        JOIN FETCH lr.leaveType
        WHERE lr.employeeId = :employeeId
    """)
    List<LeaveRequest> findByEmployeeIdWithLeaveType(@Param("employeeId") Integer employeeId);

    @Query("""
        SELECT lr
        FROM LeaveRequest lr
        JOIN FETCH lr.leaveType
        WHERE lr.managerId = :managerId
          AND lr.status = :status
          AND lr.approvalStage = :stage
    """)
    List<LeaveRequest> findByManagerIdAndStatusAndApprovalStageWithLeaveType(
            @Param("managerId") Integer managerId,
            @Param("status") LeaveStatus status,
            @Param("stage") LeaveApprovalStage stage
    );

    @Query("""
        SELECT lr
        FROM LeaveRequest lr
        JOIN FETCH lr.leaveType
        WHERE lr.hrId = :hrId
          AND lr.status = :status
          AND lr.approvalStage = :stage
    """)
    List<LeaveRequest> findByHrIdAndStatusAndApprovalStageWithLeaveType(
            @Param("hrId") Integer hrId,
            @Param("status") LeaveStatus status,
            @Param("stage") LeaveApprovalStage stage
    );

    @Query("""
        SELECT lr
        FROM LeaveRequest lr
        JOIN FETCH lr.leaveType
        WHERE lr.status = :status
          AND lr.startDate <= :endDate
          AND lr.endDate >= :startDate
    """)
    List<LeaveRequest> findByStatusAndDateRangeWithLeaveType(
            @Param("status") LeaveStatus status,
            @Param("endDate") LocalDate endDate,
            @Param("startDate") LocalDate startDate
    );

    List<LeaveRequest> findByEmployeeId(Integer employeeId);

    List<LeaveRequest> findByManagerIdAndStatusAndApprovalStage(
            Integer managerId,
            LeaveStatus status,
            LeaveApprovalStage stage
    );

    List<LeaveRequest> findByHrIdAndStatusAndApprovalStage(
            Integer hrId,
            LeaveStatus status,
            LeaveApprovalStage stage
    );

    List<LeaveRequest> findByStatusAndStartDateLessThanEqualAndEndDateGreaterThanEqual(
            LeaveStatus status,
            LocalDate endDate,
            LocalDate startDate
    );

    boolean existsByEmployeeIdAndStatusInAndStartDateLessThanEqualAndEndDateGreaterThanEqual(
            Integer employeeId,
            List<LeaveStatus> statuses,
            LocalDate endDate,
            LocalDate startDate
    );

    @Query("""
        SELECT COUNT(lr) > 0
        FROM LeaveRequest lr
        WHERE lr.employeeId = :employeeId
          AND lr.status IN :statuses
          AND lr.startDate <= :endDate
          AND lr.endDate >= :startDate
          AND (:excludeId IS NULL OR lr.id <> :excludeId)
    """)
    boolean existsOverlapExcludingCurrent(
            @Param("employeeId") Integer employeeId,
            @Param("statuses") List<LeaveStatus> statuses,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("excludeId") Long excludeId
    );

    @Query("""
        SELECT COUNT(lr)
        FROM LeaveRequest lr
        WHERE lr.managerId = :managerId
          AND lr.status = :status
    """)
    long countByManagerIdAndStatusCustom(
            @Param("managerId") Integer managerId,
            @Param("status") LeaveStatus status
    );

    @Query("""
        SELECT COUNT(lr)
        FROM LeaveRequest lr
        WHERE lr.managerId = :managerId
          AND lr.status = :status
          AND lr.approvalStage = :approvalStage
    """)
    long countByManagerIdAndStatusAndApprovalStageCustom(
            @Param("managerId") Integer managerId,
            @Param("status") LeaveStatus status,
            @Param("approvalStage") LeaveApprovalStage approvalStage
    );
}