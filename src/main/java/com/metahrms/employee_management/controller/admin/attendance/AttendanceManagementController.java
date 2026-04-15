package com.metahrms.employee_management.controller.admin.attendance;


import com.metahrms.employee_management.dto.request.Attendance.management.ApproveAttendanceRequest;
import com.metahrms.employee_management.dto.request.Attendance.management.EditAttendanceRequest;
import com.metahrms.employee_management.dto.request.Attendance.management.RejectAttendanceRequest;
import com.metahrms.employee_management.dto.request.common.ApiResponse;
import com.metahrms.employee_management.dto.response.Attendance.management.AuditLogDTO;
import com.metahrms.employee_management.dto.response.Attendance.management.DepartmentDailyAttendanceDTO;
import com.metahrms.employee_management.dto.response.Attendance.management.DepartmentMonthlyReportDTO;
import com.metahrms.employee_management.dto.response.Attendance.management.DepartmentSummaryDTO;
import com.metahrms.employee_management.service.AttendanceManagementService;
import com.metahrms.employee_management.util.SecurityUtils;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
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
@RequestMapping("/admin/attendance")
@RequiredArgsConstructor
@Tag(name = "Admin - Attendance Management",
     description = "APIs for managing employee attendance by department")
public class AttendanceManagementController {

    private final AttendanceManagementService attendanceManagementService;

    // ============================================
    // DAILY ATTENDANCE BY DEPARTMENT
    // ============================================

