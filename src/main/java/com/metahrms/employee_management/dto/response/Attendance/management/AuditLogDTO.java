package com.metahrms.employee_management.dto.response.Attendance.management;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuditLogDTO {

    private Integer id;
    private Integer attendanceId;
    private String action;
    private String oldValue;
    private String newValue;
    private String reason;
    private Integer performedBy;
    private String performedByName;
    private LocalDateTime performedAt;
}