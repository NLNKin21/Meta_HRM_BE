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

    @Query("""
        SELECT e
        FROM Employee e
        WHERE e.status = :status
          AND e.isDeleted = false
    """)
    List<Employee> findAllByStatusAndNotDeleted(@Param("status") EmployeeStatus status);
}