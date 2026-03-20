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
import com.metahrms.employee_management.enums.EmployeeStatus;
import com.metahrms.employee_management.enums.RoleInDepartment;

import com.metahrms.employee_management.entity.Employee;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Integer>, JpaSpecificationExecutor<Employee> {

    Optional<Employee> findById(Integer id);

    Optional<Employee> findByUserId(Integer userId);

    long countByDeptId(Integer deptId);

    Optional<Employee> findFirstByDeptIdAndRoleInDept(Integer deptId, RoleInDepartment roleInDept);

    List<Employee> findByDeptIdAndStatus(Integer deptId, EmployeeStatus status);

    // Dashboard queries
    Long countByIsDeletedAndStatus(boolean isDeleted, EmployeeStatus status);

    Long countByIsDeletedAndHireDateBetween(boolean isDeleted, LocalDate startDate, LocalDate endDate);

    Long countByIsDeletedAndStatusAndUpdatedAtBetween(boolean isDeleted, EmployeeStatus status,
                                                       LocalDateTime startDateTime, LocalDateTime endDateTime);

    List<Employee> findByIsDeletedAndStatus(boolean isDeleted, EmployeeStatus status);

    List<Employee> findByIsDeleted(boolean isDeleted);

    void deleteById(Integer id);

    List<Employee> findByDeptId(Integer deptId);
    
    // Lấy danh sách nhân viên sắp xếp theo cấp bậc position (levelOrder)
    @Query("SELECT e FROM Employee e " +
           "LEFT JOIN FETCH e.position p " +
           "WHERE e.deptId = :deptId AND (e.isDeleted = false OR e.isDeleted IS NULL) " +
           "ORDER BY COALESCE(p.levelOrder, 999) ASC, COALESCE(p.sortOrder, 999) ASC, e.fullName ASC")
    List<Employee> findByDeptIdAndIsDeletedFalseOrderByPositionLevel(@Param("deptId") Integer deptId);
}
