package com.metahrms.employee_management.service.payroll;

import com.metahrms.employee_management.dto.request.payroll.UpdateTaxInfoRequest;
import com.metahrms.employee_management.dto.response.payroll.EmployeeTaxInfoDTO;

public interface EmployeeTaxInfoService {
    EmployeeTaxInfoDTO getByEmployee(Integer employeeId);
    EmployeeTaxInfoDTO upsert(Integer employeeId, UpdateTaxInfoRequest request);
}