package com.metahrms.employee_management.service.impl;

import com.metahrms.employee_management.dto.request.EmployeeRequest;
import com.metahrms.employee_management.dto.response.EmployeeResponse;
import com.metahrms.employee_management.dto.response.PageResponse;
import com.metahrms.employee_management.entity.Department;
import com.metahrms.employee_management.entity.Employee;
import com.metahrms.employee_management.entity.Position;
import com.metahrms.employee_management.enums.EmployeeStatus;
import com.metahrms.employee_management.exception.BadRequestException;
import com.metahrms.employee_management.exception.ResourceNotFoundException;
import com.metahrms.employee_management.repository.DepartmentRepository;
import com.metahrms.employee_management.repository.EmployeeRepository;
import com.metahrms.employee_management.repository.PositionRepository;
import com.metahrms.employee_management.service.EmployeeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;


import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class EmployeeServiceImpl implements EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final DepartmentRepository departmentRepository;
    private final PositionRepository positionRepository;

    @Override
    public EmployeeResponse createEmployee(EmployeeRequest request) {
        log.info("Creating new employee with email: {}", request.getEmail());

        // Validate email uniqueness
        if (employeeRepository.existsByEmail(request.getEmail())) {
            throw new BadRequestException("Email already exists: " + request.getEmail());
        }

        // Get department
        Department department = departmentRepository.findByIdAndIsDeletedFalse(request.getDepartmentId())
                .orElseThrow(() -> new ResourceNotFoundException("Department not found with id: " + request.getDepartmentId()));

        // Get position
        Position position = positionRepository.findByIdAndIsDeletedFalse(request.getPositionId())
                .orElseThrow(() -> new ResourceNotFoundException("Position not found with id: " + request.getPositionId()));

        // Build employee entity
        Employee employee = Employee.builder()
                .employeeCode(generateEmployeeCode())
                .fullName(request.getFullName())
                .gender(request.getGender())
                .dateOfBirth(request.getDateOfBirth())
                .phoneNumber(request.getPhoneNumber())
                .email(request.getEmail())
                .personalEmail(request.getPersonalEmail())
                .idCardNumber(request.getIdCardNumber())
                .address(request.getAddress())
                .permanentAddress(request.getPermanentAddress())
                .hireDate(request.getHireDate())
                .basicSalary(request.getBasicSalary())
                .status(request.getStatus() != null ? request.getStatus() : EmployeeStatus.ACTIVE)
                .department(department)
                .position(position)
                .annualLeaveDays(request.getAnnualLeaveDays() != null ? request.getAnnualLeaveDays() : 12)
                .remainingLeaveDays(request.getAnnualLeaveDays() != null ? request.getAnnualLeaveDays() : 12)
                .build();

        // Set manager if provided
        if (request.getManagerId() != null) {
            Employee manager = employeeRepository.findByIdAndIsDeletedFalse(request.getManagerId())
                    .orElseThrow(() -> new ResourceNotFoundException("Manager not found with id: " + request.getManagerId()));
            employee.setManager(manager);
        }

        Employee savedEmployee = employeeRepository.save(employee);
        log.info("Employee created successfully with ID: {}", savedEmployee.getId());

        return mapToResponse(savedEmployee);
    }

    @Override
    @Transactional(readOnly = true)
    public EmployeeResponse getEmployeeById(Long id) {
        Employee employee = employeeRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with id: " + id));
        return mapToResponse(employee);
    }

    @Override
    @Transactional(readOnly = true)
    public EmployeeResponse getEmployeeByCode(String employeeCode) {
        Employee employee = employeeRepository.findByEmployeeCodeAndIsDeletedFalse(employeeCode)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with code: " + employeeCode));
        return mapToResponse(employee);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<EmployeeResponse> getAllEmployees(Pageable pageable) {
        Page<Employee> page = employeeRepository.findByIsDeletedFalse(pageable);
        Page<EmployeeResponse> responsePage = page.map(this::mapToResponse);
        return PageResponse.from(responsePage);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<EmployeeResponse> searchEmployees(String keyword, Pageable pageable) {
        Page<Employee> page = employeeRepository.searchByKeyword(keyword, pageable);
        Page<EmployeeResponse> responsePage = page.map(this::mapToResponse);
        return PageResponse.from(responsePage);
    }

    @Override
    public EmployeeResponse updateEmployee(Long id, EmployeeRequest request) {
        log.info("Updating employee with ID: {}", id);

        Employee employee = employeeRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with id: " + id));

        // Check email uniqueness if changed
        if (!employee.getEmail().equals(request.getEmail()) 
                && employeeRepository.existsByEmail(request.getEmail())) {
            throw new BadRequestException("Email already exists: " + request.getEmail());
        }

        // Update fields
        employee.setFullName(request.getFullName());
        employee.setGender(request.getGender());
        employee.setDateOfBirth(request.getDateOfBirth());
        employee.setPhoneNumber(request.getPhoneNumber());
        employee.setEmail(request.getEmail());
        employee.setPersonalEmail(request.getPersonalEmail());
        employee.setIdCardNumber(request.getIdCardNumber());
        employee.setAddress(request.getAddress());
        employee.setPermanentAddress(request.getPermanentAddress());
        employee.setHireDate(request.getHireDate());
        employee.setBasicSalary(request.getBasicSalary());

        if (request.getStatus() != null) {
            employee.setStatus(request.getStatus());
        }

        if (request.getAnnualLeaveDays() != null) {
            employee.setAnnualLeaveDays(request.getAnnualLeaveDays());
        }

        // Update department
        if (request.getDepartmentId() != null) {
            Department department = departmentRepository.findByIdAndIsDeletedFalse(request.getDepartmentId())
                    .orElseThrow(() -> new ResourceNotFoundException("Department not found"));
            employee.setDepartment(department);
        }

        // Update position
        if (request.getPositionId() != null) {
            Position position = positionRepository.findByIdAndIsDeletedFalse(request.getPositionId())
                    .orElseThrow(() -> new ResourceNotFoundException("Position not found"));
            employee.setPosition(position);
        }

        // Update manager
        if (request.getManagerId() != null) {
            if (request.getManagerId().equals(id)) {
                throw new BadRequestException("Employee cannot be their own manager");
            }
            Employee manager = employeeRepository.findByIdAndIsDeletedFalse(request.getManagerId())
                    .orElseThrow(() -> new ResourceNotFoundException("Manager not found"));
            employee.setManager(manager);
        } else {
            employee.setManager(null);
        }

        Employee updatedEmployee = employeeRepository.save(employee);
        log.info("Employee updated successfully with ID: {}", updatedEmployee.getId());

        return mapToResponse(updatedEmployee);
    }

    @Override
    public void deleteEmployee(Long id) {
        log.info("Deleting employee with ID: {}", id);

        Employee employee = employeeRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with id: " + id));

        // Soft delete
        employee.setIsDeleted(true);
        employee.setStatus(EmployeeStatus.TERMINATED);
        employee.setTerminationDate(LocalDate.now());
        employeeRepository.save(employee);

        log.info("Employee soft deleted successfully with ID: {}", id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<EmployeeResponse> getEmployeesByDepartment(Long departmentId) {
        List<Employee> employees = employeeRepository.findByDepartmentIdAndIsDeletedFalse(departmentId);
        return employees.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<EmployeeResponse> getSubordinates(Long managerId) {
        List<Employee> subordinates = employeeRepository.findByManagerIdAndIsDeletedFalse(managerId);
        return subordinates.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public String generateEmployeeCode() {
        String prefix = "EMP" + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMM"));
        String maxCode = employeeRepository.findMaxEmployeeCode(prefix);
        
        int nextNumber = 1;
        if (maxCode != null) {
            String numberPart = maxCode.substring(prefix.length());
            nextNumber = Integer.parseInt(numberPart) + 1;
        }
        
        return prefix + String.format("%04d", nextNumber);
    }

    // ==================== PRIVATE METHODS ====================

    private EmployeeResponse mapToResponse(Employee employee) {
        return EmployeeResponse.builder()
                .id(employee.getId())
                .employeeCode(employee.getEmployeeCode())
                .fullName(employee.getFullName())
                .gender(employee.getGender())
                .dateOfBirth(employee.getDateOfBirth())
                .phoneNumber(employee.getPhoneNumber())
                .email(employee.getEmail())
                .personalEmail(employee.getPersonalEmail())
                .idCardNumber(employee.getIdCardNumber())
                .address(employee.getAddress())
                .permanentAddress(employee.getPermanentAddress())
                .hireDate(employee.getHireDate())
                .terminationDate(employee.getTerminationDate())
                .basicSalary(employee.getBasicSalary())
                .status(employee.getStatus())
                .avatarUrl(employee.getAvatarUrl())
                .annualLeaveDays(employee.getAnnualLeaveDays())
                .remainingLeaveDays(employee.getRemainingLeaveDays())
                // Department
                .departmentId(employee.getDepartment() != null ? employee.getDepartment().getId() : null)
                .departmentName(employee.getDepartment() != null ? employee.getDepartment().getDeptName() : null)
                .departmentCode(employee.getDepartment() != null ? employee.getDepartment().getDeptCode() : null)
                // Position
                .positionId(employee.getPosition() != null ? employee.getPosition().getId() : null)
                .positionName(employee.getPosition() != null ? employee.getPosition().getPositionName() : null)
                .positionCode(employee.getPosition() != null ? employee.getPosition().getPositionCode() : null)
                // Manager
                .managerId(employee.getManager() != null ? employee.getManager().getId() : null)
                .managerName(employee.getManager() != null ? employee.getManager().getFullName() : null)
                // User
                .userId(employee.getUser() != null ? employee.getUser().getId() : null)
                .username(employee.getUser() != null ? employee.getUser().getUsername() : null)
                // Audit
                .createdAt(employee.getCreatedAt())
                .updatedAt(employee.getUpdatedAt())
                .build();
    }
}
