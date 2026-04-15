package com.metahrms.employee_management.repository.Payroll;

import com.metahrms.employee_management.entity.Payroll.Deduction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DeductionRepository extends JpaRepository<Deduction, Integer> {

    List<Deduction> findByEmployeeIdAndMonthAndYearAndIsDeletedFalse(
        Integer employeeId, Integer month, Integer year
    );

    List<Deduction> findByMonthAndYearAndIsDeletedFalse(Integer month, Integer year);

    List<Deduction> findByEmployeeIdAndMonthAndYearAndIsApprovedTrueAndIsDeletedFalse(
        Integer employeeId, Integer month, Integer year
    );
}