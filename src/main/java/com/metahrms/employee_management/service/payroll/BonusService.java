package com.metahrms.employee_management.service.payroll;

import com.metahrms.employee_management.dto.request.payroll.CreateBonusRequest;
import com.metahrms.employee_management.dto.response.payroll.BonusDTO;

import java.util.List;

public interface BonusService {
    BonusDTO create(CreateBonusRequest request);
    void approve(Integer id);
    void delete(Integer id);
    List<BonusDTO> getByPeriod(Integer month, Integer year);
    List<BonusDTO> getByEmployee(Integer employeeId);
}