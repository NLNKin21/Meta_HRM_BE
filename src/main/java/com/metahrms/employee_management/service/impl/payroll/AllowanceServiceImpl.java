package com.metahrms.employee_management.service.impl.payroll;

import com.metahrms.employee_management.dto.request.payroll.CreateAllowanceRequest;
import com.metahrms.employee_management.dto.request.payroll.UpdateAllowanceRequest;
import com.metahrms.employee_management.dto.response.payroll.AllowanceDTO;
import com.metahrms.employee_management.entity.Payroll.Allowance;
import com.metahrms.employee_management.exception.ResourceNotFoundException;
import com.metahrms.employee_management.repository.EmployeeRepository;
import com.metahrms.employee_management.repository.Payroll.AllowanceRepository;
import com.metahrms.employee_management.service.payroll.AllowanceService;
import com.metahrms.employee_management.util.SecurityUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class AllowanceServiceImpl implements AllowanceService {

    private final AllowanceRepository allowanceRepository;
    private final EmployeeRepository employeeRepository;

    @Override
    @Transactional
    public AllowanceDTO create(CreateAllowanceRequest request) {
        log.info("[ALLOWANCE] Creating for employee={}, type={}", 
            request.getEmployeeId(), request.getAllowanceType());

        // Validate employee exists
        employeeRepository.findByIdAndIsDeletedFalse(request.getEmployeeId())
                .orElseThrow(() -> new ResourceNotFoundException(
                    "Employee not found: " + request.getEmployeeId()));

        Allowance allowance = Allowance.builder()
                .employeeId(request.getEmployeeId())
                .allowanceType(request.getAllowanceType().toUpperCase())
                .name(request.getName())
                .amount(request.getAmount())
                .isTaxable(request.getIsTaxable() != null ? request.getIsTaxable() : true)
                .isInsurance(request.getIsInsurance() != null ? request.getIsInsurance() : false)
                .effectiveDate(request.getEffectiveDate())
                .expiryDate(request.getExpiryDate())
                .isActive(true)
                .note(request.getNote())
                .createdBy(SecurityUtils.getCurrentUserId())
                .build();

        return toDTO(allowanceRepository.save(allowance));
    }

    @Override
    @Transactional
    public AllowanceDTO update(Integer id, UpdateAllowanceRequest request) {
        Allowance allowance = allowanceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Allowance not found: " + id));

        if (request.getName() != null)          allowance.setName(request.getName());
        if (request.getAmount() != null)         allowance.setAmount(request.getAmount());
        if (request.getIsTaxable() != null)      allowance.setIsTaxable(request.getIsTaxable());
        if (request.getIsInsurance() != null)    allowance.setIsInsurance(request.getIsInsurance());
        if (request.getEffectiveDate() != null)  allowance.setEffectiveDate(request.getEffectiveDate());
        if (request.getExpiryDate() != null)     allowance.setExpiryDate(request.getExpiryDate());
        if (request.getIsActive() != null)       allowance.setIsActive(request.getIsActive());
        if (request.getNote() != null)           allowance.setNote(request.getNote());

        return toDTO(allowanceRepository.save(allowance));
    }

    @Override
    @Transactional
    public void delete(Integer id) {
        Allowance allowance = allowanceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Allowance not found: " + id));
        allowance.setIsDeleted(true);
        allowance.setIsActive(false);
        allowanceRepository.save(allowance);
    }

    @Override
    public List<AllowanceDTO> getByEmployee(Integer employeeId) {
        return allowanceRepository.findByEmployeeIdAndIsDeletedFalse(employeeId)
                .stream().map(this::toDTO).collect(Collectors.toList());
    }

    @Override
    public List<AllowanceDTO> getAll(Integer month, Integer year) {
        // Lấy active tại ngày đầu tháng
        LocalDate date = LocalDate.of(year, month, 1);
        // Lấy tất cả không filter date nếu muốn xem toàn bộ
        return allowanceRepository.findAll().stream()
                .filter(a -> !Boolean.TRUE.equals(a.getIsDeleted()))
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    private AllowanceDTO toDTO(Allowance a) {
        String empName = null;
        try {
            if (a.getEmployee() != null) {
                empName = a.getEmployee().getFullName();
            } else {
                empName = employeeRepository.findByIdAndIsDeletedFalse(a.getEmployeeId())
                        .map(e -> e.getFullName()).orElse(null);
            }
        } catch (Exception ignored) {}

        String typeName = switch (a.getAllowanceType()) {
            case "MEAL"       -> "Ăn trưa";
            case "TRANSPORT"  -> "Xăng xe";
            case "PHONE"      -> "Điện thoại";
            case "HOUSING"    -> "Nhà ở";
            case "POSITION"   -> "Chức vụ";
            case "TOXIC"      -> "Độc hại";
            case "ATTENDANCE" -> "Chuyên cần";
            default           -> "Khác";
        };

        return AllowanceDTO.builder()
                .id(a.getId())
                .employeeId(a.getEmployeeId())
                .employeeName(empName)
                .allowanceType(a.getAllowanceType())
                .allowanceTypeName(typeName)
                .name(a.getName())
                .amount(a.getAmount())
                .isTaxable(a.getIsTaxable())
                .isInsurance(a.getIsInsurance())
                .effectiveDate(a.getEffectiveDate())
                .expiryDate(a.getExpiryDate())
                .isActive(a.getIsActive())
                .note(a.getNote())
                .createdAt(a.getCreatedAt())
                .build();
    }
}