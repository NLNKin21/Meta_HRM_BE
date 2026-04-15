package com.metahrms.employee_management.service.impl.payroll;

import com.metahrms.employee_management.dto.request.payroll.CreateBonusRequest;
import com.metahrms.employee_management.dto.response.payroll.BonusDTO;
import com.metahrms.employee_management.entity.Payroll.Bonus;
import com.metahrms.employee_management.exception.ResourceNotFoundException;
import com.metahrms.employee_management.repository.DepartmentRepository;
import com.metahrms.employee_management.repository.EmployeeRepository;
import com.metahrms.employee_management.repository.Payroll.BonusRepository;
import com.metahrms.employee_management.service.payroll.BonusService;
import com.metahrms.employee_management.util.SecurityUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class BonusServiceImpl implements BonusService {

    private final BonusRepository bonusRepository;
    private final EmployeeRepository employeeRepository;
    private final DepartmentRepository departmentRepository;

    @Override
    @Transactional
    public BonusDTO create(CreateBonusRequest request) {
        log.info("[BONUS] Creating for employee={}, type={}, amount={}",
                request.getEmployeeId(), request.getBonusType(), request.getAmount());

        employeeRepository.findByIdAndIsDeletedFalse(request.getEmployeeId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Employee not found: " + request.getEmployeeId()));

        Bonus bonus = Bonus.builder()
                .employeeId(request.getEmployeeId())
                .bonusType(request.getBonusType().toUpperCase())
                .name(request.getName())
                .amount(request.getAmount())
                .isTaxable(request.getIsTaxable() != null ? request.getIsTaxable() : true)
                .month(request.getMonth())
                .year(request.getYear())
                .reason(request.getReason())
                .isApproved(false)
                .createdBy(SecurityUtils.getCurrentUserId())
                .build();

        return toDTO(bonusRepository.save(bonus));
    }

    @Override
    @Transactional
    public void approve(Integer id) {
        Bonus bonus = bonusRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Bonus not found: " + id));
        bonus.setIsApproved(true);
        bonus.setApprovedBy(SecurityUtils.getCurrentUserId());
        bonus.setApprovedAt(LocalDateTime.now());
        bonusRepository.save(bonus);
        log.info("[BONUS] Approved id={}", id);
    }

    @Override
    @Transactional
    public void delete(Integer id) {
        Bonus bonus = bonusRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Bonus not found: " + id));

        if (Boolean.TRUE.equals(bonus.getIsApproved())) {
            throw new IllegalStateException("Cannot delete approved bonus. Please contact admin.");
        }

        bonus.setIsDeleted(true);
        bonusRepository.save(bonus);
    }

    @Override
    public List<BonusDTO> getByPeriod(Integer month, Integer year) {
        return bonusRepository.findByMonthAndYearAndIsDeletedFalse(month, year)
                .stream().map(this::toDTO).collect(Collectors.toList());
    }

    @Override
    public List<BonusDTO> getByEmployee(Integer employeeId) {
        return bonusRepository.findAll().stream()
                .filter(b -> b.getEmployeeId().equals(employeeId) && !Boolean.TRUE.equals(b.getIsDeleted()))
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    private BonusDTO toDTO(Bonus b) {
        String empName = null;
        String deptName = null;
        try {
            var emp = employeeRepository.findByIdAndIsDeletedFalse(b.getEmployeeId()).orElse(null);
            if (emp != null) {
                empName = emp.getFullName();
                if (emp.getDeptId() != null) {
                    deptName = departmentRepository.findByIdAndIsDeletedFalse(emp.getDeptId())
                            .map(d -> d.getDeptName()).orElse(null);
                }
            }
        } catch (Exception ignored) {}

        String typeName = switch (b.getBonusType()) {
            case "KPI"         -> "Thưởng KPI";
            case "PROJECT"     -> "Thưởng dự án";
            case "HOLIDAY"     -> "Thưởng lễ/Tết";
            case "MONTH_13"    -> "Lương tháng 13";
            case "PERFORMANCE" -> "Thưởng hiệu suất";
            default            -> "Thưởng khác";
        };

        return BonusDTO.builder()
                .id(b.getId())
                .employeeId(b.getEmployeeId())
                .employeeName(empName)
                .deptName(deptName)
                .bonusType(b.getBonusType())
                .bonusTypeName(typeName)
                .name(b.getName())
                .amount(b.getAmount())
                .isTaxable(b.getIsTaxable())
                .month(b.getMonth())
                .year(b.getYear())
                .reason(b.getReason())
                .isApproved(b.getIsApproved())
                .approvedBy(b.getApprovedBy())
                .approvedAt(b.getApprovedAt())
                .createdAt(b.getCreatedAt())
                .build();
    }
}