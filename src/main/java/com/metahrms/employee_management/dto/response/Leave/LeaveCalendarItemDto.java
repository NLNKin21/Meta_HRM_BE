package com.metahrms.employee_management.dto.response.Leave;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
public class LeaveCalendarItemDto {
    private Long leaveRequestId;
    private Integer employeeId;

    private String employeeName;     // thêm
   

    private String leaveTypeName;
    private LocalDate startDate;
    private LocalDate endDate;
    private BigDecimal totalDays;
    private String status;
}