package com.metahrms.employee_management.controller.admin.attendance;

import com.metahrms.employee_management.dto.request.common.ApiResponse;
import com.metahrms.employee_management.dto.response.report.CompanyDailyReportDTO;
import com.metahrms.employee_management.dto.response.report.CompanyMonthlyReportDTO;
import com.metahrms.employee_management.dto.response.report.DashboardDTO;
import com.metahrms.employee_management.dto.response.report.DepartmentComparisonDTO;
import com.metahrms.employee_management.service.AttendanceReportService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.time.LocalDate;

@Slf4j
@RestController
@RequestMapping("/admin/reports")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('ADMIN', 'HR')")
@Tag(name = "Admin - Reports & Dashboard",
     description = "APIs for attendance reports and real-time dashboard")
public class AttendanceReportController {

    private final AttendanceReportService reportService;

    // ============================================
    // DASHBOARD
    // ============================================

    @GetMapping("/dashboard")
    @Operation(
        summary = "Get real-time dashboard",
        description = """
            Real-time attendance dashboard for today.
            
            Returns:
            - Today's attendance counts (present/absent/late...)
            - Employees currently working (checked in, not out)
            - Unresolved anomalies count
            - Pending approvals count
            - Weekly trend (last 7 days)
            - Department ranking by attendance rate
            - Recent check-in/out activities (last 10)
            """
    )
    public ResponseEntity<ApiResponse<DashboardDTO>> getDashboard() {
        log.info("[REPORT] GET /dashboard");
        try {
            DashboardDTO result = reportService.getDashboard();
            return ResponseEntity.ok(ApiResponse.success(result, "Dashboard data retrieved"));
        } catch (Exception e) {
            log.error("[REPORT] Error getting dashboard", e);
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    // ============================================
    // DAILY REPORT
    // ============================================

    @GetMapping("/daily")
    @Operation(
        summary = "Get company daily attendance report",
        description = """
            Daily attendance report for the entire company.
            
            Returns:
            - Company-wide totals
            - Breakdown by department
            
            Default date = today
            """
    )
    public ResponseEntity<ApiResponse<CompanyDailyReportDTO>> getDailyReport(
            @Parameter(description = "Date (yyyy-MM-dd), default = today")
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date
    ) {
        LocalDate targetDate = date != null ? date : LocalDate.now();
        log.info("[REPORT] GET /daily date={}", targetDate);

        try {
            CompanyDailyReportDTO result = reportService.getDailyReport(targetDate);
            return ResponseEntity.ok(ApiResponse.success(result, "Daily report retrieved"));
        } catch (Exception e) {
            log.error("[REPORT] Error getting daily report", e);
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    // ============================================
    // MONTHLY REPORT
    // ============================================

    @GetMapping("/monthly")
    @Operation(
        summary = "Get company monthly attendance report",
        description = """
            Monthly attendance report for the entire company.
            
            Returns:
            - Company-wide statistics
            - Department breakdown with ranking
            - Top 5 best attendance employees
            - Top 5 most late employees
            """
    )
    public ResponseEntity<ApiResponse<CompanyMonthlyReportDTO>> getMonthlyReport(
            @Parameter(description = "Year (e.g. 2024)", required = true)
            @RequestParam int year,

            @Parameter(description = "Month 1-12", required = true)
            @RequestParam int month
    ) {
        log.info("[REPORT] GET /monthly year={}, month={}", year, month);

        try {
            CompanyMonthlyReportDTO result = reportService.getMonthlyReport(year, month);
            return ResponseEntity.ok(ApiResponse.success(result, "Monthly report retrieved"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            log.error("[REPORT] Error getting monthly report", e);
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    // ============================================
    // DEPARTMENT COMPARISON
    // ============================================

    @GetMapping("/department-comparison")
    @Operation(
        summary = "Compare departments by attendance",
        description = """
            Compare attendance metrics across all departments.
            
            Returns departments ranked by attendance rate (best first).
            
            Metrics per department:
            - Attendance rate, punctuality rate
            - Present/absent/late days
            - Total work hours, average work hours per day
            
            Max date range: 365 days
            """
    )
    public ResponseEntity<ApiResponse<DepartmentComparisonDTO>> getDepartmentComparison(
            @Parameter(description = "Start date (yyyy-MM-dd)", required = true)
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,

            @Parameter(description = "End date (yyyy-MM-dd)", required = true)
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate
    ) {
        log.info("[REPORT] GET /department-comparison {} to {}", startDate, endDate);

        // Validate
        if (startDate.isAfter(endDate)) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("startDate must be before or equal to endDate"));
        }
        if (java.time.temporal.ChronoUnit.DAYS.between(startDate, endDate) > 365) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Date range must not exceed 365 days"));
        }

        try {
            DepartmentComparisonDTO result = reportService.getDepartmentComparison(startDate, endDate);
            return ResponseEntity.ok(ApiResponse.success(result, "Comparison retrieved"));
        } catch (Exception e) {
            log.error("[REPORT] Error getting department comparison", e);
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    // ============================================
    // EXPORT EXCEL - DEPARTMENT
    // ============================================

    @GetMapping("/export/department/{deptId}/monthly")
    @Operation(
        summary = "Export department monthly attendance to Excel",
        description = """
            Export monthly attendance sheet for a department as Excel file.
            
            Excel format:
            - Rows: employees
            - Columns: days of month + summary
            - Color coded: Green=Present, Red=Absent, Yellow=Late, Gray=Weekend
            - Legend: P=Present, L=Late, E=EarlyLeave, A=Absent, AL=AnnualLeave
            """
    )
    public void exportDepartmentMonthlyExcel(
            @PathVariable("deptId") Integer deptId,

            @Parameter(description = "Year (e.g. 2024)", required = true)
            @RequestParam("year") int year,

            @Parameter(description = "Month 1-12", required = true)
            @RequestParam("month") int month,

            HttpServletResponse response
    ) throws IOException {

        log.info("[REPORT] Export Excel: deptId={}, year={}, month={}", deptId, year, month);

        try {
            reportService.exportDepartmentMonthlyExcel(deptId, year, month, response);
        } catch (Exception e) {
            log.error("[REPORT] Error exporting Excel", e);
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.setContentType("application/json");
            response.getWriter().write("{\"error\":\"" + e.getMessage() + "\"}");
        }
    }

    // ============================================
    // EXPORT EXCEL - COMPANY
    // ============================================

    @GetMapping("/export/company/monthly")
    @Operation(
        summary = "Export company monthly attendance summary to Excel",
        description = "Export company-wide monthly attendance summary with department breakdown"
    )
    public void exportCompanyMonthlyExcel(
            @Parameter(description = "Year (e.g. 2024)", required = true)
            @RequestParam int year,

            @Parameter(description = "Month 1-12", required = true)
            @RequestParam int month,

            HttpServletResponse response
    ) throws IOException {

        log.info("[REPORT] Export Company Excel: year={}, month={}", year, month);

        try {
            reportService.exportCompanyMonthlyExcel(year, month, response);
        } catch (Exception e) {
            log.error("[REPORT] Error exporting company Excel", e);
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.setContentType("application/json");
            response.getWriter().write("{\"error\":\"" + e.getMessage() + "\"}");
        }
    }
}