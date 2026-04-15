package com.metahrms.employee_management.dto.response.Attendance;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MyMonthlyCalendarDTO {

    private Integer year;
    private Integer month;
    private String monthName;           // "January", "February"...
    private Integer totalDaysInMonth;

    // Shift hiện tại của employee
    private String currentShiftName;
    private String currentShiftCode;
    private String shiftStartTime;
    private String shiftEndTime;
    private List<Integer> workDays;     // [1,2,3,4,5]

    // Danh sách ngày trong tháng
    private List<DayRecordDTO> days;

    // Tóm tắt nhanh
    private MyAttendanceSummaryDTO summary;
}