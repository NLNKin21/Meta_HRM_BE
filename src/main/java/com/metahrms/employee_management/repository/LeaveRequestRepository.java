package com.metahrms.employee_management.repository;

import com.metahrms.employee_management.entity.Leave.LeaveRequest;
import com.metahrms.employee_management.enums.Leave.LeaveApprovalStage;
import com.metahrms.employee_management.enums.Leave.LeaveStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalDateTime;
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
        ORDER BY lr.createdAt DESC
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
        ORDER BY lr.createdAt DESC
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

    @Query("""
        SELECT lr
        FROM LeaveRequest lr
        JOIN FETCH lr.leaveType
        WHERE lr.managerId = :managerId
          AND lr.status IN :statuses
          AND (
                (lr.status = com.metahrms.employee_management.enums.Leave.LeaveStatus.APPROVED
                    AND lr.approvedAt IS NOT NULL
                    AND lr.approvedAt >= :startDateTime
                    AND lr.approvedAt <= :endDateTime)
             OR (lr.status = com.metahrms.employee_management.enums.Leave.LeaveStatus.REJECTED
                    AND lr.updatedAt IS NOT NULL
                    AND lr.updatedAt >= :startDateTime
                    AND lr.updatedAt <= :endDateTime)
          )
        ORDER BY COALESCE(lr.approvedAt, lr.updatedAt, lr.createdAt) DESC
    """)
    List<LeaveRequest> findManagerHistoryByStatusesAndProcessedAtRangeWithLeaveType(
            @Param("managerId") Integer managerId,
            @Param("statuses") List<LeaveStatus> statuses,
            @Param("startDateTime") LocalDateTime startDateTime,
            @Param("endDateTime") LocalDateTime endDateTime
    );

    @Query("""
        SELECT lr
        FROM LeaveRequest lr
        JOIN FETCH lr.leaveType
        WHERE lr.hrId = :hrId
          AND lr.status IN :statuses
          AND (
                (lr.status = com.metahrms.employee_management.enums.Leave.LeaveStatus.APPROVED
                    AND lr.approvedAt IS NOT NULL
                    AND lr.approvedAt >= :startDateTime
                    AND lr.approvedAt <= :endDateTime)
             OR (lr.status = com.metahrms.employee_management.enums.Leave.LeaveStatus.REJECTED
                    AND lr.updatedAt IS NOT NULL
                    AND lr.updatedAt >= :startDateTime
                    AND lr.updatedAt <= :endDateTime)
          )
        ORDER BY COALESCE(lr.approvedAt, lr.updatedAt, lr.createdAt) DESC
    """)
     List<LeaveRequest> findHrHistoryByStatusesAndProcessedAtRangeWithLeaveType(
            @Param("hrId") Integer hrId,
            @Param("statuses") List<LeaveStatus> statuses,
            @Param("startDateTime") LocalDateTime startDateTime,
            @Param("endDateTime") LocalDateTime endDateTime
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

    @Query("""
        SELECT COUNT(lr)
        FROM LeaveRequest lr
        WHERE lr.managerId = :managerId
          AND lr.status = :status
          AND lr.approvedAt IS NOT NULL
          AND lr.approvedAt >= :startDateTime
          AND lr.approvedAt <= :endDateTime
    """)
    long countByManagerIdAndStatusAndApprovedAtBetween(
            @Param("managerId") Integer managerId,
            @Param("status") LeaveStatus status,
            @Param("startDateTime") LocalDateTime startDateTime,
            @Param("endDateTime") LocalDateTime endDateTime
    );

    @Query("""
        SELECT COUNT(lr)
        FROM LeaveRequest lr
        WHERE lr.managerId = :managerId
          AND lr.status = :status
          AND lr.updatedAt IS NOT NULL
          AND lr.updatedAt >= :startDateTime
          AND lr.updatedAt <= :endDateTime
    """)
    long countByManagerIdAndStatusAndUpdatedAtBetween(
            @Param("managerId") Integer managerId,
            @Param("status") LeaveStatus status,
            @Param("startDateTime") LocalDateTime startDateTime,
            @Param("endDateTime") LocalDateTime endDateTime
    );

    @Query("""
        SELECT COUNT(lr)
        FROM LeaveRequest lr
        WHERE lr.managerId = :managerId
          AND lr.status = :status
          AND lr.createdAt BETWEEN :start AND :end
    """)
    long countByManagerIdAndStatusAndCreatedAtBetween(
            @Param("managerId") Integer managerId,
            @Param("status") LeaveStatus status,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end
    );

    @Query("""
        SELECT COUNT(lr)
        FROM LeaveRequest lr
        WHERE lr.managerId = :managerId
          AND lr.status = :status
          AND lr.startDate <= :endDate
          AND lr.endDate >= :startDate
    """)
    long countByManagerIdAndStatusAndDateRange(
            @Param("managerId") Integer managerId,
            @Param("status") LeaveStatus status,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );

    @Query("""
        SELECT COUNT(DISTINCT lr.employeeId)
        FROM LeaveRequest lr
        WHERE lr.status = com.metahrms.employee_management.enums.Leave.LeaveStatus.APPROVED
          AND lr.startDate <= CURRENT_DATE
          AND lr.endDate >= CURRENT_DATE
    """)
    long countEmployeesOnLeaveToday();

    @Query("""
        SELECT COUNT(lr)
        FROM LeaveRequest lr
        WHERE lr.hrId = :hrId
          AND lr.status = com.metahrms.employee_management.enums.Leave.LeaveStatus.PENDING
          AND lr.approvalStage = com.metahrms.employee_management.enums.Leave.LeaveApprovalStage.WAITING_HR
    """)
    long countPendingRequestsForHr(@Param("hrId") Integer hrId);


    /**
         * Lấy leave requests đã approved của NV trong khoảng thời gian
         * Dùng trong tính lương để tính ngày nghỉ có/không lương
         */
        @Query("SELECT lr FROM LeaveRequest lr " +
        "WHERE lr.employeeId = :empId " +
        "AND lr.status = 'APPROVED' " +
        "AND lr.finalApproved = true " +
        "AND lr.startDate <= :endDate " +
        "AND lr.endDate >= :startDate")
        List<LeaveRequest> findApprovedByEmployeeAndPeriod(
        @Param("empId") Integer empId,
        @Param("startDate") LocalDate startDate,
        @Param("endDate") LocalDate endDate
        );
}