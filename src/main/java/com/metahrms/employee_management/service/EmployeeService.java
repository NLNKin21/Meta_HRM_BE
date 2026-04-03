package com.metahrms.employee_management.service;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.metahrms.employee_management.dto.request.Contract.ContractCreateDto;
import com.metahrms.employee_management.dto.request.Employee.EmployeeCreateDto;
import com.metahrms.employee_management.dto.request.Employee.EmployeeFilterDto;
import com.metahrms.employee_management.dto.request.Employee.EmployeeUpdateDto;
import com.metahrms.employee_management.dto.response.Employee.EmployeeResponse;
import com.metahrms.employee_management.dto.response.EmployeeProfileResponseDto;
import com.metahrms.employee_management.dto.response.PagedResponse;
import com.metahrms.employee_management.entity.Contract;
import com.metahrms.employee_management.entity.Department;
import com.metahrms.employee_management.entity.Employee;
import com.metahrms.employee_management.entity.User;
import com.metahrms.employee_management.enums.ContractStatus;
import com.metahrms.employee_management.enums.EmployeeStatus;
import com.metahrms.employee_management.enums.RoleInDepartment;
import com.metahrms.employee_management.exception.ResourceNotFoundException;
import com.metahrms.employee_management.repository.ContractRepository;
import com.metahrms.employee_management.repository.DepartmentRepository;
import com.metahrms.employee_management.repository.EmployeeRepository;
import com.metahrms.employee_management.repository.UserRepository;
import com.metahrms.employee_management.specification.EmployeeSpecification;
import com.metahrms.employee_management.util.SecurityUtils;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class EmployeeService {

    EmployeeRepository employeeRepository;
    UserRepository userRepository;
    DepartmentRepository departmentRepository;
    ContractRepository contractRepository;
    CloudinaryService cloudinaryService;

    @Transactional(readOnly = true)
    public PagedResponse<EmployeeResponse> getEmployees(EmployeeFilterDto filterDto) {
        log.info("Fetching employees with filters: {}", filterDto);

        Specification<Employee> spec = EmployeeSpecification.filterEmployees(
            filterDto.getStatus(),
            filterDto.getDeptId(),
            filterDto.getHireDate(),
            filterDto.getSearch()
        );

        Pageable pageable = PageRequest.of(
            filterDto.getPage(),
            filterDto.getPageSize(),
            Sort.by(Sort.Direction.DESC, "createdAt")
        );

        Page<Employee> employeePage = employeeRepository.findAll(spec, pageable);

        List<EmployeeResponse> employeeResponses = employeePage.getContent().stream()
            .map(this::toEmployeeResponse)
            .collect(Collectors.toList());

        return PagedResponse.<EmployeeResponse>builder()
            .content(employeeResponses)
            .currentPage(employeePage.getNumber())
            .pageSize(employeePage.getSize())
            .totalElements(employeePage.getTotalElements())
            .totalPages(employeePage.getTotalPages())
            .hasNext(employeePage.hasNext())
            .hasPrevious(employeePage.hasPrevious())
            .build();
    }

    @Transactional(readOnly = true)
    public EmployeeResponse getEmployeeById(Integer id) {
        log.info("Fetching employee with id: {}", id);

        Employee employee = employeeRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Employee not found with id: " + id));

        if (Boolean.TRUE.equals(employee.getIsDeleted())) {
            throw new ResourceNotFoundException("Employee has been deleted");
        }

        return toEmployeeResponse(employee);
    }

    @Transactional(readOnly = true)
    public EmployeeResponse getEmployeeByUserId(Integer userId) {
        log.info("Fetching employee with userId: {}", userId);

        Employee employee = employeeRepository.findByUserId(userId)
            .orElseThrow(() -> new ResourceNotFoundException("Employee not found with userId: " + userId));

        if (Boolean.TRUE.equals(employee.getIsDeleted())) {
            throw new ResourceNotFoundException("Employee has been deleted");
        }

        return toEmployeeResponse(employee);
    }

    @Transactional
    public EmployeeResponse createEmployee(EmployeeCreateDto createDto) {
        log.info("Creating employee for user: {}", createDto.getUserId());

        userRepository.findById(createDto.getUserId())
            .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + createDto.getUserId()));

        if (employeeRepository.findByUserId(createDto.getUserId()).isPresent()) {
            throw new IllegalStateException("User is already linked to an employee profile");
        }

        departmentRepository.findById(createDto.getDeptId())
            .orElseThrow(() -> new ResourceNotFoundException("Department not found with id: " + createDto.getDeptId()));

        RoleInDepartment roleInDept = createDto.getRoleInDept() != null
            ? createDto.getRoleInDept()
            : RoleInDepartment.STAFF;

        if (roleInDept == RoleInDepartment.HEAD) {
            Optional<Employee> existingHead = employeeRepository.findFirstByDeptIdAndRoleInDept(
                createDto.getDeptId(),
                RoleInDepartment.HEAD
            );

            if (existingHead.isPresent() && !Boolean.TRUE.equals(existingHead.get().getIsDeleted())) {
                throw new IllegalStateException("Department already has a head employee");
            }
        }

        Employee employee = new Employee();
        employee.setUserId(createDto.getUserId());
        employee.setDeptId(createDto.getDeptId());
        employee.setFullName(createDto.getFullName());
        employee.setGender(createDto.getGender());
        employee.setDob(createDto.getDob());
        employee.setPhoneNumber(createDto.getPhoneNumber());
        employee.setAddress(createDto.getAddress());
        employee.setHireDate(createDto.getHireDate());
        employee.setStatus(createDto.getStatus() != null ? createDto.getStatus() : EmployeeStatus.ACTIVE);
        employee.setBasicSalary(createDto.getBasicSalary());
        employee.setIsDeleted(false);
        employee.setRoleInDept(roleInDept);

        Employee savedEmployee = employeeRepository.save(employee);
        log.info("Employee created with ID: {}", savedEmployee.getId());

        return toEmployeeResponse(savedEmployee);
    }

    @Transactional
    public EmployeeResponse createEmployeeWithContract(
        EmployeeCreateDto employeeData,
        ContractCreateDto contractData,
        MultipartFile contractFile
    ) throws IOException {

        log.info("Creating employee with contract for user: {}", employeeData.getUserId());

        EmployeeResponse employeeResponse = createEmployee(employeeData);

        if (contractData != null) {
            Employee employee = employeeRepository.findById(employeeResponse.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found"));

            String fileUrl = null;
            String fileKey = null;

            if (contractFile != null && !contractFile.isEmpty()) {
                fileUrl = cloudinaryService.uploadFile(contractFile);
                fileKey = cloudinaryService.extractPublicId(fileUrl);
                log.info("Contract file uploaded: {}", fileUrl);
            }

            Contract contract = Contract.builder()
                .empId(employee.getId())
                .contractType(contractData.getContractType())
                .startDate(contractData.getStartDate())
                .endDate(contractData.getEndDate())
                .fileUrl(fileUrl)
                .fileKey(fileKey)
                .status(contractData.getStatus() != null
                    ? contractData.getStatus()
                    : ContractStatus.ACTIVE)
                .build();

            contract.setIsDeleted(false);
            Contract savedContract = contractRepository.save(contract);

            log.info("Contract created with ID: {} for employee: {}",
                savedContract.getId(), employee.getId());
        }

        return employeeResponse;
    }

    @Transactional
    public EmployeeResponse updateEmployee(Integer id, EmployeeUpdateDto updateDto) {
        log.info("Updating employee with id: {}", id);

        Employee employee = employeeRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Employee not found with id: " + id));

        if (Boolean.TRUE.equals(employee.getIsDeleted())) {
            throw new IllegalStateException("Cannot update deleted employee");
        }

        if (updateDto.getDeptId() != null) {
            departmentRepository.findById(updateDto.getDeptId())
                .orElseThrow(() -> new ResourceNotFoundException("Department not found with id: " + updateDto.getDeptId()));
            employee.setDeptId(updateDto.getDeptId());
        }

        if (updateDto.getFullName() != null) {
            employee.setFullName(updateDto.getFullName());
        }
        if (updateDto.getGender() != null) {
            employee.setGender(updateDto.getGender());
        }
        if (updateDto.getDob() != null) {
            employee.setDob(updateDto.getDob());
        }
        if (updateDto.getPhoneNumber() != null) {
            employee.setPhoneNumber(updateDto.getPhoneNumber());
        }
        if (updateDto.getAddress() != null) {
            employee.setAddress(updateDto.getAddress());
        }
        if (updateDto.getHireDate() != null) {
            employee.setHireDate(updateDto.getHireDate());
        }
        if (updateDto.getStatus() != null) {
            employee.setStatus(updateDto.getStatus());
        }

        if (updateDto.getRoleInDept() != null) {
            if (updateDto.getRoleInDept() == RoleInDepartment.HEAD &&
                employee.getRoleInDept() != RoleInDepartment.HEAD) {

                Integer targetDeptId = updateDto.getDeptId() != null
                    ? updateDto.getDeptId()
                    : employee.getDeptId();

                Optional<Employee> existingHead = employeeRepository.findFirstByDeptIdAndRoleInDept(
                    targetDeptId,
                    RoleInDepartment.HEAD
                );

                if (existingHead.isPresent()
                    && !Boolean.TRUE.equals(existingHead.get().getIsDeleted())
                    && !existingHead.get().getId().equals(employee.getId())) {
                    throw new IllegalStateException("Department already has a head employee");
                }
            }
            employee.setRoleInDept(updateDto.getRoleInDept());
        }

        Employee updatedEmployee = employeeRepository.save(employee);
        log.info("Employee updated successfully: {}", id);

        return toEmployeeResponse(updatedEmployee);
    }

    @Transactional
    public void deleteEmployee(Integer id) {
        log.info("Deleting employee with id: {}", id);

        Employee employee = employeeRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Employee not found with id: " + id));

        employee.setIsDeleted(true);
        employeeRepository.save(employee);

        log.info("Employee soft deleted successfully: {}", id);
    }

    @Transactional(readOnly = true)
    public EmployeeResponse getCurrentUserEmployee() {
        Integer currentUserId = SecurityUtils.getCurrentUserId();
        if (currentUserId == null) {
            throw new IllegalStateException("User not authenticated");
        }

        Employee employee = employeeRepository.findByUserId(currentUserId)
            .orElseThrow(() -> new ResourceNotFoundException("Employee not found for current user"));

        if (Boolean.TRUE.equals(employee.getIsDeleted())) {
            throw new ResourceNotFoundException("Employee has been deleted");
        }

        return toEmployeeResponse(employee);
    }

    @Transactional(readOnly = true)
    public EmployeeProfileResponseDto getEmployeeProfile(Integer employeeId) {
        log.info("Fetching employee profile with id: {}", employeeId);

        Employee employee = employeeRepository.findById(employeeId)
            .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy nhân viên với id: " + employeeId));

        if (Boolean.TRUE.equals(employee.getIsDeleted())) {
            throw new ResourceNotFoundException("Employee has been deleted");
        }

        Employee departmentHead = findDepartmentHead(employee.getDeptId(), employee.getId());

        return EmployeeProfileResponseDto.builder()
            .id(employee.getId())
            .userId(employee.getUserId())
            .deptId(employee.getDeptId())
            .fullName(employee.getFullName())
            .gender(employee.getGender())
            .dob(employee.getDob())
            .phoneNumber(employee.getPhoneNumber())
            .address(employee.getAddress())
            .hireDate(employee.getHireDate())
            .basicSalary(employee.getBasicSalary())
            .status(employee.getStatus())
            .roleInDept(employee.getRoleInDept())
            .positionId(employee.getPosition() != null ? employee.getPosition().getId() : null)
            .positionName(employee.getPosition() != null ? employee.getPosition().getPositionName() : null)
            .managerId(departmentHead != null ? departmentHead.getId() : null)
            .managerName(departmentHead != null ? departmentHead.getFullName() : null)
            .build();
    }

    @Transactional(readOnly = true)
    public List<EmployeeResponse> getDepartmentMembersForManager(Integer managerEmployeeId) {
        log.info("Fetching department members for managerEmployeeId: {}", managerEmployeeId);

        Employee manager = employeeRepository.findById(managerEmployeeId)
            .orElseThrow(() -> new ResourceNotFoundException(
                "Manager not found with id: " + managerEmployeeId
            ));

        if (Boolean.TRUE.equals(manager.getIsDeleted())) {
            throw new ResourceNotFoundException("Manager has been deleted");
        }

        Integer deptId = manager.getDeptId();
        if (deptId == null) {
            return List.of();
        }

        List<Employee> departmentEmployees = employeeRepository.findByDeptId(deptId);

        return departmentEmployees.stream()
            .filter(emp -> !Boolean.TRUE.equals(emp.getIsDeleted()))
            .filter(emp -> !emp.getId().equals(managerEmployeeId))
            .map(this::toEmployeeResponse)
            .collect(Collectors.toList());
    }

    private EmployeeResponse toEmployeeResponse(Employee employee) {
        String username = null;
        String email = null;

        if (employee.getUserId() != null) {
            Optional<User> userOpt = userRepository.findById(employee.getUserId());
            if (userOpt.isPresent()) {
                username = userOpt.get().getUsername();
                email = userOpt.get().getEmail();
            }
        }

        Department department = null;
        if (employee.getDeptId() != null) {
            department = departmentRepository.findById(employee.getDeptId()).orElse(null);
        }

        Employee departmentHead = findDepartmentHead(employee.getDeptId(), employee.getId());

        return EmployeeResponse.builder()
            .id(employee.getId())
            .userId(employee.getUserId())
            .deptId(employee.getDeptId())
            .deptName(department != null ? department.getDeptName() : null)
            .fullName(employee.getFullName())
            .gender(employee.getGender())
            .dob(employee.getDob())
            .phoneNumber(employee.getPhoneNumber())
            .address(employee.getAddress())
            .hireDate(employee.getHireDate())
            .basicSalary(employee.getBasicSalary())
            .status(employee.getStatus())
            .roleInDept(employee.getRoleInDept())
            .positionId(employee.getPosition() != null ? employee.getPosition().getId() : null)
            .positionName(employee.getPosition() != null ? employee.getPosition().getPositionName() : null)
            .username(username)
            .email(email)
            .managerId(departmentHead != null ? departmentHead.getId() : null)
            .managerName(departmentHead != null ? departmentHead.getFullName() : null)
            .build();
    }

    private Employee findDepartmentHead(Integer deptId, Integer currentEmployeeId) {
        if (deptId == null) {
            return null;
        }

        List<Employee> employees = employeeRepository.findByDeptIdAndStatus(deptId, EmployeeStatus.ACTIVE);

        return employees.stream()
            .filter(e -> !Boolean.TRUE.equals(e.getIsDeleted()))
            .filter(e -> e.getRoleInDept() == RoleInDepartment.HEAD)
            .filter(e -> currentEmployeeId == null || !e.getId().equals(currentEmployeeId))
            .findFirst()
            .orElse(null);
    }
}