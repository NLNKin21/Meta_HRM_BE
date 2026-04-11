package com.metahrms.employee_management.repository.Attendance;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.metahrms.employee_management.entity.Attendance.EmployeeFace;

import java.util.List;
import java.util.Optional;

/**
 * Repository cho EmployeeFace entity
 */
@Repository
public interface EmployeeFaceRepository extends JpaRepository<EmployeeFace, Integer> {
    
    /**
     * Tìm tất cả faces của một employee (active)
     */
    List<EmployeeFace> findByEmployeeIdAndIsActiveTrue(Integer employeeId);
    
    /**
     * Tìm face chính của employee
     */
    Optional<EmployeeFace> findByEmployeeIdAndIsPrimaryTrueAndIsActiveTrue(Integer employeeId);
    
    /**
     * Tìm tất cả faces (cả active và inactive)
     */
    List<EmployeeFace> findByEmployeeId(Integer employeeId);
    
    /**
     * Đếm số lượng faces của employee
     */
    @Query("SELECT COUNT(ef) FROM EmployeeFace ef WHERE ef.employee.id = :employeeId AND ef.isActive = true")
    long countActiveByEmployeeId(@Param("employeeId") Integer employeeId);
    
    /**
     * Set tất cả faces của employee thành không primary
     */
    @Modifying
    @Query("UPDATE EmployeeFace ef SET ef.isPrimary = false WHERE ef.employee.id = :employeeId")
    void unsetAllPrimaryByEmployeeId(@Param("employeeId") Integer employeeId);
    
    /**
     * Xóa mềm (set isActive = false)
     */
    @Modifying
    @Query("UPDATE EmployeeFace ef SET ef.isActive = false WHERE ef.id = :id")
    void softDelete(@Param("id") Integer id);
    
    /**
     * Kiểm tra employee đã có face chưa
     */
    boolean existsByEmployeeIdAndIsActiveTrue(Integer employeeId);
}