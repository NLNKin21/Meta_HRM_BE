package com.metahrms.employee_management.service.impl.Leave;

import com.metahrms.employee_management.dto.request.Leave.LeaveBalanceInitDto;
import com.metahrms.employee_management.dto.response.Leave.LeaveBalanceResponseDto;
import com.metahrms.employee_management.entity.Employee;
import com.metahrms.employee_management.entity.Leave.LeaveBalance;
import com.metahrms.employee_management.entity.Leave.LeaveType;
import com.metahrms.employee_management.entity.Leave.LeaveTypeSeniorityRule;
import com.metahrms.employee_management.enums.EmployeeStatus;
import com.metahrms.employee_management.exception.BadRequestException;
import com.metahrms.employee_management.exception.ResourceNotFoundException;
import com.metahrms.employee_management.repository.EmployeeRepository;
import com.metahrms.employee_management.repository.LeaveBalanceRepository;
import com.metahrms.employee_management.repository.LeaveTypeRepository;
import com.metahrms.employee_management.service.Leave.LeaveBalanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Period;
import java.util.List;

@Service
@RequiredArgsConstructor
public class LeaveBalanceServiceImpl implements LeaveBalanceService {

    private final LeaveBalanceRepository leaveBalanceRepository;
    private final LeaveTypeRepository leaveTypeRepository;
    private final EmployeeRepository employeeRepository;

