package com.metahrms.employee_management.controller;

import com.metahrms.employee_management.dto.request.Attendance.AttendanceRecordDTO;
import com.metahrms.employee_management.dto.request.common.ApiResponse;
import com.metahrms.employee_management.dto.response.Attendance.MyAttendanceSummaryDTO;
import com.metahrms.employee_management.dto.response.Attendance.MyMonthlyCalendarDTO;
import com.metahrms.employee_management.dto.response.Attendance.MyTodayStatusDTO;
import com.metahrms.employee_management.service.MyAttendanceService;
import com.metahrms.employee_management.util.SecurityUtils;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/attendance/me")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('EMPLOYEE', 'ADMIN', 'HR', 'MANAGER')")
@Tag(name = "My Attendance", description = "Employee self-service attendance APIs")
public class MyAttendanceController {

    private final MyAttendanceService myAttendanceService;

    // ============================================
    // TODAY STATUS
    // ============================================

    @GetMapping("/today")
    @Operation(
        summary = "Get today's attendance status",
        description = """
            Get current employee's attendance status for today.
            
            Returns:
            - hasCheckedIn / hasCheckedOut
            - checkIn/Out time
            - Current status (PRESENT, LATE, NOT_CHECKED...)
            - Work hours until now (if checked in but not out)
            - Shift info (start/end time)
            - Location names
            
            Note: Employee ID is taken from JWT token automatically.
            """
    )
    public ResponseEntity<ApiResponse<MyTodayStatusDTO>> getTodayStatus() {
        Integer userId = SecurityUtils.getCurrentUserId();
        log.info("[MY-ATTENDANCE] GET /me/today userId={}", userId);

        try {
            MyTodayStatusDTO result = myAttendanceService.getTodayStatus(userId);
            return ResponseEntity.ok(ApiResponse.success(result, "Today's status retrieved"));
        } catch (Exception e) {
            log.error("[MY-ATTENDANCE] Error getting today status for userId={}", userId, e);
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    // ============================================
    // HISTORY
    // ============================================

    @GetMapping("/history")
    @Operation(
        summary = "Get attendance history",
        description = """
            Get attendance records for a date range.
            
            Limits:
            - Max 365 days range
            - startDate must be <= endDate
            
            Returns list of attendance records sorted by date.
            """
    )
    public ResponseEntity<ApiResponse<List<AttendanceRecordDTO>>> getHistory(
            @Parameter(description = "Start date (yyyy-MM-dd)", required = true)
            @RequestParam("startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,

            @Parameter(description = "End date (yyyy-MM-dd)", required = true)
            @RequestParam("endDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate
    ) {
        Integer userId = SecurityUtils.getCurrentUserId();
        log.info("[MY-ATTENDANCE] GET /me/history userId={}, {} to {}", userId, startDate, endDate);

        try {
            List<AttendanceRecordDTO> result = myAttendanceService.getHistory(userId, startDate, endDate);
            return ResponseEntity.ok(
                    ApiResponse.success(result, "Retrieved " + result.size() + " record(s)")
            );
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            log.error("[MY-ATTENDANCE] Error getting history for userId={}", userId, e);
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    // ============================================
    // MONTHLY CALENDAR
    // ============================================

    @GetMapping("/monthly")
    @Operation(
        summary = "Get monthly attendance calendar",
        description = """
            Get full month calendar view with attendance data for each day.
            
            Returns:
            - List of all days in month with attendance status
            - Shift info (work days, start/end time)
            - Monthly summary statistics
            
            isWorkDay = true  → Scheduled work day (based on shift)
            isWorkDay = false → Weekend or day off
            status = null     → Future date or non-work day
            status = ABSENT   → Work day passed without attendance
            """
    )
    public ResponseEntity<ApiResponse<MyMonthlyCalendarDTO>> getMonthlyCalendar(
            @Parameter(description = "Year (e.g. 2024)", required = true)
            @RequestParam("year") int year,

            @Parameter(description = "Month 1-12", required = true)
            @RequestParam("month") int month
    ) {
        Integer userId = SecurityUtils.getCurrentUserId();
        log.info("[MY-ATTENDANCE] GET /me/monthly userId={}, year={}, month={}", userId, year, month);

        try {
            MyMonthlyCalendarDTO result = myAttendanceService.getMonthlyCalendar(userId, year, month);
            return ResponseEntity.ok(ApiResponse.success(result, "Monthly calendar retrieved"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            log.error("[MY-ATTENDANCE] Error getting monthly calendar for userId={}", userId, e);
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    // ============================================
    // MONTHLY SUMMARY
    // ============================================

    @GetMapping("/summary")
    @Operation(
        summary = "Get monthly attendance summary",
        description = """
            Get attendance statistics for a specific month.
            
            Returns:
            - Present/Absent/Late/EarlyLeave days count
            - Total work hours and overtime hours
            - Attendance rate (%) and punctuality rate (%)
            - Today's check-in/out status (if current month)
            """
    )
    public ResponseEntity<ApiResponse<MyAttendanceSummaryDTO>> getMonthlySummary(
            @Parameter(description = "Year (e.g. 2024)", required = true)
            @RequestParam("year") int year,

            @Parameter(description = "Month 1-12", required = true)
            @RequestParam("month") int month
    ) {
        Integer userId = SecurityUtils.getCurrentUserId();
        log.info("[MY-ATTENDANCE] GET /me/summary userId={}, year={}, month={}", userId, year, month);

        try {
            MyAttendanceSummaryDTO result = myAttendanceService.getMonthlySummary(userId, year, month);
            return ResponseEntity.ok(ApiResponse.success(result, "Monthly summary retrieved"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            log.error("[MY-ATTENDANCE] Error getting monthly summary for userId={}", userId, e);
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    // ============================================
    // CURRENT MONTH SHORTCUT
    // ============================================

    @GetMapping("/this-month")
    @Operation(
        summary = "Get current month calendar (shortcut)",
        description = "Shortcut to get current month calendar without specifying year/month"
    )
    public ResponseEntity<ApiResponse<MyMonthlyCalendarDTO>> getCurrentMonth() {
        Integer userId = SecurityUtils.getCurrentUserId();
        LocalDate today = LocalDate.now();

        log.info("[MY-ATTENDANCE] GET /me/this-month userId={}", userId);

        try {
            MyMonthlyCalendarDTO result = myAttendanceService.getMonthlyCalendar(
                    userId, today.getYear(), today.getMonthValue()
            );
            return ResponseEntity.ok(ApiResponse.success(result, "Current month calendar retrieved"));
        } catch (Exception e) {
            log.error("[MY-ATTENDANCE] Error getting current month for userId={}", userId, e);
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage()));
        }
    }
}
