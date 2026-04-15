package com.metahrms.employee_management.dto.response.Attendance.management;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DepartmentDailyAttendanceDTO {

    private Integer deptId;
    private String deptName;
    private LocalDate date;
    private Integer totalEmployees;

    // Quick summary
    private Integer presentCount;
    private Integer absentCount;
    private Integer lateCount;
    private Integer earlyLeaveCount;
    private Integer notCheckedCount;
    private Integer leaveCount;

    // Chi tiết từng nhân viên
    private List<EmployeeAttendanceDTO> employees;
}