    @Override
    @Transactional
    public LeaveBalanceResponseDto initBalance(LeaveBalanceInitDto dto) {
        LeaveType leaveType = leaveTypeRepository.findById(dto.getLeaveTypeId())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy loại nghỉ"));

        leaveBalanceRepository.findByEmployeeIdAndLeaveTypeIdAndYear(
                dto.getEmployeeId(),
                dto.getLeaveTypeId(),
                dto.getYear()
        ).ifPresent(item -> {
            throw new BadRequestException("Số dư phép đã tồn tại");
        });

        LeaveBalance balance = LeaveBalance.builder()
                .employeeId(dto.getEmployeeId())
                .leaveType(leaveType)
                .year(dto.getYear())
                .allocatedDays(dto.getAllocatedDays())
                .usedDays(BigDecimal.ZERO)
                .pendingDays(BigDecimal.ZERO)
                .carryForwardDays(dto.getCarryForwardDays() == null ? BigDecimal.ZERO : dto.getCarryForwardDays())
                .encashedDays(BigDecimal.ZERO)
                .build();

        LeaveBalance saved = leaveBalanceRepository.save(balance);
        return map(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<LeaveBalanceResponseDto> getEmployeeBalances(Integer employeeId, Integer year) {
        return leaveBalanceRepository.findByEmployeeIdAndYear(employeeId, year)
                .stream()
                .map(this::map)
                .toList();
    }

    @Override
    @Transactional
    public void addPendingDays(Integer employeeId, Long leaveTypeId, Integer year, BigDecimal days) {
        LeaveBalance balance = getBalance(employeeId, leaveTypeId, year);
        balance.setPendingDays(balance.getPendingDays().add(days));
        leaveBalanceRepository.save(balance);
    }

    @Override
    @Transactional
    public void approveDays(Integer employeeId, Long leaveTypeId, Integer year, BigDecimal days) {
        LeaveBalance balance = getBalance(employeeId, leaveTypeId, year);
        balance.setPendingDays(balance.getPendingDays().subtract(days));
        balance.setUsedDays(balance.getUsedDays().add(days));
        leaveBalanceRepository.save(balance);
    }

    @Override
    @Transactional
    public void rollbackPendingDays(Integer employeeId, Long leaveTypeId, Integer year, BigDecimal days) {
        LeaveBalance balance = getBalance(employeeId, leaveTypeId, year);
        balance.setPendingDays(balance.getPendingDays().subtract(days));
        leaveBalanceRepository.save(balance);
    }

    @Override
    @Transactional
    public void rollbackUsedDays(Integer employeeId, Long leaveTypeId, Integer year, BigDecimal days) {
        LeaveBalance balance = getBalance(employeeId, leaveTypeId, year);
        balance.setUsedDays(balance.getUsedDays().subtract(days));
        leaveBalanceRepository.save(balance);
    }

    @Override
    @Transactional(readOnly = true)
    public void validateEnoughBalance(Integer employeeId, Long leaveTypeId, Integer year, BigDecimal days) {
        LeaveBalance balance = getBalance(employeeId, leaveTypeId, year);
        BigDecimal remaining = calculateRemaining(balance);

        if (remaining.compareTo(days) < 0) {
            throw new BadRequestException("Không đủ số dư phép");
        }
    }

    @Override
    @Transactional
    public void syncBalancesForYear(Integer year) {
        List<Employee> employees = employeeRepository.findAllByStatusAndNotDeleted(EmployeeStatus.ACTIVE);

        List<LeaveType> leaveTypes = leaveTypeRepository.findAll()
                .stream()
                .filter(leaveType -> Boolean.TRUE.equals(leaveType.getActive()))
                .toList();

        for (Employee employee : employees) {
            for (LeaveType leaveType : leaveTypes) {
                upsertBalance(employee, leaveType, year);
            }
        }
    }

    @Override
    @Transactional
    public void syncBalancesForLeaveType(Long leaveTypeId, Integer year) {
        LeaveType leaveType = leaveTypeRepository.findById(leaveTypeId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy loại nghỉ"));

        List<Employee> employees = employeeRepository.findAllByStatusAndNotDeleted(EmployeeStatus.ACTIVE);

        for (Employee employee : employees) {
            upsertBalance(employee, leaveType, year);
        }
    }

    @Override
    @Transactional
    public void initBalancesForEmployee(Integer employeeId, Integer year) {
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy nhân viên"));

        List<LeaveType> leaveTypes = leaveTypeRepository.findAll()
                .stream()
                .filter(leaveType -> Boolean.TRUE.equals(leaveType.getActive()))
                .toList();

        for (LeaveType leaveType : leaveTypes) {
            upsertBalance(employee, leaveType, year);
        }
    }

    @Transactional(readOnly = true)
    protected LeaveBalance getBalance(Integer employeeId, Long leaveTypeId, Integer year) {
        return leaveBalanceRepository.findByEmployeeIdAndLeaveTypeIdAndYear(employeeId, leaveTypeId, year)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy balance nghỉ"));
    }

    private void upsertBalance(Employee employee, LeaveType leaveType, Integer year) {
        LeaveBalance existing = leaveBalanceRepository
                .findByEmployeeIdAndLeaveTypeIdAndYear(employee.getId(), leaveType.getId(), year)
                .orElse(null);

        BigDecimal allocatedDays = calculateAllocatedDays(employee, leaveType, year);

        if (existing == null) {
            LeaveBalance newBalance = LeaveBalance.builder()
                    .employeeId(employee.getId())
                    .leaveType(leaveType)
                    .year(year)
                    .allocatedDays(allocatedDays)
                    .usedDays(BigDecimal.ZERO)
                    .pendingDays(BigDecimal.ZERO)
                    .carryForwardDays(BigDecimal.ZERO)
                    .encashedDays(BigDecimal.ZERO)
                    .build();

            leaveBalanceRepository.save(newBalance);
            return;
        }

        existing.setAllocatedDays(allocatedDays);
        leaveBalanceRepository.save(existing);
    }

    private BigDecimal calculateAllocatedDays(Employee employee, LeaveType leaveType, Integer year) {
    int days = leaveType.getDefaultDaysPerYear() == null
            ? 0
            : leaveType.getDefaultDaysPerYear();

    if (Boolean.TRUE.equals(leaveType.getIncreaseBySeniority())
            && employee.getHireDate() != null
            && leaveType.getSeniorityRules() != null
            && !leaveType.getSeniorityRules().isEmpty()) {

        LocalDate calculationDate = LocalDate.of(year, 12, 31);
        int seniorityYears = Period.between(employee.getHireDate(), calculationDate).getYears();

        int maxExtraDays = leaveType.getSeniorityRules().stream()
                .filter(rule -> seniorityYears >= rule.getMinYears())
                .map(LeaveTypeSeniorityRule::getExtraDays)
                .max(Integer::compareTo)
                .orElse(0);

        days += maxExtraDays;
    }

    return BigDecimal.valueOf(days);
}
    private BigDecimal calculateRemaining(LeaveBalance balance) {
        return balance.getAllocatedDays()
                .add(balance.getCarryForwardDays())
                .subtract(balance.getUsedDays())
                .subtract(balance.getPendingDays())
                .subtract(balance.getEncashedDays());
    }

    private LeaveBalanceResponseDto map(LeaveBalance entity) {
        return LeaveBalanceResponseDto.builder()
                .id(entity.getId())
                .employeeId(entity.getEmployeeId())
                .leaveTypeId(entity.getLeaveType().getId())
                .leaveTypeCode(entity.getLeaveType().getCode())
                .leaveTypeName(entity.getLeaveType().getName())
                .year(entity.getYear())
                .allocatedDays(entity.getAllocatedDays())
                .usedDays(entity.getUsedDays())
                .pendingDays(entity.getPendingDays())
                .carryForwardDays(entity.getCarryForwardDays())
                .encashedDays(entity.getEncashedDays())
                .remainingDays(calculateRemaining(entity))
                .build();
    }
}