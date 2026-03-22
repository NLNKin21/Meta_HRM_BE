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
    ContractRepository contractRepository;  // ✅ Thêm
    CloudinaryService cloudinaryService;    // ✅ Thêm

    /**
     * Get employees with filtering and pagination
     */
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

    /**
     * Get employee by ID
     */
    public EmployeeResponse getEmployeeById(Integer id) {
        log.info("Fetching employee with id: {}", id);

        Employee employee = employeeRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Employee not found with id: " + id));

        if (Boolean.TRUE.equals(employee.getIsDeleted())) {
            throw new ResourceNotFoundException("Employee has been deleted");
        }

        return toEmployeeResponse(employee);
    }

    /**
     * Get employee by user ID
     */
    public EmployeeResponse getEmployeeByUserId(Integer userId) {
        log.info("Fetching employee with userId: {}", userId);

        Employee employee = employeeRepository.findByUserId(userId)
            .orElseThrow(() -> new ResourceNotFoundException("Employee not found with userId: " + userId));

        if (Boolean.TRUE.equals(employee.getIsDeleted())) {
            throw new ResourceNotFoundException("Employee has been deleted");
        }

        return toEmployeeResponse(employee);
    }

    /**
     * Create employee (simple - without contract)
     */
    @Transactional
    public EmployeeResponse createEmployee(EmployeeCreateDto createDto) {
        log.info("Creating employee for user: {}", createDto.getUserId());

        // Validate user exists
        userRepository.findById(createDto.getUserId())
            .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + createDto.getUserId()));

        // Check if user is already linked to an employee
        if (employeeRepository.findByUserId(createDto.getUserId()).isPresent()) {
            throw new IllegalStateException("User is already linked to an employee profile");
        }

        // Validate department exists
        departmentRepository.findById(createDto.getDeptId())
            .orElseThrow(() -> new ResourceNotFoundException("Department not found with id: " + createDto.getDeptId()));

        // Determine role
        RoleInDepartment roleInDept = createDto.getRoleInDept() != null 
            ? createDto.getRoleInDept() 
            : RoleInDepartment.STAFF;

        // Check if department already has a HEAD
        if (roleInDept == RoleInDepartment.HEAD) {
            Optional<Employee> existingHead = employeeRepository.findFirstByDeptIdAndRoleInDept(
                createDto.getDeptId(),
                RoleInDepartment.HEAD
            );

            if (existingHead.isPresent() && !Boolean.TRUE.equals(existingHead.get().getIsDeleted())) {
                throw new IllegalStateException("Department already has a head employee");
            }
        }

        // Create employee
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

    /**
     * Create employee with contract and file upload
     */
    @Transactional
    public EmployeeResponse createEmployeeWithContract(
            EmployeeCreateDto employeeData,
            ContractCreateDto contractData,
            MultipartFile contractFile) throws IOException {

        log.info("Creating employee with contract for user: {}", employeeData.getUserId());

        // 1. Create employee (reuse existing method)
        EmployeeResponse employeeResponse = createEmployee(employeeData);

        // 2. Create contract if provided
        if (contractData != null) {
            // Get the created employee
            Employee employee = employeeRepository.findById(employeeResponse.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found"));

            // Upload file to Cloudinary if provided
            String fileUrl = null;
            String fileKey = null;

            if (contractFile != null && !contractFile.isEmpty()) {
                fileUrl = cloudinaryService.uploadFile(contractFile);
                fileKey = cloudinaryService.extractPublicId(fileUrl);
                log.info("Contract file uploaded: {}", fileUrl);
            }

            // Create contract
            Contract contract = Contract.builder()
                .empId(employee.getId())  // ✅ Link to newly created employee
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

    /**
     * Update employee
     */
    @Transactional
    public EmployeeResponse updateEmployee(Integer id, EmployeeUpdateDto updateDto) {
        log.info("Updating employee with id: {}", id);

        Employee employee = employeeRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Employee not found with id: " + id));

        if (Boolean.TRUE.equals(employee.getIsDeleted())) {
            throw new IllegalStateException("Cannot update deleted employee");
        }

        // Validate department if provided
        if (updateDto.getDeptId() != null) {
            departmentRepository.findById(updateDto.getDeptId())
                .orElseThrow(() -> new ResourceNotFoundException("Department not found with id: " + updateDto.getDeptId()));
            employee.setDeptId(updateDto.getDeptId());
        }

        // Update fields
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

        // Update role with HEAD validation
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

                if (existingHead.isPresent() &&
                    !Boolean.TRUE.equals(existingHead.get().getIsDeleted()) &&
                    !existingHead.get().getId().equals(employee.getId())) {
                    throw new IllegalStateException("Department already has a head employee");
                }
            }
            employee.setRoleInDept(updateDto.getRoleInDept());
        }

        Employee updatedEmployee = employeeRepository.save(employee);
        log.info("Employee updated successfully: {}", id);

        return toEmployeeResponse(updatedEmployee);
    }

    /**
     * Delete employee (soft delete)
     */
    @Transactional
    public void deleteEmployee(Integer id) {
        log.info("Deleting employee with id: {}", id);

        Employee employee = employeeRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Employee not found with id: " + id));

        employee.setIsDeleted(true);
        employeeRepository.save(employee);
        
        log.info("Employee soft deleted successfully: {}", id);
    }

    /**
     * Get current user's employee info
     */
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

    /**
     * Convert Employee entity to EmployeeResponse DTO
     */
    private EmployeeResponse toEmployeeResponse(Employee employee) {
        // Get user information
        String username = null;
        if (employee.getUserId() != null) {
            username = userRepository.findById(employee.getUserId())
                .map(User::getUsername)
                .orElse(null);
        }

        // Get department information
        String departmentName = null;
        if (employee.getDeptId() != null) {
            departmentName = departmentRepository.findById(employee.getDeptId())
                .map(Department::getDeptName)
                .orElse(null);
        }

        return EmployeeResponse.builder()
            .id(employee.getId())
            .fullName(employee.getFullName())
            .gender(employee.getGender() != null ? employee.getGender().name() : null)
            .phoneNumber(employee.getPhoneNumber())
            .department(departmentName)
            .deptId(employee.getDeptId())
            .address(employee.getAddress())
            .dob(employee.getDob())
            .hireDate(employee.getHireDate())
            .roleInDept(employee.getRoleInDept() != null ? employee.getRoleInDept().name() : null)
            .status(employee.getStatus() != null ? employee.getStatus().name() : null)
            .username(username)
            .basicSalary(employee.getBasicSalary())  // ✅ Thêm nếu chưa có
            .createdAt(employee.getCreatedAt())
            .build();
    }
}