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

    // =========================
    // Leave approval support
    // =========================

    Optional<Employee> findFirstByDeptIdAndRoleInDeptAndStatusAndIsDeletedFalse(
            Integer deptId,
            RoleInDepartment roleInDept,
            EmployeeStatus status
    );

    /**
     * Tìm trưởng phòng đang ACTIVE và chưa bị xóa theo deptId
     */
    @Query("""
        SELECT e
        FROM Employee e
        WHERE e.deptId = :deptId
          AND e.roleInDept = :roleInDept
          AND e.status = :status
          AND e.isDeleted = false
        ORDER BY e.id ASC
    """)
    List<Employee> findHeadsByDeptIdAndStatus(
            @Param("deptId") Integer deptId,
            @Param("roleInDept") RoleInDepartment roleInDept,
            @Param("status") EmployeeStatus status
    );

    /**
     * Lấy 1 trưởng phòng mặc định của phòng ban theo deptId
     */
    default Optional<Employee> findActiveDepartmentHead(Integer deptId) {
        List<Employee> heads = findHeadsByDeptIdAndStatus(
                deptId,
                RoleInDepartment.HEAD,
                EmployeeStatus.ACTIVE
        );
        return heads.stream().findFirst();
    }

    /**
     * Tìm trưởng phòng theo tên phòng ban.
     *
     * Lưu ý:
     * - Query này dùng native SQL vì Employee của bạn đang lưu deptId trực tiếp.
     * - Giả định bảng departments có cột id và name.
     * - Nếu bảng departments của bạn không phải cột "name" mà là "dept_name"
     *   thì đổi d.name thành d.dept_name ở query bên dưới.
     */
    @Query(value = """
        SELECT e.*
        FROM employees e
        JOIN departments d ON e.dept_id = d.id
        WHERE UPPER(d.name) = UPPER(:departmentName)
          AND e.role_in_dept = :roleInDept
          AND e.status = :status
          AND e.is_deleted = false
        ORDER BY e.id ASC
        LIMIT 1
    """, nativeQuery = true)
    Optional<Employee> findFirstActiveHeadByDepartmentNameNative(
            @Param("departmentName") String departmentName,
            @Param("roleInDept") String roleInDept,
            @Param("status") String status
    );

    /**
     * Lấy trưởng phòng HR đang ACTIVE và chưa bị xóa
     */
    default Optional<Employee> findActiveHrDepartmentHead() {
        return findFirstActiveHeadByDepartmentNameNative(
                "HR",
                RoleInDepartment.HEAD.name(),
                EmployeeStatus.ACTIVE.name()
        );
    }

    /**
     * Nếu muốn lấy toàn bộ nhân viên đang làm ở phòng HR
     */
    @Query(value = """
        SELECT e.*
        FROM employees e
        JOIN departments d ON e.dept_id = d.id
        WHERE UPPER(d.name) = UPPER(:departmentName)
          AND e.status = :status
          AND e.is_deleted = false
        ORDER BY e.id ASC
    """, nativeQuery = true)
    List<Employee> findActiveEmployeesByDepartmentNameNative(
            @Param("departmentName") String departmentName,
            @Param("status") String status
    );

    /**
     * Lấy danh sách nhân viên ACTIVE của phòng HR
     */
    default List<Employee> findActiveEmployeesInHrDepartment() {
        return findActiveEmployeesByDepartmentNameNative(
                "HR",
                EmployeeStatus.ACTIVE.name()
        );
    }

    // =========================
    // Dashboard queries
    // =========================

    Long countByIsDeletedAndStatus(boolean isDeleted, EmployeeStatus status);

    Long countByIsDeletedAndHireDateBetween(boolean isDeleted, LocalDate startDate, LocalDate endDate);

    Long countByIsDeletedAndStatusAndUpdatedAtBetween(
            boolean isDeleted,
            EmployeeStatus status,
            LocalDateTime startDateTime,
            LocalDateTime endDateTime
    );

    List<Employee> findByIsDeletedAndStatus(boolean isDeleted, EmployeeStatus status);

    List<Employee> findByIsDeleted(boolean isDeleted);

    /**
     * Lấy nhân viên theo department (chưa xóa), sắp xếp theo position level
     */
    @Query("""
        SELECT e
        FROM Employee e
        WHERE e.deptId = :deptId
          AND e.isDeleted = false
        ORDER BY e.position.levelOrder ASC NULLS LAST
    """)
    List<Employee> findByDeptIdAndIsDeletedFalseOrderByPositionLevel(@Param("deptId") Integer deptId);

    /**
     * Lấy nhân viên ACTIVE theo department
     */
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

    /**
     * Đếm nhân viên theo department và status
     */
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

    /**
     * Lấy trưởng phòng đang ACTIVE và chưa bị xóa của 1 phòng ban
     */
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

    /**
     * Lấy toàn bộ nhân viên đang ACTIVE và chưa bị xóa
     * Dùng để sync leave balance cho toàn hệ thống, bao gồm cả HEAD / MANAGER / STAFF
     */
    @Query("""
        SELECT e
        FROM Employee e
        WHERE e.status = :status
          AND e.isDeleted = false
    """)
    List<Employee> findAllByStatusAndNotDeleted(@Param("status") EmployeeStatus status);
}