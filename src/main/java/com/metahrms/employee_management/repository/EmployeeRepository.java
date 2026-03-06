package com.metahrms.employee_management.repository;

<<<<<<< HEAD
import com.metahrms.employee_management.entity.Employee;
import com.metahrms.employee_management.enums.EmployeeStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
=======
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

>>>>>>> 8c6be7d9d227d6c9e9aae8bb6ebeb4808ead22d3
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
<<<<<<< HEAD

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long>, JpaSpecificationExecutor<Employee> {

    Optional<Employee> findByIdAndIsDeletedFalse(Long id);

    Optional<Employee> findByEmployeeCodeAndIsDeletedFalse(String employeeCode);

    Optional<Employee> findByEmailAndIsDeletedFalse(String email);

    Page<Employee> findByIsDeletedFalse(Pageable pageable);

    List<Employee> findByDepartmentIdAndIsDeletedFalse(Long departmentId);

    List<Employee> findByStatusAndIsDeletedFalse(EmployeeStatus status);

    List<Employee> findByManagerIdAndIsDeletedFalse(Long managerId);

    boolean existsByEmail(String email);

    boolean existsByEmployeeCode(String employeeCode);

    // Tìm kiếm nhân viên
    @Query("SELECT e FROM Employee e WHERE e.isDeleted = false AND " +
           "(LOWER(e.fullName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(e.email) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(e.employeeCode) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    Page<Employee> searchByKeyword(@Param("keyword") String keyword, Pageable pageable);

    // Thống kê theo status
    @Query("SELECT e.status, COUNT(e) FROM Employee e WHERE e.isDeleted = false GROUP BY e.status")
    List<Object[]> countByStatus();

    // Thống kê theo phòng ban
    @Query("SELECT e.department.deptName, COUNT(e) FROM Employee e " +
           "WHERE e.isDeleted = false AND e.department IS NOT NULL GROUP BY e.department.deptName")
    List<Object[]> countByDepartment();

    // Nhân viên sắp sinh nhật
    @Query("SELECT e FROM Employee e WHERE e.isDeleted = false AND " +
           "MONTH(e.dateOfBirth) = :month AND DAY(e.dateOfBirth) BETWEEN :startDay AND :endDay")
    List<Employee> findUpcomingBirthdays(@Param("month") int month, 
                                         @Param("startDay") int startDay, 
                                         @Param("endDay") int endDay);

    // Nhân viên mới trong tháng
    @Query("SELECT e FROM Employee e WHERE e.isDeleted = false AND " +
           "e.hireDate BETWEEN :startDate AND :endDate")
    List<Employee> findNewEmployeesInPeriod(@Param("startDate") LocalDate startDate, 
                                            @Param("endDate") LocalDate endDate);

    // Generate employee code
    @Query("SELECT MAX(e.employeeCode) FROM Employee e WHERE e.employeeCode LIKE :prefix%")
    String findMaxEmployeeCode(@Param("prefix") String prefix);
}
=======
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
>>>>>>> 8c6be7d9d227d6c9e9aae8bb6ebeb4808ead22d3
