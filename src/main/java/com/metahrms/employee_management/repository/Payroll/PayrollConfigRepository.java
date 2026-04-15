package com.metahrms.employee_management.repository.Payroll;

import com.metahrms.employee_management.entity.Payroll.PayrollConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PayrollConfigRepository extends JpaRepository<PayrollConfig, Integer> {

    Optional<PayrollConfig> findByConfigKeyAndIsDeletedFalse(String configKey);

    List<PayrollConfig> findByConfigGroupAndIsDeletedFalse(String configGroup);

    List<PayrollConfig> findByIsDeletedFalseAndIsActiveTrue();

    boolean existsByConfigKeyAndIsDeletedFalse(String configKey);
}
