package com.metahrms.employee_management.dto.response;

import com.metahrms.employee_management.enums.EmployeeStatus;
import com.metahrms.employee_management.enums.Gender;
import com.metahrms.employee_management.enums.RoleInDepartment;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmployeeProfileResponseDto {

    private Integer id;
    private Integer userId;
    private Integer deptId;
    private String fullName;
    private Gender gender;
    private LocalDate dob;
    private String phoneNumber;
    private String address;
    private LocalDate hireDate;
    private BigDecimal basicSalary;
    private EmployeeStatus status;
    private RoleInDepartment roleInDept;

    private Integer positionId;
    private String positionName;

    // Trưởng phòng / người duyệt cấp quản lý
    private Integer managerId;
    private String managerName;
}