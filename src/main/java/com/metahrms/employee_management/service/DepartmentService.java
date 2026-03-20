package com.metahrms.employee_management.service;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.metahrms.employee_management.dto.request.Department.DepartmentDto;
import com.metahrms.employee_management.dto.response.Department.DepartmentResponse;
import com.metahrms.employee_management.dto.response.Department.DepartmentSummaryDto;
import com.metahrms.employee_management.dto.response.Employee.EmployeeSummaryDto;
import com.metahrms.employee_management.entity.Department;
import com.metahrms.employee_management.entity.Employee;
import com.metahrms.employee_management.repository.DepartmentRepository;
import com.metahrms.employee_management.repository.EmployeeRepository;
import com.metahrms.employee_management.enums.RoleInDepartment;
import com.metahrms.employee_management.exception.ResourceNotFoundException;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.AccessLevel;

@Service
@RequiredArgsConstructor
@Transactional
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class DepartmentService {

    DepartmentRepository departmentRepository;
    EmployeeRepository employeeRepository;

    /**
     * Tạo phòng ban mới
     */
    public DepartmentResponse createDepartment(DepartmentDto createDto) {
        departmentRepository.findByDeptNameAndIsDeletedFalse(createDto.getDeptName())
            .ifPresent(d -> {
                throw new RuntimeException("Tên phòng ban đã tồn tại: " + createDto.getDeptName());
            });

        Department department = new Department();
        department.setDeptName(createDto.getDeptName());
        department.setIsDeleted(false);

        Department saved = departmentRepository.save(department);
        return toDepartmentResponse(saved);
    }

    /**
     * Cập nhật phòng ban
     */
    public DepartmentResponse updateDepartment(Integer id, DepartmentDto updateDto) {
        Department department = departmentRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy phòng ban với id: " + id));

        if (Boolean.TRUE.equals(department.getIsDeleted())) {
            throw new RuntimeException("Không thể cập nhật phòng ban đã bị xóa");
        }

        departmentRepository.findByDeptNameAndIsDeletedFalse(updateDto.getDeptName())
            .filter(d -> !d.getId().equals(id))
            .ifPresent(d -> {
                throw new RuntimeException("Tên phòng ban đã tồn tại: " + updateDto.getDeptName());
            });

        department.setDeptName(updateDto.getDeptName());
        Department updated = departmentRepository.save(department);
        return toDepartmentResponse(updated);
    }

    /**
     * Xóa mềm phòng ban
     */
    public void deleteDepartment(Integer id) {
        Department department = departmentRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy phòng ban với id: " + id));

        if (Boolean.TRUE.equals(department.getIsDeleted())) {
            throw new RuntimeException("Phòng ban đã bị xóa trước đó");
        }

        department.setIsDeleted(true);
        departmentRepository.save(department);
    }

    /**
     * Lấy chi tiết phòng ban theo ID
     */
    @Transactional(readOnly = true)
    public DepartmentResponse getDepartmentById(Integer id) {
        Department department = departmentRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy phòng ban với id: " + id));

        if (Boolean.TRUE.equals(department.getIsDeleted())) {
            throw new ResourceNotFoundException("Phòng ban đã bị xóa");
        }

        return toDepartmentResponse(department);
    }

    /**
     * Lấy danh sách nhân viên theo phòng ban (sắp xếp theo cấp bậc)
     */
    @Transactional(readOnly = true)
    public List<EmployeeSummaryDto> getEmployeesByDepartmentId(Integer deptId) {
        // Kiểm tra phòng ban tồn tại
        Department department = departmentRepository.findById(deptId)
            .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy phòng ban với id: " + deptId));

        if (Boolean.TRUE.equals(department.getIsDeleted())) {
            throw new ResourceNotFoundException("Phòng ban đã bị xóa");
        }

        // Lấy danh sách nhân viên của phòng ban (đã sắp xếp theo levelOrder)
        List<Employee> employees = employeeRepository.findByDeptIdAndIsDeletedFalseOrderByPositionLevel(deptId);

        if (employees.isEmpty()) {
            return Collections.emptyList();
        }

        // Tìm ID quản lý phòng ban
        Integer managerId = employeeRepository
            .findFirstByDeptIdAndRoleInDept(deptId, RoleInDepartment.HEAD)
            .map(Employee::getId)
            .orElse(null);

        // Map sang DTO
        return employees.stream()
            .map(emp -> toEmployeeSummaryDto(emp, managerId))
            .collect(Collectors.toList());
    }

    /**
     * Lấy danh sách tổng hợp phòng ban
     */
    @Transactional(readOnly = true)
    public List<DepartmentSummaryDto> getDepartmentSummaries() {
        return departmentRepository.findByIsDeletedFalse().stream()
            .map(dept -> {
                Long count = employeeRepository.countByDeptId(dept.getId());
                
                String managerName = employeeRepository
                    .findFirstByDeptIdAndRoleInDept(dept.getId(), RoleInDepartment.HEAD)
                    .map(Employee::getFullName)
                    .orElse(null);

                return new DepartmentSummaryDto(
                    dept.getId(),
                    dept.getDeptName(),
                    managerName,
                    count,
                    dept.getCreatedAt()
                );
            })
            .collect(Collectors.toList());
    }

    /**
     * Chuyển đổi Department entity sang DepartmentResponse DTO
     */
    private DepartmentResponse toDepartmentResponse(Department department) {
        Long employeeCount = employeeRepository.countByDeptId(department.getId());

        String managerName = employeeRepository
            .findFirstByDeptIdAndRoleInDept(department.getId(), RoleInDepartment.HEAD)
            .map(Employee::getFullName)
            .orElse(null);

        Integer managerId = employeeRepository
            .findFirstByDeptIdAndRoleInDept(department.getId(), RoleInDepartment.HEAD)
            .map(Employee::getId)
            .orElse(null);

        return DepartmentResponse.builder()
            .id(department.getId())
            .deptName(department.getDeptName())
            .managerName(managerName)
            .managerId(managerId)
            .employeeCount(employeeCount)
            .createdAt(department.getCreatedAt())
            .build();
    }

    /**
     * Chuyển đổi Employee entity sang EmployeeSummaryDto
     */
    private EmployeeSummaryDto toEmployeeSummaryDto(Employee employee, Integer managerId) {
        Integer positionLevel = null;
        String positionName = null;
        Integer positionId = null;
        
        if (employee.getPosition() != null) {
            positionId = employee.getPosition().getId();
            positionName = employee.getPosition().getPositionName();
            positionLevel = employee.getPosition().getLevelOrder();
        }
        
        return EmployeeSummaryDto.builder()
            .id(employee.getId())
            .fullName(employee.getFullName())
            .gender(employee.getGender())
            .phoneNumber(employee.getPhoneNumber())
            .hireDate(employee.getHireDate())
            .status(employee.getStatus())
            .positionId(positionId)
            .positionName(positionName)
            .positionLevel(positionLevel)
            .roleInDept(employee.getRoleInDept())
            .isManager(employee.getId().equals(managerId))
            .build();
    }
}