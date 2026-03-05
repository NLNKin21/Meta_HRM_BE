package com.metahrms.employee_management.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.metahrms.employee_management.dto.request.Employee.EmployeeCreateDto;
import com.metahrms.employee_management.dto.request.Employee.EmployeeFilterDto;
import com.metahrms.employee_management.dto.request.Employee.EmployeeUpdateDto;
import com.metahrms.employee_management.dto.response.PagedResponse;
import com.metahrms.employee_management.dto.response.Employee.EmployeeResponse;
// import com.metahrms.employee_management.dto.response.PerformanceStatisticsResponse;
import com.metahrms.employee_management.entity.Department;
import com.metahrms.employee_management.entity.Employee;
import com.metahrms.employee_management.entity.User;
import com.metahrms.employee_management.enums.EmployeeStatus;
import com.metahrms.employee_management.enums.RoleInDepartment;
import com.metahrms.employee_management.enums.UserRole;
import com.metahrms.employee_management.repository.DepartmentRepository;
import com.metahrms.employee_management.repository.EmployeeRepository;
import com.metahrms.employee_management.repository.UserRepository;
// import com.metahrms.employee_management.repository.AttendanceRepository;
// import com.metahrms.employee_management.repository.TaskAssignmentRepository;
import com.metahrms.employee_management.repository.LeaveRequestRepository;
import com.metahrms.employee_management.specification.EmployeeSpecification;
import com.metahrms.employee_management.util.SecurityUtils;
import com.metahrms.employee_management.enums.LeaveStatus;
import com.metahrms.employee_management.enums.LeaveType;
import org.springframework.beans.factory.annotation.Value;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.temporal.ChronoUnit;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EmployeeService {
    final EmployeeRepository employeeRepository;
    final UserRepository userRepository;
    final DepartmentRepository departmentRepository;
    // final AttendanceRepository attendanceRepository;
    // final TaskAssignmentRepository taskAssignmentRepository;
    // final LeaveRequestRepository leaveRequestRepository;

    // @Value("${leave.annual.default-days}")
    // int defaultAnnualLeaveDays;

    public PagedResponse<EmployeeResponse> getEmployees(EmployeeFilterDto filterDto) {
        // Build specification for filtering
        Specification<Employee> spec = EmployeeSpecification.filterEmployees(
            filterDto.getStatus(),
            filterDto.getDeptId(),
            filterDto.getHireDate(),
            filterDto.getSearch()
        );

        // Create pageable with sorting by createdAt descending
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

    public EmployeeResponse getEmployeeById(Integer id) {
        Employee employee = employeeRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Employee not found with id: " + id));

        if (employee.getIsDeleted()) {
            throw new RuntimeException("Employee has been deleted");
        }

        return toEmployeeResponse(employee);
    }

    public EmployeeResponse getEmployeeByUserId(Integer userId) {
        Employee employee = employeeRepository.findByUserId(userId)
            .orElseThrow(() -> new RuntimeException("Employee not found with userId: " + userId));

        if (employee.getIsDeleted()) {
            throw new RuntimeException("Employee has been deleted");
        }

        return toEmployeeResponse(employee);
    }

    public EmployeeResponse createEmployee(EmployeeCreateDto createDto) {
        // Validate user exists
        userRepository.findById(createDto.getUserId())
            .orElseThrow(() -> new RuntimeException("User not found with id: " + createDto.getUserId()));

        // Validate department exists
        Department dept = departmentRepository.findById(createDto.getDeptId())
            .orElseThrow(() -> new RuntimeException("Department not found with id: " + createDto.getDeptId()));

        // Determine the role for the new employee
        RoleInDepartment roleInDept = createDto.getRoleInDept() != null ? createDto.getRoleInDept() : RoleInDepartment.STAFF;

        // Check if department already has a HEAD when trying to assign HEAD role
        if (roleInDept == RoleInDepartment.HEAD) {
            Optional<Employee> existingHead = employeeRepository.findFirstByDeptIdAndRoleInDept(
                createDto.getDeptId(),
                RoleInDepartment.HEAD
            );

            if (existingHead.isPresent() && !existingHead.get().getIsDeleted()) {
                throw new RuntimeException("Department already has a head employee");
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
        return toEmployeeResponse(savedEmployee);
    }

    public EmployeeResponse updateEmployee(Integer id, EmployeeUpdateDto updateDto) {
        Employee employee = employeeRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Employee not found with id: " + id));

        if (employee.getIsDeleted()) {
            throw new RuntimeException("Cannot update deleted employee");
        }

        // Validate department if provided
        if (updateDto.getDeptId() != null) {
            departmentRepository.findById(updateDto.getDeptId())
                .orElseThrow(() -> new RuntimeException("Department not found with id: " + updateDto.getDeptId()));
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
            // Check if trying to change role to HEAD
            if (updateDto.getRoleInDept() == RoleInDepartment.HEAD &&
                employee.getRoleInDept() != RoleInDepartment.HEAD) {

                // Use the current or new department ID
                Integer targetDeptId = updateDto.getDeptId() != null ? updateDto.getDeptId() : employee.getDeptId();

                Optional<Employee> existingHead = employeeRepository.findFirstByDeptIdAndRoleInDept(
                    targetDeptId,
                    RoleInDepartment.HEAD
                );

                if (existingHead.isPresent() &&
                    !existingHead.get().getIsDeleted() &&
                    !existingHead.get().getId().equals(employee.getId())) {
                    throw new RuntimeException("Department already has a head employee");
                }
            }
            employee.setRoleInDept(updateDto.getRoleInDept());
        }

        Employee updatedEmployee = employeeRepository.save(employee);
        return toEmployeeResponse(updatedEmployee);
    }

    public void deleteEmployee(Integer id) {
        Employee employee = employeeRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Employee not found with id: " + id));

        // Soft delete
        employee.setIsDeleted(true);
        employeeRepository.save(employee);
    }

    public EmployeeResponse getCurrentUserEmployee() {
        Integer currentUserId = SecurityUtils.getCurrentUserId();
        if (currentUserId == null) {
            throw new RuntimeException("User not authenticated");
        }

        Employee employee = employeeRepository.findByUserId(currentUserId)
            .orElseThrow(() -> new RuntimeException("Employee not found for current user"));

        if (employee.getIsDeleted()) {
            throw new RuntimeException("Employee has been deleted");
        }

        return toEmployeeResponse(employee);
    }

    public EmployeeResponse toEmployeeResponse(Employee employee) {
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
            .createdAt(employee.getCreatedAt())
            .build();
    }

    // public PerformanceStatisticsResponse getEmployeePerformanceStatistics() {
    //     Integer currentUserId = SecurityUtils.getCurrentUserId();
    //     if (currentUserId == null) {
    //         throw new RuntimeException("User not authenticated");
    //     }

    //     Employee employee = employeeRepository.findByUserId(currentUserId)
    //         .orElseThrow(() -> new RuntimeException("Employee not found for current user"));

    //     if (employee.getIsDeleted()) {
    //         throw new RuntimeException("Employee has been deleted");
    //     }

    //     Integer empId = employee.getId();
    //     YearMonth currentMonth = YearMonth.now();
    //     int month = currentMonth.getMonthValue();
    //     int year = currentMonth.getYear();

    //     var attendances = attendanceRepository.findByEmpIdAndMonthAndYear(empId, month, year);
    //     Integer workingDaysThisMonth = attendances.size();

    //     Double overtimeHoursThisMonth = attendances.stream()
    //         .filter(a -> a.getOvertimeHours() != null)
    //         .mapToDouble(a -> a.getOvertimeHours().doubleValue())
    //         .sum();

    //     var allAssignments = taskAssignmentRepository.findByEmpId(empId);

    //     LocalDate startOfMonth = currentMonth.atDay(1);
    //     LocalDate endOfMonth = currentMonth.atEndOfMonth();

    //     var assignmentsThisMonth = allAssignments.stream()
    //         .filter(ta -> ta.getCreatedAt() != null)
    //         .filter(ta -> {
    //             LocalDate createdDate = ta.getCreatedAt().toLocalDate();
    //             return !createdDate.isBefore(startOfMonth) && !createdDate.isAfter(endOfMonth);
    //         })
    //         .toList();

    //     Integer totalTasksThisMonth = assignmentsThisMonth.size();

    //     Integer completedTasksThisMonth = (int) assignmentsThisMonth.stream()
    //         .filter(ta -> ta.getCompletedDate() != null)
    //         .count();

    //     Double taskCompletionRate = totalTasksThisMonth > 0
    //         ? (completedTasksThisMonth * 100.0) / totalTasksThisMonth
    //         : 0.0;

    //     var annualLeaveRequests = leaveRequestRepository.findByEmpIdAndStatus(empId, LeaveStatus.APPROVED)
    //         .stream()
    //         .filter(lr -> lr.getLeaveType() == LeaveType.ANNUAL_LEAVE)
    //         .filter(lr -> lr.getStartDate().getYear() == year)
    //         .toList();

    //     long totalApprovedLeaveDays = annualLeaveRequests.stream()
    //         .mapToLong(lr -> ChronoUnit.DAYS.between(lr.getStartDate(), lr.getEndDate()) + 1)
    //         .sum();

    //     Integer remainingLeaveDays = defaultAnnualLeaveDays - (int) totalApprovedLeaveDays;

    //     Integer pendingLeaveRequests = (int) leaveRequestRepository.findByEmpIdAndStatus(empId, LeaveStatus.PENDING)
    //         .size();

    //     return PerformanceStatisticsResponse.builder()
    //         .workingDaysThisMonth(workingDaysThisMonth)
    //         .completedTasksThisMonth(completedTasksThisMonth)
    //         .totalTasksThisMonth(totalTasksThisMonth)
    //         .taskCompletionRate(Math.round(taskCompletionRate * 100.0) / 100.0)
    //         .remainingLeaveDays(remainingLeaveDays)
    //         .pendingLeaveRequests(pendingLeaveRequests)
    //         .overtimeHoursThisMonth(Math.round(overtimeHoursThisMonth * 100.0) / 100.0)
    //         .build();
    // }

    // public List<EmployeeResponse> getEmployeesWithoutKpiResults(EmployeeWithoutKpiFilterDto filterDto) {
    //     Integer currentUserId = SecurityUtils.getCurrentUserId();
    //     if (currentUserId == null) {
    //         throw new RuntimeException("User not authenticated");
    //     }

    //     String userRole = SecurityUtils.getCurrentUserRole();
    //     if (userRole == null) {
    //         throw new RuntimeException("User role not found");
    //     }

    //     // Validate kpiPeriodId is provided
    //     if (filterDto.getKpiPeriodId() == null) {
    //         throw new RuntimeException("KPI Period ID is required");
    //     }

    //     Integer deptId = filterDto.getDeptId();

    //     // Access control: Department heads can only see their department
    //     if (UserRole.valueOf(userRole) != UserRole.ADMIN) {
    //         Employee currentEmployee = employeeRepository.findByUserId(currentUserId)
    //             .orElseThrow(() -> new RuntimeException("Employee not found for current user"));

    //         if (currentEmployee.getIsDeleted()) {
    //             throw new RuntimeException("Employee has been deleted");
    //         }

    //         // Check if user is a department head
    //         if (currentEmployee.getRoleInDept() != RoleInDepartment.HEAD) {
    //             throw new RuntimeException("Access denied. Only department heads and admins can access this resource");
    //         }

    //         // Override deptId with current employee's department
    //         deptId = currentEmployee.getDeptId();
    //     }

    //     // Fetch employees without KPI results for the specified period
    //     List<Employee> employees = employeeRepository.findEmployeesWithoutKpiResults(
    //         filterDto.getKpiPeriodId(),
    //         deptId
    //     );

    //     return employees.stream()
    //         .map(this::toEmployeeResponse)
    //         .collect(Collectors.toList());
    // }
}
