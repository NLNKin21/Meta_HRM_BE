package com.metahrms.employee_management.service.impl.payroll;

import com.metahrms.employee_management.dto.request.payroll.CreateDeductionRequest;
import com.metahrms.employee_management.dto.response.payroll.DeductionDTO;
import com.metahrms.employee_management.entity.Payroll.Deduction;
import com.metahrms.employee_management.exception.ResourceNotFoundException;
import com.metahrms.employee_management.repository.DepartmentRepository;
import com.metahrms.employee_management.repository.EmployeeRepository;
import com.metahrms.employee_management.repository.Payroll.DeductionRepository;
import com.metahrms.employee_management.service.payroll.DeductionService;
import com.metahrms.employee_management.util.SecurityUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class DeductionServiceImpl implements DeductionService {

    private final DeductionRepository deductionRepository;
    private final EmployeeRepository employeeRepository;
    private final DepartmentRepository departmentRepository;

    @Override
    @Transactional
    public DeductionDTO create(CreateDeductionRequest request) {
        log.info("[DEDUCTION] Creating for employee={}, type={}, amount={}",
                request.getEmployeeId(), request.getDeductionType(), request.getAmount());

        employeeRepository.findByIdAndIsDeletedFalse(request.getEmployeeId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Employee not found: " + request.getEmployeeId()));

        Deduction deduction = Deduction.builder()
                .employeeId(request.getEmployeeId())
                .deductionType(request.getDeductionType().toUpperCase())
                .name(request.getName())
                .amount(request.getAmount())
                .month(request.getMonth())
                .year(request.getYear())
                .reason(request.getReason())
                .isApproved(false)
                .createdBy(SecurityUtils.getCurrentUserId())
                .build();

        return toDTO(deductionRepository.save(deduction));
    }

    @Override
    @Transactional
    public void approve(Integer id) {
        Deduction deduction = deductionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Deduction not found: " + id));
        deduction.setIsApproved(true);
        deduction.setApprovedBy(SecurityUtils.getCurrentUserId());
        deductionRepository.save(deduction);
    }

    @Override
    @Transactional
    public void delete(Integer id) {
        Deduction deduction = deductionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Deduction not found: " + id));

        if (Boolean.TRUE.equals(deduction.getIsApproved())) {
            throw new IllegalStateException("Cannot delete approved deduction.");
        }

        deduction.setIsDeleted(true);
        deductionRepository.save(deduction);
    }

    @Override
    public List<DeductionDTO> getByPeriod(Integer month, Integer year) {
        return deductionRepository.findByMonthAndYearAndIsDeletedFalse(month, year)
                .stream().map(this::toDTO).collect(Collectors.toList());
    }

    @Override
    public List<DeductionDTO> getByEmployee(Integer employeeId) {
        return deductionRepository.findAll().stream()
                .filter(d -> d.getEmployeeId().equals(employeeId) && !Boolean.TRUE.equals(d.getIsDeleted()))
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    private DeductionDTO toDTO(Deduction d) {
        String empName = null;
        String deptName = null;
        try {
            var emp = employeeRepository.findByIdAndIsDeletedFalse(d.getEmployeeId()).orElse(null);
            if (emp != null) {
                empName = emp.getFullName();
                if (emp.getDeptId() != null) {
                    deptName = departmentRepository.findByIdAndIsDeletedFalse(emp.getDeptId())
                            .map(dept -> dept.getDeptName()).orElse(null);
                }
            }
        } catch (Exception ignored) {}

        String typeName = switch (d.getDeductionType()) {
            case "PENALTY"      -> "Phạt vi phạm";
            case "LATE_PENALTY" -> "Phạt đi trễ";
            case "LOAN"         -> "Trả nợ công ty";
            case "DAMAGE"       -> "Bồi thường thiết bị";
            default             -> "Khấu trừ khác";
        };

        return DeductionDTO.builder()
                .id(d.getId())
                .employeeId(d.getEmployeeId())
                .employeeName(empName)
                .deptName(deptName)
                .deductionType(d.getDeductionType())
                .deductionTypeName(typeName)
                .name(d.getName())
                .amount(d.getAmount())
                .month(d.getMonth())
                .year(d.getYear())
                .reason(d.getReason())
                .isApproved(d.getIsApproved())
                .approvedBy(d.getApprovedBy())
                .createdAt(d.getCreatedAt())
                .build();
    }
}