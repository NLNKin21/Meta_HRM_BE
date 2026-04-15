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
public class UpdateShiftRequest {

    @Size(max = 100)
    private String name;

    @Size(max = 50)
    private String code;

    private LocalTime startTime;

    private LocalTime endTime;

    @Min(0) @Max(120)
    private Integer lateThreshold;

    @Min(0) @Max(120)
    private Integer earlyLeaveThreshold;

    @Min(0) @Max(120)
    private Integer checkInStartBefore;

    @Min(0) @Max(240)
    private Integer checkInEndAfter;

    private List<Integer> workDays;

    @Min(0) @Max(180)
    private Integer breakDuration;

    @Size(max = 1000)
    private String description;

    @Size(max = 20)
    private String color;

    private Boolean isActive;
}