    @GetMapping("/department/{deptId}/daily")
    @PreAuthorize("hasAnyRole('ADMIN', 'HR')")
    @Operation(
        summary = "Get department daily attendance",
        description = """
            Get attendance records for all employees in a department on a specific date.
            
            Permissions:
            - ADMIN / HR: All departments
            - MANAGER: Only their own department (roleInDept = HEAD)
            
            Returns:
            - List of all employees with attendance status
            - Employees without records shown as ABSENT (past dates) or NOT_CHECKED
            - Summary counts (present, absent, late...)
            """
    )
    public ResponseEntity<ApiResponse<DepartmentDailyAttendanceDTO>> getDailyAttendance(
            @PathVariable("deptId") Integer deptId,

            @Parameter(description = "Date (yyyy-MM-dd), default = today")
            @RequestParam(name = "date",required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date
    ) {
        Integer userId = SecurityUtils.getCurrentUserId();
        LocalDate targetDate = date != null ? date : LocalDate.now();

        log.info("[ADMIN-ATTENDANCE] Daily: deptId={}, date={}, userId={}", deptId, targetDate, userId);

        try {
            DepartmentDailyAttendanceDTO result = attendanceManagementService
                    .getDepartmentDailyAttendance(deptId, targetDate, userId);
            return ResponseEntity.ok(ApiResponse.success(result, "Daily attendance retrieved"));
        } catch (SecurityException e) {
            return ResponseEntity.status(403).body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            log.error("[ADMIN-ATTENDANCE] Error getting daily attendance", e);
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    // ============================================
    // SUMMARY BY DEPARTMENT
    // ============================================

    @GetMapping("/department/{deptId}/summary")
    @PreAuthorize("hasAnyRole('ADMIN', 'HR', 'MANAGER')")
    @Operation(
        summary = "Get department attendance summary",
        description = "Get quick summary counts for a department on a specific date"
    )
    public ResponseEntity<ApiResponse<DepartmentSummaryDTO>> getDepartmentSummary(
            @PathVariable("deptId") Integer deptId,

            @Parameter(description = "Date (yyyy-MM-dd), default = today")
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date
    ) {
        Integer userId = SecurityUtils.getCurrentUserId();
        LocalDate targetDate = date != null ? date : LocalDate.now();

        log.info("[ADMIN-ATTENDANCE] Summary: deptId={}, date={}", deptId, targetDate);

        try {
            DepartmentSummaryDTO result = attendanceManagementService
                    .getDepartmentSummary(deptId, targetDate, userId);
            return ResponseEntity.ok(ApiResponse.success(result, "Summary retrieved"));
        } catch (SecurityException e) {
            return ResponseEntity.status(403).body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            log.error("[ADMIN-ATTENDANCE] Error getting summary", e);
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    // ============================================
    // MONTHLY REPORT BY DEPARTMENT
    // ============================================

    @GetMapping("/department/{deptId}/monthly")
    @PreAuthorize("hasAnyRole('ADMIN', 'HR', 'MANAGER')")
    @Operation(
        summary = "Get department monthly attendance report",
        description = """
            Get full monthly attendance report for all employees in a department.
            
            Returns:
            - Per-employee statistics (present/absent/late days, work hours)
            - Per-day calendar for each employee
            - Department-level aggregates (avg attendance rate, total hours)
            """
    )
    public ResponseEntity<ApiResponse<DepartmentMonthlyReportDTO>> getMonthlyReport(
            @PathVariable("deptId") Integer deptId,

            @Parameter(description = "Year (e.g. 2024)")
            @RequestParam("year") int year,

            @Parameter(description = "Month 1-12")
            @RequestParam("month") int month
    ) {
        Integer userId = SecurityUtils.getCurrentUserId();
        log.info("[ADMIN-ATTENDANCE] Monthly: deptId={}, year={}, month={}", deptId, year, month);

        try {
            DepartmentMonthlyReportDTO result = attendanceManagementService
                    .getDepartmentMonthlyReport(deptId, year, month, userId);
            return ResponseEntity.ok(ApiResponse.success(result, "Monthly report retrieved"));
        } catch (SecurityException e) {
            return ResponseEntity.status(403).body(ApiResponse.error(e.getMessage()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            log.error("[ADMIN-ATTENDANCE] Error getting monthly report", e);
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    // ============================================
    // APPROVE ATTENDANCE
    // ============================================

    @PutMapping("/{attendanceId}/approve")
    @PreAuthorize("hasAnyRole('ADMIN', 'HR', 'MANAGER')")
    @Operation(
        summary = "Approve attendance record",
        description = "Approve an employee's attendance record. Note field is optional."
    )
    public ResponseEntity<ApiResponse<Void>> approve(
            @PathVariable Integer attendanceId,
            @RequestBody(required = false) ApproveAttendanceRequest request
    ) {
        Integer userId = SecurityUtils.getCurrentUserId();
        log.info("[ADMIN-ATTENDANCE] Approve: attendanceId={}, userId={}", attendanceId, userId);

        // Request có thể null (không bắt buộc nhập note)
        ApproveAttendanceRequest req = request != null ? request : new ApproveAttendanceRequest();

        try {
            attendanceManagementService.approveAttendance(attendanceId, req, userId);
            return ResponseEntity.ok(ApiResponse.success(null, "Attendance approved"));
        } catch (SecurityException e) {
            return ResponseEntity.status(403).body(ApiResponse.error(e.getMessage()));
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            log.error("[ADMIN-ATTENDANCE] Error approving attendance", e);
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    // ============================================
    // REJECT ATTENDANCE
    // ============================================

    @PutMapping("/{attendanceId}/reject")
    @PreAuthorize("hasAnyRole('ADMIN', 'HR', 'MANAGER')")
    @Operation(
        summary = "Reject attendance record",
        description = "Reject an employee's attendance record. Reason is REQUIRED."
    )
    public ResponseEntity<ApiResponse<Void>> reject(
            @PathVariable Integer attendanceId,
            @Valid @RequestBody RejectAttendanceRequest request
    ) {
        Integer userId = SecurityUtils.getCurrentUserId();
        log.info("[ADMIN-ATTENDANCE] Reject: attendanceId={}, userId={}", attendanceId, userId);

        try {
            attendanceManagementService.rejectAttendance(attendanceId, request, userId);
            return ResponseEntity.ok(ApiResponse.success(null, "Attendance rejected"));
        } catch (SecurityException e) {
            return ResponseEntity.status(403).body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            log.error("[ADMIN-ATTENDANCE] Error rejecting attendance", e);
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    // ============================================
    // EDIT ATTENDANCE
    // ============================================

    @PutMapping("/{attendanceId}/edit")
    @PreAuthorize("hasAnyRole('ADMIN', 'HR', 'MANAGER')")
    @Operation(
        summary = "Edit attendance record",
        description = """
            Edit an employee's attendance record.
            
            Rules:
            - Reason is REQUIRED
            - Only non-null fields will be updated
            - Work hours will be recalculated if checkIn/Out time changes
            - Approval status will be RESET (needs re-approval)
            - All changes are logged in audit log
            """
    )
    public ResponseEntity<ApiResponse<Void>> edit(
            @PathVariable Integer attendanceId,
            @Valid @RequestBody EditAttendanceRequest request
    ) {
        Integer userId = SecurityUtils.getCurrentUserId();
        log.info("[ADMIN-ATTENDANCE] Edit: attendanceId={}, userId={}", attendanceId, userId);

        try {
            attendanceManagementService.editAttendance(attendanceId, request, userId);
            return ResponseEntity.ok(ApiResponse.success(null, "Attendance edited successfully"));
        } catch (SecurityException e) {
            return ResponseEntity.status(403).body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            log.error("[ADMIN-ATTENDANCE] Error editing attendance", e);
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    // ============================================
    // AUDIT LOGS
    // ============================================

    @GetMapping("/{attendanceId}/audit-logs")
    @PreAuthorize("hasAnyRole('ADMIN', 'HR', 'MANAGER')")
    @Operation(
        summary = "Get attendance audit logs",
        description = "Get change history for an attendance record. Sorted by newest first."
    )
    public ResponseEntity<ApiResponse<List<AuditLogDTO>>> getAuditLogs(
            @PathVariable Integer attendanceId
    ) {
        Integer userId = SecurityUtils.getCurrentUserId();
        log.info("[ADMIN-ATTENDANCE] AuditLogs: attendanceId={}", attendanceId);

        try {
            List<AuditLogDTO> logs = attendanceManagementService.getAuditLogs(attendanceId, userId);
            return ResponseEntity.ok(
                    ApiResponse.success(logs, "Found " + logs.size() + " log(s)")
            );
        } catch (SecurityException e) {
            return ResponseEntity.status(403).body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            log.error("[ADMIN-ATTENDANCE] Error getting audit logs", e);
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
}
