package com.metahrms.employee_management.service.payroll;

import com.metahrms.employee_management.dto.request.payroll.UpdateConfigRequest;
import com.metahrms.employee_management.dto.response.payroll.PayrollConfigDTO;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public interface PayrollConfigService {

    List<PayrollConfigDTO> getAll();

    List<PayrollConfigDTO> getByGroup(String group);

    PayrollConfigDTO getByKey(String key);

    PayrollConfigDTO update(String key, UpdateConfigRequest request);

    // Helper: lấy giá trị config dạng BigDecimal
    BigDecimal getValue(String key);

    // Helper: lấy toàn bộ config thành Map (dùng trong tính lương)
    Map<String, BigDecimal> getAllAsMap();
}