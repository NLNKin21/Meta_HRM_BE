package com.metahrms.employee_management.repository.Payroll;

import com.metahrms.employee_management.entity.Payroll.Allowance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface AllowanceRepository extends JpaRepository<Allowance, Integer> {

    List<Allowance> findByEmployeeIdAndIsDeletedFalse(Integer employeeId);

    /**
     * Lấy phụ cấp active của NV tại thời điểm
     * effectiveDate <= date AND (expiryDate IS NULL OR expiryDate >= date)
     */
    @Query("SELECT a FROM Allowance a " +
           "WHERE a.employeeId = :empId " +
           "AND a.isActive = true " +
           "AND a.isDeleted = false " +
           "AND a.effectiveDate <= :date " +
           "AND (a.expiryDate IS NULL OR a.expiryDate >= :date)")
    List<Allowance> findActiveByEmployeeIdAndDate(
        @Param("empId") Integer empId,
        @Param("date") LocalDate date
    );

    /**
     * Lấy phụ cấp tính vào lương đóng BH
     */
    @Query("SELECT a FROM Allowance a " +
           "WHERE a.employeeId = :empId " +
           "AND a.isInsurance = true " +
           "AND a.isActive = true " +
           "AND a.isDeleted = false " +
           "AND a.effectiveDate <= :date " +
           "AND (a.expiryDate IS NULL OR a.expiryDate >= :date)")
    List<Allowance> findInsuranceAllowances(
        @Param("empId") Integer empId,
        @Param("date") LocalDate date
    );
}