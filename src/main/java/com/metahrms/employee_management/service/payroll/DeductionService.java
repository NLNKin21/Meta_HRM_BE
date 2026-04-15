package com.metahrms.employee_management.service.payroll;

import com.metahrms.employee_management.dto.request.payroll.CreateDeductionRequest;
import com.metahrms.employee_management.dto.response.payroll.DeductionDTO;

import java.util.List;

public interface DeductionService {
    DeductionDTO create(CreateDeductionRequest request);
    void approve(Integer id);
    void delete(Integer id);
    List<DeductionDTO> getByPeriod(Integer month, Integer year);
    List<DeductionDTO> getByEmployee(Integer employeeId);
}