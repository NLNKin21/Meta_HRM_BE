package com.metahrms.employee_management.service.impl.Leave;

import com.metahrms.employee_management.dto.request.Leave.LeaveTypeCreateDto;
import com.metahrms.employee_management.dto.request.Leave.LeaveTypeSeniorityRuleDto;
import com.metahrms.employee_management.dto.request.Leave.LeaveTypeUpdateDto;
import com.metahrms.employee_management.dto.response.Leave.LeaveTypeResponseDto;
import com.metahrms.employee_management.dto.response.Leave.LeaveTypeSeniorityRuleResponseDto;
import com.metahrms.employee_management.entity.Leave.LeaveType;
import com.metahrms.employee_management.entity.Leave.LeaveTypeSeniorityRule;
import com.metahrms.employee_management.exception.BadRequestException;
import com.metahrms.employee_management.exception.ResourceNotFoundException;
import com.metahrms.employee_management.repository.LeaveTypeRepository;
import com.metahrms.employee_management.service.Leave.LeaveBalanceService;
import com.metahrms.employee_management.service.Leave.LeaveTypeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class LeaveTypeServiceImpl implements LeaveTypeService {

    private final LeaveTypeRepository leaveTypeRepository;
    private final LeaveBalanceService leaveBalanceService;

    @Override
    @Transactional
    public LeaveTypeResponseDto create(LeaveTypeCreateDto dto) {
        if (leaveTypeRepository.existsByCode(dto.getCode())) {
            throw new BadRequestException("Mã loại nghỉ đã tồn tại");
        }

        validateSeniorityRules(dto.getIncreaseBySeniority(), dto.getSeniorityRules());

        LeaveType entity = LeaveType.builder()
                .code(dto.getCode().trim())
                .name(dto.getName().trim())
                .maxDaysPerYear(dto.getMaxDaysPerYear())
                .defaultDaysPerYear(dto.getDefaultDaysPerYear())
                .paidLeave(dto.getPaidLeave())
                .requiresApproval(dto.getRequiresApproval())
                .requiresDocument(dto.getRequiresDocument())
                .active(dto.getActive())
                .deductBalance(dto.getDeductBalance())
                .deductFromAnnualLeaveBalance(dto.getDeductFromAnnualLeaveBalance())
                .autoApprove(dto.getAutoApprove())
                .allowCarryForward(dto.getAllowCarryForward())
                .allowEncashment(dto.getAllowEncashment())
                .countsInAttendance(dto.getCountsInAttendance())
                .countsInCompanyPayroll(dto.getCountsInCompanyPayroll())
                .deductSalary(dto.getDeductSalary())
                .socialInsurancePaid(dto.getSocialInsurancePaid())
                .increaseBySeniority(dto.getIncreaseBySeniority())
                .build();

        replaceSeniorityRules(entity, dto.getSeniorityRules());

        LeaveType saved = leaveTypeRepository.save(entity);

        leaveBalanceService.syncBalancesForLeaveType(saved.getId(), LocalDate.now().getYear());

        return map(saved);
    }

    @Override
    @Transactional
    public LeaveTypeResponseDto update(Long id, LeaveTypeUpdateDto dto) {
        LeaveType entity = leaveTypeRepository.findByIdWithRules(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy loại nghỉ"));

        validateSeniorityRules(dto.getIncreaseBySeniority(), dto.getSeniorityRules());

        entity.setName(dto.getName().trim());
        entity.setMaxDaysPerYear(dto.getMaxDaysPerYear());
        entity.setDefaultDaysPerYear(dto.getDefaultDaysPerYear());
        entity.setPaidLeave(dto.getPaidLeave());
        entity.setRequiresApproval(dto.getRequiresApproval());
        entity.setRequiresDocument(dto.getRequiresDocument());
        entity.setActive(dto.getActive());
        entity.setDeductBalance(dto.getDeductBalance());
        entity.setDeductFromAnnualLeaveBalance(dto.getDeductFromAnnualLeaveBalance());
        entity.setAutoApprove(dto.getAutoApprove());
        entity.setAllowCarryForward(dto.getAllowCarryForward());
        entity.setAllowEncashment(dto.getAllowEncashment());
        entity.setCountsInAttendance(dto.getCountsInAttendance());
        entity.setCountsInCompanyPayroll(dto.getCountsInCompanyPayroll());
        entity.setDeductSalary(dto.getDeductSalary());
        entity.setSocialInsurancePaid(dto.getSocialInsurancePaid());
        entity.setIncreaseBySeniority(dto.getIncreaseBySeniority());

        replaceSeniorityRules(entity, dto.getSeniorityRules());

        LeaveType saved = leaveTypeRepository.save(entity);

        leaveBalanceService.syncBalancesForLeaveType(saved.getId(), LocalDate.now().getYear());

        return map(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public LeaveTypeResponseDto getById(Long id) {
        LeaveType entity = leaveTypeRepository.findByIdWithRules(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy loại nghỉ"));
        return map(entity);
    }

    @Override
    @Transactional(readOnly = true)
    public List<LeaveTypeResponseDto> getAll() {
        return leaveTypeRepository.findAllWithRules()
                .stream()
                .map(this::map)
                .toList();
    }

    private void replaceSeniorityRules(LeaveType leaveType, List<LeaveTypeSeniorityRuleDto> rules) {
        leaveType.getSeniorityRules().clear();

        if (!Boolean.TRUE.equals(leaveType.getIncreaseBySeniority()) || rules == null || rules.isEmpty()) {
            return;
        }

        List<LeaveTypeSeniorityRuleDto> sortedRules = new ArrayList<>(rules);
        sortedRules.sort(Comparator.comparing(LeaveTypeSeniorityRuleDto::getMinYears));

        for (LeaveTypeSeniorityRuleDto ruleDto : sortedRules) {
            LeaveTypeSeniorityRule rule = LeaveTypeSeniorityRule.builder()
                    .leaveType(leaveType)
                    .minYears(ruleDto.getMinYears())
                    .extraDays(ruleDto.getExtraDays())
                    .build();

            leaveType.getSeniorityRules().add(rule);
        }
    }

    private void validateSeniorityRules(Boolean increaseBySeniority, List<LeaveTypeSeniorityRuleDto> rules) {
        if (!Boolean.TRUE.equals(increaseBySeniority)) {
            return;
        }

        if (rules == null || rules.isEmpty()) {
            throw new BadRequestException("Vui lòng cấu hình mốc thâm niên");
        }

        Integer prevMinYears = null;
        for (LeaveTypeSeniorityRuleDto rule : rules) {
            if (rule.getMinYears() == null || rule.getMinYears() < 0) {
                throw new BadRequestException("Mốc thâm niên không hợp lệ");
            }

            if (rule.getExtraDays() == null || rule.getExtraDays() < 0) {
                throw new BadRequestException("Số ngày cộng thêm không hợp lệ");
            }

            if (prevMinYears != null && rule.getMinYears() <= prevMinYears) {
                throw new BadRequestException("Các mốc thâm niên phải tăng dần và không trùng nhau");
            }

            prevMinYears = rule.getMinYears();
        }
    }

    private LeaveTypeResponseDto map(LeaveType entity) {
        return LeaveTypeResponseDto.builder()
                .id(entity.getId())
                .code(entity.getCode())
                .name(entity.getName())
                .maxDaysPerYear(entity.getMaxDaysPerYear())
                .defaultDaysPerYear(entity.getDefaultDaysPerYear())
                .paidLeave(entity.getPaidLeave())
                .requiresApproval(entity.getRequiresApproval())
                .requiresDocument(entity.getRequiresDocument())
                .active(entity.getActive())
                .deductBalance(entity.getDeductBalance())
                .deductFromAnnualLeaveBalance(entity.getDeductFromAnnualLeaveBalance())
                .autoApprove(entity.getAutoApprove())
                .allowCarryForward(entity.getAllowCarryForward())
                .allowEncashment(entity.getAllowEncashment())
                .countsInAttendance(entity.getCountsInAttendance())
                .countsInCompanyPayroll(entity.getCountsInCompanyPayroll())
                .deductSalary(entity.getDeductSalary())
                .socialInsurancePaid(entity.getSocialInsurancePaid())
                .increaseBySeniority(entity.getIncreaseBySeniority())
                .seniorityRules(
                        entity.getSeniorityRules() == null
                                ? List.of()
                                : entity.getSeniorityRules().stream()
                                        .sorted(Comparator.comparing(LeaveTypeSeniorityRule::getMinYears))
                                        .map(rule -> LeaveTypeSeniorityRuleResponseDto.builder()
                                                .id(rule.getId())
                                                .minYears(rule.getMinYears())
                                                .extraDays(rule.getExtraDays())
                                                .build())
                                        .toList()
                )
                .build();
    }
}