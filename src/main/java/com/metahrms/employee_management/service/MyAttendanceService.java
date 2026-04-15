package com.metahrms.employee_management.service;


import com.metahrms.employee_management.dto.request.Attendance.AttendanceRecordDTO;
import com.metahrms.employee_management.dto.response.Attendance.MyAttendanceSummaryDTO;
import com.metahrms.employee_management.dto.response.Attendance.MyMonthlyCalendarDTO;
import com.metahrms.employee_management.dto.response.Attendance.MyTodayStatusDTO;

import java.time.LocalDate;
import java.util.List;

/**
 * Service cho Employee self-service attendance
 * Tất cả methods nhận userId từ JWT (không phải employeeId)
 * → Service tự resolve userId → Employee
 */
public interface MyAttendanceService {

    /**
     * Lấy status hôm nay
     * @param userId từ SecurityUtils.getCurrentUserId()
     */
    MyTodayStatusDTO getTodayStatus(Integer userId);

    /**
     * Lấy lịch sử attendance theo khoảng ngày
     */
    List<AttendanceRecordDTO> getHistory(Integer userId, LocalDate startDate, LocalDate endDate);

    /**
     * Lấy bảng công tháng (calendar view)
     */
    MyMonthlyCalendarDTO getMonthlyCalendar(Integer userId, int year, int month);

    /**
     * Lấy thống kê tháng
     */
    MyAttendanceSummaryDTO getMonthlySummary(Integer userId, int year, int month);
}