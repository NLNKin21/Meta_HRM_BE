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

    // @Query("SELECT DISTINCT e FROM Employee e " +
    //        "LEFT JOIN KpiResults kr ON (e.id = kr.empId AND kr.kpiPeriodId = :kpiPeriodId AND kr.isDeleted = false) " +
    //        "WHERE e.isDeleted = false " +
    //        "AND (:deptId IS NULL OR e.deptId = :deptId) " +
    //        "AND kr.id IS NULL")
    // List<Employee> findEmployeesWithoutKpiResults(
    //     @Param("kpiPeriodId") Integer kpiPeriodId,
    //     @Param("deptId") Integer deptId
    // );

}
