package com.metahrms.employee_management.dto.request.Department;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class DepartmentRequest {

    @NotBlank(message = "Department name is required")
    @Size(max = 100, message = "Department name must be less than 100 characters")
    private String deptName;

    @NotBlank(message = "Department code is required")
    @Size(max = 20, message = "Department code must be less than 20 characters")
    private String deptCode;

    private String description;
    
    private Long parentId; // For sub-departments

    private Long managerId; // Head of Department
}