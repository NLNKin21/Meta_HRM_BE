package com.metahrms.employee_management.dto.request.Attendance.management;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApproveAttendanceRequest {

    private String note;
}