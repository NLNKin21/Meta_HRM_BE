package com.metahrms.employee_management.repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.metahrms.employee_management.entity.Employee;
import com.metahrms.employee_management.enums.ContractStatus;
import com.metahrms.employee_management.enums.EmployeeStatus;
import com.metahrms.employee_management.enums.RoleInDepartment;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Integer>, JpaSpecificationExecutor<Employee> {

    Optional<Employee> findById(Integer id);

    Optional<Employee> findByUserId(Integer userId);

    long countByDeptId(Integer deptId);

    Optional<Employee> findFirstByDeptIdAndRoleInDept(Integer deptId, RoleInDepartment roleInDept);

    List<Employee> findByDeptIdAndStatus(Integer deptId, EmployeeStatus status);

    List<Employee> findByDeptId(Integer deptId);

    void deleteById(Integer id);

    @Query("""
        select e
        from Employee e
        left join fetch e.position
        where e.id = :id
        """)
    Optional<Employee> findByIdWithPosition(@Param("id") Integer id);

    // giữ method cũ để LeaveRequestServiceImpl không đỏ
    Optional<Employee> findFirstByDeptIdAndRoleInDeptAndStatusAndIsDeletedFalse(
            Integer deptId,
            RoleInDepartment roleInDept,
            EmployeeStatus status
    );

    // thêm cho contract notification
    List<Employee> findByDeptIdAndRoleInDeptAndStatusAndIsDeletedFalse(
            Integer deptId,
            RoleInDepartment roleInDept,
            EmployeeStatus status
    );

    // =========================
    // Leave approval support
    // =========================

    @Query("""
        SELECT e
        FROM Employee e
        WHERE e.deptId = :deptId
          AND e.status = :status
          AND e.isDeleted = false
          AND e.roleInDept = 'HEAD'
    """)
    Optional<Employee> findActiveHeadByDeptId(
            @Param("deptId") Integer deptId,
            @Param("status") EmployeeStatus status
    );

    @Query("""
        SELECT e
        FROM Employee e
        WHERE e.deptId = :deptId
          AND e.isDeleted = false
    """)
    List<Employee> findAllActiveByDeptId(@Param("deptId") Integer deptId);

    @Query("""
        SELECT e
        FROM Employee e
        WHERE e.id IN :employeeIds
          AND e.isDeleted = false
    """)
    List<Employee> findAllByIdInAndNotDeleted(@Param("employeeIds") List<Integer> employeeIds);

    // =========================
    // Dashboard / statistics
    // =========================

    Long countByIsDeletedAndCreatedAtBetween(
            boolean isDeleted,
            LocalDateTime startDateTime,
            LocalDateTime endDateTime
    );

    Long countByIsDeletedAndStatusAndUpdatedAtBetween(
            boolean isDeleted,
            EmployeeStatus status,
            LocalDateTime startDateTime,
            LocalDateTime endDateTime
    );

    List<Employee> findByIsDeletedAndStatus(boolean isDeleted, EmployeeStatus status);

    List<Employee> findByIsDeleted(boolean isDeleted);

    @Query("""
        SELECT e
        FROM Employee e
        WHERE e.deptId = :deptId
          AND e.isDeleted = false
        ORDER BY e.position.levelOrder ASC NULLS LAST
    """)
    List<Employee> findByDeptIdAndIsDeletedFalseOrderByPositionLevel(@Param("deptId") Integer deptId);

    @Query("""
        SELECT e
        FROM Employee e
        WHERE e.deptId = :departmentId
          AND e.status = :status
          AND e.isDeleted = false
        ORDER BY e.position.levelOrder ASC NULLS LAST
    """)
    List<Employee> findActiveByDepartmentId(
            @Param("departmentId") Integer departmentId,
            @Param("status") EmployeeStatus status
    );

    @Query("""
        SELECT COUNT(e)
        FROM Employee e
        WHERE e.deptId = :departmentId
          AND e.status = :status
          AND e.isDeleted = false
    """)
    Long countByDepartmentIdAndStatus(
            @Param("departmentId") Integer departmentId,
            @Param("status") EmployeeStatus status
    );

    @Query("""
        SELECT e
        FROM Employee e
        WHERE e.deptId = :deptId
          AND e.roleInDept = :roleInDept
          AND e.status = :status
          AND e.isDeleted = false
    """)
    Optional<Employee> findDepartmentHead(
            @Param("deptId") Integer deptId,
            @Param("roleInDept") RoleInDepartment roleInDept,
            @Param("status") EmployeeStatus status
    );

    @Query("SELECT e FROM Employee e " +
       "LEFT JOIN FETCH e.position " +
       "LEFT JOIN FETCH e.shift " +
       "WHERE e.deptId = :deptId AND e.isDeleted = false")
        List<Employee> findByDeptIdWithDetails(@Param("deptId") Integer deptId);

    @Query("""
        SELECT e
        FROM Employee e
        WHERE e.status = :status
          AND e.isDeleted = false
    """)
    List<Employee> findAllByStatusAndNotDeleted(@Param("status") EmployeeStatus status);

    /**
     * Tìm Employee theo userId từ JWT
     * SecurityUtils.getCurrentUserId() → userId → Employee
     *
     * Dùng trong:
     * - WorkLocationServiceImpl (getCurrentUserId → createdBy)
     * - ShiftServiceImpl (getCurrentUserId → createdBy)
     * - Bước 2: /me/* endpoints
     */
    Optional<Employee> findByUserIdAndIsDeletedFalse(Integer userId);

    /**
     * Tìm theo ID và chưa bị xoá
     * Dùng trong ShiftServiceImpl.assignShiftToEmployee()
     */
    Optional<Employee> findByIdAndIsDeletedFalse(Integer id);

    /**
     * Lấy employees theo danh sách IDs (chưa xoá)
     * Dùng trong ShiftServiceImpl.assignShiftToEmployees() - bulk assign
     */
    List<Employee> findByIdInAndIsDeletedFalse(List<Integer> ids);

    /**
     * Lấy employees đang dùng shift với thông tin position
     * Dùng trong ShiftServiceImpl.getEmployeesByShift()
     *
     * JOIN FETCH position để tránh N+1 query
     */
    @Query("SELECT e FROM Employee e " +
           "LEFT JOIN FETCH e.position " +
           "WHERE e.shift.id = :shiftId " +
           "AND e.isDeleted = false " +
           "ORDER BY e.fullName ASC")
    List<Employee> findByShiftIdWithDetails(@Param("shiftId") Integer shiftId);

    /**
     * Lấy employees theo department (chưa xoá)
     * Dùng trong Bước 3: quản lý theo phòng ban
     */
    List<Employee> findByDeptIdAndIsDeletedFalse(Integer deptId);

    /**
     * Đếm employees theo department
     * Dùng trong Bước 3: summary report
     */
    long countByDeptIdAndIsDeletedFalse(Integer deptId);

     @Query("""
        select e
        from Employee e
        left join fetch e.position
        where e.userId = :userId
        and e.isDeleted = false
        """)
    Optional<Employee> findByUserIdWithPosition(@Param("userId") Integer userId);

    /**
     * Lấy employee chưa có hoặc chỉ có contract EXPIRED/TERMINATED
     * Dùng cho dropdown tạo hợp đồng mới
     */
    @Query("""
        SELECT e
        FROM Employee e
        WHERE e.isDeleted = false
        AND e.status = :employeeStatus
        AND e.id NOT IN (
            SELECT c.empId
            FROM Contract c
            WHERE c.isDeleted = false
                AND c.status IN :contractStatuses
                AND (c.endDate IS NULL OR c.endDate >= :today)
        )
        ORDER BY e.fullName ASC
    """)
    List<Employee> findEmployeesAvailableForContract(
            @Param("employeeStatus") EmployeeStatus employeeStatus,
            @Param("contractStatuses") List<ContractStatus> contractStatuses,
            @Param("today") LocalDate today
    );
}