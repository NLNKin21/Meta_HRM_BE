package com.metahrms.employee_management.repository.Payroll;

import com.metahrms.employee_management.entity.Payroll.Payslip;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PayslipRepository extends JpaRepository<Payslip, Integer> {

    Optional<Payslip> findByEmployeeIdAndMonthAndYearAndIsDeletedFalse(
        Integer employeeId, Integer month, Integer year
    );

    List<Payslip> findByMonthAndYearAndIsDeletedFalse(Integer month, Integer year);

    List<Payslip> findByMonthAndYearAndStatusAndIsDeletedFalse(
        Integer month, Integer year, String status
    );

    boolean existsByEmployeeIdAndMonthAndYearAndIsDeletedFalse(
        Integer employeeId, Integer month, Integer year
    );

    @Query("SELECT COUNT(p) FROM Payslip p " +
           "WHERE p.month = :month AND p.year = :year " +
           "AND p.status = :status AND p.isDeleted = false")
    long countByMonthAndYearAndStatus(
        @Param("month") Integer month,
        @Param("year") Integer year,
        @Param("status") String status
    );

    /**
     * Lấy payslips của 1 NV, sắp mới nhất trước
     */
    @Query("SELECT p FROM Payslip p " +
           "WHERE p.employeeId = :empId " +
           "AND p.isDeleted = false " +
           "AND p.status IN ('APPROVED', 'PAID') " +
           "ORDER BY p.year DESC, p.month DESC")
    List<Payslip> findApprovedByEmployeeId(@Param("empId") Integer empId);

    /**
     * Payslip mới nhất của NV (đã duyệt/trả)
     */
    @Query("SELECT p FROM Payslip p " +
           "WHERE p.employeeId = :empId " +
           "AND p.isDeleted = false " +
           "AND p.status IN ('APPROVED', 'PAID') " +
           "ORDER BY p.year DESC, p.month DESC " +
           "LIMIT 1")
    Optional<Payslip> findLatestApprovedByEmployeeId(@Param("empId") Integer empId);

    /**
     * Tổng lương NET toàn công ty trong tháng
     */
    @Query("SELECT COALESCE(SUM(p.netSalary), 0) FROM Payslip p " +
           "WHERE p.month = :month AND p.year = :year AND p.isDeleted = false")
    java.math.BigDecimal sumNetSalaryByMonthAndYear(
        @Param("month") Integer month, @Param("year") Integer year
    );

    /**
     * Tổng chi phí công ty trong tháng
     */
    @Query("SELECT COALESCE(SUM(p.totalCompanyCost), 0) FROM Payslip p " +
           "WHERE p.month = :month AND p.year = :year AND p.isDeleted = false")
    java.math.BigDecimal sumCompanyCostByMonthAndYear(
        @Param("month") Integer month, @Param("year") Integer year
    );
}