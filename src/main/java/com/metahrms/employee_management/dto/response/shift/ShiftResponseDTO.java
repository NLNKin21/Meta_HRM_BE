package com.metahrms.employee_management.dto.response.shift;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalTime;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ShiftResponseDTO {

    private Integer id;
    private String name;
    private String code;
    private LocalTime startTime;
    private LocalTime endTime;
    private Integer lateThreshold;
    private Integer earlyLeaveThreshold;
    private Integer checkInStartBefore;
    private Integer checkInEndAfter;
    private List<Integer> workDays;
    private Integer breakDuration;
    private String description;
    private String color;
    private Boolean isActive;
    private Double totalWorkHours;
    private Integer employeeCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}