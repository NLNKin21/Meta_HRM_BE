package com.metahrms.employee_management.dto.request.shift;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateShiftRequest {

    @NotBlank(message = "Shift name is required")
    @Size(max = 100, message = "Name must not exceed 100 characters")
    private String name;

    @Size(max = 50, message = "Code must not exceed 50 characters")
    private String code;

    @NotNull(message = "Start time is required")
    private LocalTime startTime;

    @NotNull(message = "End time is required")
    private LocalTime endTime;

    @Min(value = 0, message = "Late threshold must be >= 0")
    @Max(value = 120, message = "Late threshold must be <= 120")
    private Integer lateThreshold = 15;

    @Min(value = 0, message = "Early leave threshold must be >= 0")
    @Max(value = 120, message = "Early leave threshold must be <= 120")
    private Integer earlyLeaveThreshold = 15;

    @Min(value = 0)
    @Max(value = 120)
    private Integer checkInStartBefore = 30;

    @Min(value = 0)
    @Max(value = 240)
    private Integer checkInEndAfter = 120;

    /**
     * Ngày làm việc trong tuần: 1=Mon, 2=Tue, ..., 7=Sun
     * Ví dụ: [1,2,3,4,5] = Mon-Fri
     */
    private List<Integer> workDays;

    @Min(value = 0, message = "Break duration must be >= 0")
    @Max(value = 180, message = "Break duration must be <= 180")
    private Integer breakDuration = 60;

    @Size(max = 1000)
    private String description;

    @Size(max = 20)
    private String color;
}