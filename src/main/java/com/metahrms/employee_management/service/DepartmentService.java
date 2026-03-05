package com.metahrms.employee_management.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.metahrms.employee_management.dto.request.Department.DepartmentDto;
import com.metahrms.employee_management.dto.response.Department.DepartmentResponse;
import com.metahrms.employee_management.dto.response.Department.DepartmentSummaryDto;
// import com.metahrms.employee_management.entity.Employee;
import com.metahrms.employee_management.entity.Department;
import com.metahrms.employee_management.repository.DepartmentRepository;
import com.metahrms.employee_management.repository.EmployeeRepository;
import com.metahrms.employee_management.enums.RoleInDepartment;

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

    public DepartmentResponse createDepartment(DepartmentDto createDto) {

        departmentRepository.findByDeptName(createDto.getDeptName())
            .ifPresent(d -> { throw new RuntimeException("Department name already exists"); });

        Department department = new Department();
        department.setDeptName(createDto.getDeptName());
        department.setIsDeleted(false);


        Department saved = departmentRepository.save(department);
        return toDepartmentResponse(saved);
    }

    public DepartmentResponse updateDepartment(Integer id, DepartmentDto updateDto) {

        Department department = departmentRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Department not found with id: " + id));

        if (department.getIsDeleted()) {
            throw new RuntimeException("Cannot update deleted department");
        }

        departmentRepository.findByDeptName(updateDto.getDeptName())
            .filter(d -> !d.getId().equals(id))
            .ifPresent(d -> { throw new RuntimeException("Department name already exists"); });

    department.setDeptName(updateDto.getDeptName());
        Department updated = departmentRepository.save(department);
        return toDepartmentResponse(updated);
    }

    public void deleteDepartment(Integer id) {
        Department department = departmentRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Department not found with id: " + id));

        department.setIsDeleted(true);
        departmentRepository.save(department);
    }

    public DepartmentResponse getDepartmentById(Integer id) {
        Department department = departmentRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Department not found with id: " + id));

        return toDepartmentResponse(department);
    }

    // public List<DepartmentResponse> getAllDepartments() {
    //     return departmentRepository.findByIsDeletedFalse().stream()
    //             .map(this::toDepartmentResponse)
    //             .collect(Collectors.toList());
    // }

     public List<DepartmentSummaryDto> getDepartmentSummaries() { // Hoàn thiện hàm này
        return departmentRepository.findByIsDeletedFalse().stream()
                .map(dept -> {
                    Long count = employeeRepository.countByDeptId(dept.getId());
                    String managerName = null;
                    var headOpt = employeeRepository.findFirstByDeptIdAndRoleInDept(dept.getId(), RoleInDepartment.HEAD); // Sử dụng hàm đã tạo
                    if (headOpt.isPresent()) managerName = headOpt.get().getFullName();
                    return new DepartmentSummaryDto(dept.getId(), dept.getDeptName(), managerName, count, dept.getCreatedAt());
                })
                .collect(Collectors.toList());
    }

    private DepartmentResponse toDepartmentResponse(Department department) {
        return DepartmentResponse.builder()
                .id(department.getId())
                .deptName(department.getDeptName())
                .createdAt(department.getCreatedAt())
                .build();
    }
}
