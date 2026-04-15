package com.metahrms.employee_management.service.payroll;

import com.metahrms.employee_management.dto.request.payroll.CreateAllowanceRequest;
import com.metahrms.employee_management.dto.request.payroll.UpdateAllowanceRequest;
import com.metahrms.employee_management.dto.response.payroll.AllowanceDTO;

import java.util.List;

public interface AllowanceService {
    AllowanceDTO create(CreateAllowanceRequest request);
    AllowanceDTO update(Integer id, UpdateAllowanceRequest request);
    void delete(Integer id);
    List<AllowanceDTO> getByEmployee(Integer employeeId);
    List<AllowanceDTO> getAll(Integer month, Integer year);
}