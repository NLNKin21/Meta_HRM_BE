package com.metahrms.employee_management.dto.response.shift;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ShiftEmployeeDTO {

    private Integer employeeId;
    private String fullName;
    private Integer deptId;
    private String deptName;
    private String positionName;
    private String status;
}