package com.metahrms.employee_management.repository.Payroll;

import com.metahrms.employee_management.entity.Payroll.EmployeeTaxInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EmployeeTaxInfoRepository extends JpaRepository<EmployeeTaxInfo, Integer> {

    Optional<EmployeeTaxInfo> findByEmployeeIdAndIsDeletedFalse(Integer employeeId);

    boolean existsByEmployeeIdAndIsDeletedFalse(Integer employeeId);
}