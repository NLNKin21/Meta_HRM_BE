package com.metahrms.employee_management.repository.Payroll;

import com.metahrms.employee_management.entity.Payroll.PayslipDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PayslipDetailRepository extends JpaRepository<PayslipDetail, Integer> {

    List<PayslipDetail> findByPayslipIdAndIsDeletedFalseOrderBySortOrder(Integer payslipId);

    void deleteByPayslipId(Integer payslipId);

    List<PayslipDetail> findByPayslipIdAndItemTypeAndIsDeletedFalseOrderBySortOrder(
        Integer payslipId, String itemType
    );
}