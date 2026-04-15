package com.metahrms.employee_management.dto.request.Attendance.management;

import com.metahrms.employee_management.enums.Attendance.AttendanceStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EditAttendanceRequest {

    private LocalDateTime checkInTime;

    private LocalDateTime checkOutTime;

    private AttendanceStatus status;

    private Integer lateMinutes;

    private Integer earlyLeaveMinutes;

    /**
     * Lý do chỉnh sửa - BẮT BUỘC
     */
    @NotBlank(message = "Reason is required when editing attendance")
    @Size(max = 500, message = "Reason must not exceed 500 characters")
    private String reason;

    private String note;
}