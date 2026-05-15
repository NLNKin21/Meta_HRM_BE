package com.metahrms.employee_management.dto.response.Employee;

import lombok.*;
import lombok.experimental.FieldDefaults;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EmployeeContractDto {
    Integer id;
    String fullName;
    String employeeCode;
    String departmentName;
}