package com.metahrms.employee_management.repository.Payroll;

import com.metahrms.employee_management.entity.Payroll.Bonus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BonusRepository extends JpaRepository<Bonus, Integer> {

    List<Bonus> findByEmployeeIdAndMonthAndYearAndIsDeletedFalse(
        Integer employeeId, Integer month, Integer year
    );

    List<Bonus> findByMonthAndYearAndIsDeletedFalse(Integer month, Integer year);

    List<Bonus> findByEmployeeIdAndMonthAndYearAndIsApprovedTrueAndIsDeletedFalse(
        Integer employeeId, Integer month, Integer year
    );
}
