package com.metahrms.employee_management.service.impl.payroll;

import com.metahrms.employee_management.dto.request.payroll.UpdateTaxInfoRequest;
import com.metahrms.employee_management.dto.response.payroll.EmployeeTaxInfoDTO;
import com.metahrms.employee_management.entity.Payroll.EmployeeTaxInfo;
import com.metahrms.employee_management.exception.ResourceNotFoundException;
import com.metahrms.employee_management.repository.EmployeeRepository;
import com.metahrms.employee_management.repository.Payroll.EmployeeTaxInfoRepository;
import com.metahrms.employee_management.service.payroll.EmployeeTaxInfoService;
import com.metahrms.employee_management.util.SecurityUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmployeeTaxInfoServiceImpl implements EmployeeTaxInfoService {

    private final EmployeeTaxInfoRepository taxInfoRepository;
    private final EmployeeRepository employeeRepository;

    @Override
    public EmployeeTaxInfoDTO getByEmployee(Integer employeeId) {
        EmployeeTaxInfo info = taxInfoRepository
                .findByEmployeeIdAndIsDeletedFalse(employeeId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Tax info not found for employee: " + employeeId));
        return toDTO(info);
    }

    @Override
    @Transactional
    public EmployeeTaxInfoDTO upsert(Integer employeeId, UpdateTaxInfoRequest request) {
        log.info("[TAX-INFO] Upsert for employee={}", employeeId);

        employeeRepository.findByIdAndIsDeletedFalse(employeeId)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found: " + employeeId));

        EmployeeTaxInfo info = taxInfoRepository
                .findByEmployeeIdAndIsDeletedFalse(employeeId)
                .orElse(EmployeeTaxInfo.builder().employeeId(employeeId).build());

        // Partial update - chỉ update field != null
        if (request.getTaxCode() != null)
            info.setTaxCode(request.getTaxCode());
        if (request.getNumberOfDependents() != null)
            info.setNumberOfDependents(request.getNumberOfDependents());
        if (request.getSocialInsuranceNo() != null)
            info.setSocialInsuranceNo(request.getSocialInsuranceNo());
        if (request.getSocialInsuranceSalary() != null)
            info.setSocialInsuranceSalary(request.getSocialInsuranceSalary());
        if (request.getBankName() != null)
            info.setBankName(request.getBankName());
        if (request.getBankBranch() != null)
            info.setBankBranch(request.getBankBranch());
        if (request.getBankAccountNumber() != null)
            info.setBankAccountNumber(request.getBankAccountNumber());
        if (request.getBankAccountHolder() != null)
            info.setBankAccountHolder(request.getBankAccountHolder());
        if (request.getNote() != null)
            info.setNote(request.getNote());

        info.setUpdatedBy(SecurityUtils.getCurrentUserId());

        return toDTO(taxInfoRepository.save(info));
    }

    private EmployeeTaxInfoDTO toDTO(EmployeeTaxInfo i) {
        String empName = null;
        try {
            empName = employeeRepository.findByIdAndIsDeletedFalse(i.getEmployeeId())
                    .map(e -> e.getFullName()).orElse(null);
        } catch (Exception ignored) {}

        return EmployeeTaxInfoDTO.builder()
                .id(i.getId())
                .employeeId(i.getEmployeeId())
                .employeeName(empName)
                .taxCode(i.getTaxCode())
                .numberOfDependents(i.getNumberOfDependents())
                .socialInsuranceNo(i.getSocialInsuranceNo())
                .socialInsuranceSalary(i.getSocialInsuranceSalary())
                .bankName(i.getBankName())
                .bankBranch(i.getBankBranch())
                .bankAccountNumber(i.getBankAccountNumber())
                .bankAccountHolder(i.getBankAccountHolder())
                .note(i.getNote())
                .updatedAt(i.getUpdatedAt())
                .build();
    }
}