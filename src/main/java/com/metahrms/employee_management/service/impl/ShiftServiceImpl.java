package com.metahrms.employee_management.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.metahrms.employee_management.dto.request.shift.AssignShiftRequest;
import com.metahrms.employee_management.dto.request.shift.CreateShiftRequest;
import com.metahrms.employee_management.dto.request.shift.UpdateShiftRequest;
import com.metahrms.employee_management.dto.response.shift.ShiftEmployeeDTO;
import com.metahrms.employee_management.dto.response.shift.ShiftResponseDTO;
import com.metahrms.employee_management.entity.Attendance.Shift;
import com.metahrms.employee_management.entity.Department;
import com.metahrms.employee_management.entity.Employee;
import com.metahrms.employee_management.repository.Attendance.ShiftRepository;
import com.metahrms.employee_management.repository.DepartmentRepository;
import com.metahrms.employee_management.repository.EmployeeRepository;
import com.metahrms.employee_management.service.ShiftService;
import com.metahrms.employee_management.util.SecurityUtils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ShiftServiceImpl implements ShiftService {

    private final ShiftRepository shiftRepository;
    private final EmployeeRepository employeeRepository;
    private final DepartmentRepository departmentRepository;
    private final ObjectMapper objectMapper;

    // ============================================
    // CREATE
    // ============================================
    @Override
    @Transactional
    public ShiftResponseDTO create(CreateShiftRequest request) {
        log.info("[SHIFT] Creating shift: name={}, code={}", request.getName(), request.getCode());

        // Validate unique code
        if (request.getCode() != null && !request.getCode().isBlank()) {
            if (shiftRepository.existsByCodeAndIsDeletedFalse(request.getCode())) {
                throw new IllegalArgumentException("Shift code already exists: " + request.getCode());
            }
        }

        // Validate thời gian
        if (request.getStartTime().equals(request.getEndTime())) {
            throw new IllegalArgumentException("Start time and end time cannot be the same");
        }

        // Validate workDays (1=Mon ... 7=Sun)
        if (request.getWorkDays() != null) {
            for (Integer day : request.getWorkDays()) {
                if (day < 1 || day > 7) {
                    throw new IllegalArgumentException(
                        "Work day must be between 1 (Mon) and 7 (Sun), got: " + day
                    );
                }
            }
        }

        Shift shift = new Shift();
        shift.setName(request.getName());
        shift.setCode(request.getCode());
        shift.setStartTime(request.getStartTime());
        shift.setEndTime(request.getEndTime());
        shift.setLateThreshold(request.getLateThreshold() != null ? request.getLateThreshold() : 15);
        shift.setEarlyLeaveThreshold(request.getEarlyLeaveThreshold() != null ? request.getEarlyLeaveThreshold() : 15);
        shift.setCheckInStartBefore(request.getCheckInStartBefore() != null ? request.getCheckInStartBefore() : 30);
        shift.setCheckInEndAfter(request.getCheckInEndAfter() != null ? request.getCheckInEndAfter() : 120);
        shift.setWorkDays(toJson(
            request.getWorkDays() != null ? request.getWorkDays() : List.of(1, 2, 3, 4, 5)
        ));
        shift.setBreakDuration(request.getBreakDuration() != null ? request.getBreakDuration() : 60);
        shift.setDescription(request.getDescription());
        shift.setColor(request.getColor());
        shift.setIsActive(true);
        shift.setCreatedBy(SecurityUtils.getCurrentUserId());

        Shift saved = shiftRepository.save(shift);
        log.info("[SHIFT] Created shift id={}", saved.getId());

        return toDTO(saved);
    }

    // ============================================
    // UPDATE
    // ============================================
    @Override
    @Transactional
    public ShiftResponseDTO update(Integer id, UpdateShiftRequest request) {
        log.info("[SHIFT] Updating shift id={}", id);

        Shift shift = shiftRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new IllegalArgumentException("Shift not found: " + id));

        if (request.getCode() != null && !request.getCode().isBlank()) {
            if (shiftRepository.existsByCodeAndIdNotAndIsDeletedFalse(request.getCode(), id)) {
                throw new IllegalArgumentException("Shift code already exists: " + request.getCode());
            }
            shift.setCode(request.getCode());
        }

        if (request.getName() != null) shift.setName(request.getName());
        if (request.getStartTime() != null) shift.setStartTime(request.getStartTime());
        if (request.getEndTime() != null) shift.setEndTime(request.getEndTime());
        if (request.getLateThreshold() != null) shift.setLateThreshold(request.getLateThreshold());
        if (request.getEarlyLeaveThreshold() != null) shift.setEarlyLeaveThreshold(request.getEarlyLeaveThreshold());
        if (request.getCheckInStartBefore() != null) shift.setCheckInStartBefore(request.getCheckInStartBefore());
        if (request.getCheckInEndAfter() != null) shift.setCheckInEndAfter(request.getCheckInEndAfter());
        if (request.getWorkDays() != null) shift.setWorkDays(toJson(request.getWorkDays()));
        if (request.getBreakDuration() != null) shift.setBreakDuration(request.getBreakDuration());
        if (request.getDescription() != null) shift.setDescription(request.getDescription());
        if (request.getColor() != null) shift.setColor(request.getColor());
        if (request.getIsActive() != null) shift.setIsActive(request.getIsActive());

        // Re-validate sau khi update
        if (shift.getStartTime().equals(shift.getEndTime())) {
            throw new IllegalArgumentException("Start time and end time cannot be the same");
        }

        shift.setUpdatedBy(SecurityUtils.getCurrentUserId());

        Shift saved = shiftRepository.save(shift);
        log.info("[SHIFT] Updated shift id={}", saved.getId());

        return toDTO(saved);
    }

    // ============================================
    // SOFT DELETE
    // ============================================
    @Override
    @Transactional
    public void softDelete(Integer id) {
        log.info("[SHIFT] Soft deleting shift id={}", id);

        Shift shift = shiftRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new IllegalArgumentException("Shift not found: " + id));

        // Không cho xoá nếu vẫn có employee đang dùng
        long employeeCount = shiftRepository.countEmployeesByShiftId(id);
        if (employeeCount > 0) {
            throw new IllegalArgumentException(
                "Cannot delete shift '" + shift.getName() + "'. " +
                employeeCount + " employee(s) still assigned. Please reassign them first."
            );
        }

        shift.setIsDeleted(true);
        shift.setIsActive(false);
        shift.setUpdatedBy(SecurityUtils.getCurrentUserId());

        shiftRepository.save(shift);
        log.info("[SHIFT] Soft deleted shift id={}", id);
    }

    // ============================================
    // GET
    // ============================================
    @Override
    public ShiftResponseDTO getById(Integer id) {
        Shift shift = shiftRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new IllegalArgumentException("Shift not found: " + id));
        return toDTO(shift);
    }

    @Override
    public Page<ShiftResponseDTO> getAll(Boolean isActive, String keyword, Pageable pageable) {
        return shiftRepository.findAllWithFilters(isActive, keyword, pageable)
                .map(this::toDTO);
    }

    // ============================================
    // ASSIGN / UNASSIGN SHIFT
    // ============================================

    @Override
    @Transactional
    public void assignShiftToEmployee(Integer employeeId, Integer shiftId) {
        log.info("[SHIFT] Assigning shift {} to employee {}", shiftId, employeeId);

        Employee employee = employeeRepository.findByIdAndIsDeletedFalse(employeeId)
                .orElseThrow(() -> new IllegalArgumentException("Employee not found: " + employeeId));

        Shift shift = shiftRepository.findByIdAndIsDeletedFalse(shiftId)
                .orElseThrow(() -> new IllegalArgumentException("Shift not found: " + shiftId));

        if (!Boolean.TRUE.equals(shift.getIsActive())) {
            throw new IllegalArgumentException(
                "Cannot assign inactive shift: " + shift.getName()
            );
        }

        // Employee.shift là ManyToOne relationship
        // → Set trực tiếp entity (không set shiftId column)
        employee.setShift(shift);
        employeeRepository.save(employee);

        log.info("[SHIFT] Assigned shift '{}' to employee '{}' (id={})",
                shift.getName(), employee.getFullName(), employeeId);
    }

    @Override
    @Transactional
    public void assignShiftToEmployees(AssignShiftRequest request) {
        log.info("[SHIFT] Bulk assigning shift {} to {} employees",
                request.getShiftId(), request.getEmployeeIds().size());

        if (request.getEmployeeIds() == null || request.getEmployeeIds().isEmpty()) {
            throw new IllegalArgumentException("Employee IDs list cannot be empty");
        }

        Shift shift = shiftRepository.findByIdAndIsDeletedFalse(request.getShiftId())
                .orElseThrow(() -> new IllegalArgumentException("Shift not found: " + request.getShiftId()));

        if (!Boolean.TRUE.equals(shift.getIsActive())) {
            throw new IllegalArgumentException(
                "Cannot assign inactive shift: " + shift.getName()
            );
        }

        List<Employee> employees = employeeRepository.findByIdInAndIsDeletedFalse(request.getEmployeeIds());

        // Kiểm tra tất cả IDs đều tồn tại
        if (employees.size() != request.getEmployeeIds().size()) {
            List<Integer> foundIds = employees.stream()
                    .map(Employee::getId)
                    .collect(Collectors.toList());

            List<Integer> notFoundIds = request.getEmployeeIds().stream()
                    .filter(empId -> !foundIds.contains(empId))
                    .collect(Collectors.toList());

            throw new IllegalArgumentException("Employees not found: " + notFoundIds);
        }

        employees.forEach(emp -> emp.setShift(shift));
        employeeRepository.saveAll(employees);

        log.info("[SHIFT] Bulk assigned shift '{}' to {} employees",
                shift.getName(), employees.size());
    }

    @Override
    @Transactional
    public void unassignShift(Integer employeeId) {
        log.info("[SHIFT] Unassigning shift from employee {}", employeeId);

        Employee employee = employeeRepository.findByIdAndIsDeletedFalse(employeeId)
                .orElseThrow(() -> new IllegalArgumentException("Employee not found: " + employeeId));

        String oldShiftName = employee.getShift() != null
                ? employee.getShift().getName()
                : "none";

        // Set null để bỏ gán ca
        employee.setShift(null);
        employeeRepository.save(employee);

        log.info("[SHIFT] Unassigned shift '{}' from employee '{}'",
                oldShiftName, employee.getFullName());
    }

    // ============================================
    // GET EMPLOYEES BY SHIFT
    // ============================================
    @Override
    public List<ShiftEmployeeDTO> getEmployeesByShift(Integer shiftId) {

        shiftRepository.findByIdAndIsDeletedFalse(shiftId)
                .orElseThrow(() -> new IllegalArgumentException("Shift not found: " + shiftId));

        List<Employee> employees = employeeRepository.findByShiftIdWithDetails(shiftId);

        return employees.stream()
                .map(emp -> {
                    // Lấy department name (Department entity rất đơn giản chỉ có deptName)
                    String deptName = null;
                    if (emp.getDeptId() != null) {
                        deptName = departmentRepository.findByIdAndIsDeletedFalse(emp.getDeptId())
                                .map(Department::getDeptName)
                                .orElse(null);
                    }

                    // Employee.position là ManyToOne → Position entity
                    // Dùng toString() hoặc tên field nếu Position có getName()
                    String positionName = emp.getPosition() != null
                            ? emp.getPosition().toString()
                            : null;

                    return ShiftEmployeeDTO.builder()
                            .employeeId(emp.getId())
                            .fullName(emp.getFullName())
                            .deptId(emp.getDeptId())
                            .deptName(deptName)
                            .positionName(positionName)
                            .status(emp.getStatus() != null ? emp.getStatus().name() : null)
                            .build();
                })
                .collect(Collectors.toList());
    }

    // ============================================
    // HELPERS
    // ============================================

    private ShiftResponseDTO toDTO(Shift shift) {
        // Tính số employee đang dùng shift này
        long empCount = shiftRepository.countEmployeesByShiftId(shift.getId());

        return ShiftResponseDTO.builder()
                .id(shift.getId())
                .name(shift.getName())
                .code(shift.getCode())
                .startTime(shift.getStartTime())
                .endTime(shift.getEndTime())
                .lateThreshold(shift.getLateThreshold())
                .earlyLeaveThreshold(shift.getEarlyLeaveThreshold())
                .checkInStartBefore(shift.getCheckInStartBefore())
                .checkInEndAfter(shift.getCheckInEndAfter())
                .workDays(fromJson(shift.getWorkDays()))
                .breakDuration(shift.getBreakDuration())
                .description(shift.getDescription())
                .color(shift.getColor())
                .isActive(shift.getIsActive())
                .totalWorkHours(shift.getTotalWorkHours())
                .employeeCount((int) empCount)
                .createdAt(shift.getCreatedAt())
                .updatedAt(shift.getUpdatedAt())
                .build();
    }

    /**
     * List<Integer> → JSON string để lưu vào Shift.workDays (column JSON)
     */
    private String toJson(List<Integer> list) {
        try {
            return objectMapper.writeValueAsString(list);
        } catch (JsonProcessingException e) {
            log.error("[SHIFT] Failed to serialize workDays: {}", list, e);
            return "[1,2,3,4,5]"; // fallback Mon-Fri
        }
    }

    /**
     * JSON string → List<Integer> để trả về DTO
     */
    private List<Integer> fromJson(String json) {
        if (json == null || json.isBlank()) {
            return List.of(1, 2, 3, 4, 5);
        }
        try {
            return objectMapper.readValue(json, new TypeReference<List<Integer>>() {});
        } catch (JsonProcessingException e) {
            log.error("[SHIFT] Failed to deserialize workDays: {}", json, e);
            return Collections.emptyList();
        }
    }
}