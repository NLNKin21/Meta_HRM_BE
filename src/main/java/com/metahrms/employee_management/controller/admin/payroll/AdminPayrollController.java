package com.metahrms.employee_management.controller.admin.payroll;

import com.metahrms.employee_management.dto.request.common.ApiResponse;
import com.metahrms.employee_management.dto.request.payroll.*;
import com.metahrms.employee_management.dto.response.payroll.*;
import com.metahrms.employee_management.service.payroll.PayrollService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/admin/payroll")
@RequiredArgsConstructor
@Tag(name = "Admin - Payroll", description = "Quản lý bảng lương")
public class AdminPayrollController {

    private final PayrollService payrollService;

    // ============================================
    // WORKFLOW
    // ============================================

    @PostMapping("/generate")
    @PreAuthorize("hasAnyRole('ADMIN', 'HR')")
    @Operation(summary = "BƯỚC 1: Tạo phiếu lương DRAFT cho tháng")
    public ResponseEntity<ApiResponse<List<PayslipSummaryDTO>>> generate(
            @Valid @RequestBody GeneratePayrollRequest request) {
        log.info("[PAYROLL] Generate: {}/{}", request.getMonth(), request.getYear());
        try {
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.success(
                        payrollService.generatePayroll(request),
                        "Payroll generated"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @PostMapping("/calculate")
    @PreAuthorize("hasAnyRole('ADMIN', 'HR')")
    @Operation(summary = "BƯỚC 2: Tính lương cho toàn bộ tháng")
    public ResponseEntity<ApiResponse<List<PayslipSummaryDTO>>> calculate(
            @RequestParam("month") Integer month,
            @RequestParam("year") Integer year) {
        log.info("[PAYROLL] Calculate: {}/{}", month, year);
        try {
            return ResponseEntity.ok(ApiResponse.success(
                payrollService.calculatePayroll(month, year),
                "Payroll calculated"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @PostMapping("/calculate/employee/{employeeId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'HR')")
    @Operation(summary = "BƯỚC 2b: Tính lại lương cho 1 nhân viên")
    public ResponseEntity<ApiResponse<PayslipSummaryDTO>> calculateOne(
            @PathVariable("employeeId") Integer employeeId,
            @RequestParam("month") Integer month,
            @RequestParam("year") Integer year) {
        try {
            return ResponseEntity.ok(ApiResponse.success(
                payrollService.calculateOneEmployee(employeeId, month, year),
                "Recalculated"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @PutMapping("/payslips/{id}/approve")
    @PreAuthorize("hasAnyRole('ADMIN', 'HR')")
    @Operation(summary = "BƯỚC 3: Duyệt 1 phiếu lương")
    public ResponseEntity<ApiResponse<Void>> approve(
            @PathVariable("id") Integer id,
            @RequestBody(required = false) ApprovePayslipRequest request) {
        try {
            payrollService.approvePayslip(id,
                request != null ? request : new ApprovePayslipRequest());
            return ResponseEntity.ok(ApiResponse.success(null, "Payslip approved"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @PutMapping("/approve-all")
    @PreAuthorize("hasAnyRole('ADMIN', 'HR')")
    @Operation(summary = "BƯỚC 3b: Duyệt toàn bộ phiếu lương trong tháng")
    public ResponseEntity<ApiResponse<Void>> approveAll(
            @RequestParam("month") Integer month,
            @RequestParam("year") Integer year) {
        try {
            payrollService.approveAll(month, year);
            return ResponseEntity.ok(ApiResponse.success(null, "All payslips approved"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @PutMapping("/payslips/{id}/reject")
    @PreAuthorize("hasAnyRole('ADMIN', 'HR')")
    @Operation(summary = "Từ chối phiếu lương")
    public ResponseEntity<ApiResponse<Void>> reject(
            @PathVariable("id") Integer id,
            @Valid @RequestBody RejectPayslipRequest request) {
        try {
            payrollService.rejectPayslip(id, request);
            return ResponseEntity.ok(ApiResponse.success(null, "Payslip rejected"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @PutMapping("/payslips/{id}/pay")
    @PreAuthorize("hasAnyRole('ADMIN')")
    @Operation(summary = "BƯỚC 4: Đánh dấu đã thanh toán 1 phiếu")
    public ResponseEntity<ApiResponse<Void>> markAsPaid(@PathVariable("id") Integer id) {
        try {
            payrollService.markAsPaid(id);
            return ResponseEntity.ok(ApiResponse.success(null, "Marked as PAID"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @PutMapping("/pay-all")
    @PreAuthorize("hasAnyRole('ADMIN')")
    @Operation(summary = "BƯỚC 4b: Đánh dấu đã thanh toán toàn bộ")
    public ResponseEntity<ApiResponse<Void>> markAllAsPaid(
            @RequestParam("month") Integer month,
            @RequestParam("year") Integer year) {
        try {
            payrollService.markAllAsPaid(month, year);
            return ResponseEntity.ok(ApiResponse.success(null, "All marked as PAID"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @PutMapping("/payslips/{id}/edit")
    @PreAuthorize("hasAnyRole('ADMIN', 'HR')")
    @Operation(summary = "Chỉnh sửa manual phiếu lương (reset về CALCULATED)")
    public ResponseEntity<ApiResponse<PayslipSummaryDTO>> edit(
            @PathVariable("id") Integer id,
            @Valid @RequestBody EditPayslipRequest request) {
        try {
            return ResponseEntity.ok(ApiResponse.success(
                payrollService.editPayslip(id, request), "Payslip edited"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    // ============================================
    // QUERY
    // ============================================

    @GetMapping("/payslips")
    @PreAuthorize("hasAnyRole('ADMIN', 'HR')")
    @Operation(summary = "Danh sách phiếu lương trong tháng")
    public ResponseEntity<ApiResponse<List<PayslipSummaryDTO>>> getPayslips(
            @RequestParam("month") Integer month,
            @RequestParam("year") Integer year,
            @RequestParam(name="status", required = false) String status) {
        return ResponseEntity.ok(ApiResponse.success(
            payrollService.getPayslipsByPeriod(month, year, status),
            "Payslips retrieved"));
    }

    @GetMapping("/payslips/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'HR')")
    @Operation(summary = "Chi tiết 1 phiếu lương")
    public ResponseEntity<ApiResponse<PayslipFullDTO>> getDetail(@PathVariable("id") Integer id) {
        try {
            return ResponseEntity.ok(ApiResponse.success(
                payrollService.getPayslipDetail(id), "Payslip found"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/summary")
    @PreAuthorize("hasAnyRole('ADMIN', 'HR')")
    @Operation(summary = "Tổng kết bảng lương tháng")
    public ResponseEntity<ApiResponse<PayrollPeriodSummaryDTO>> getSummary(
            @RequestParam("month") Integer month,
            @RequestParam("year") Integer year) {
        return ResponseEntity.ok(ApiResponse.success(
            payrollService.getPeriodSummary(month, year),
            "Summary retrieved"));
    }

    // ============================================
    // EXPORT
    // ============================================

    @GetMapping("/export/excel")
    @PreAuthorize("hasAnyRole('ADMIN', 'HR')")
    public void exportExcel(
            @RequestParam("month") Integer month,
            @RequestParam("year") Integer year,
            HttpServletResponse response) {
        try {
            payrollService.exportPayrollExcel(month, year, response);
        } catch (Exception e) {
            log.error("[PAYROLL] Export Excel failed: {}", e.getMessage(), e);
            // ✅ Tự xử lý — không để Spring/GlobalExceptionHandler bắt
            if (!response.isCommitted()) {
                try {
                    response.reset();
                    response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                    response.setContentType("application/json;charset=UTF-8");
                    response.getWriter().write(
                        "{\"success\":false,\"code\":500,\"message\":\"" +
                        e.getMessage().replace("\"", "'") + "\"}"
                    );
                    response.flushBuffer();
                } catch (IOException ioEx) {
                    log.error("Cannot write error response", ioEx);
                }
            }
        }
    }

    @GetMapping("/export/techcombank")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public void exportTechcombank(
            @RequestParam("month") Integer month,
            @RequestParam("year") Integer year,
            HttpServletResponse response) {
        try {
            payrollService.exportTechcombank(month, year, response);
        } catch (Exception e) {
            log.error("[PAYROLL] Export TCB failed: {}", e.getMessage(), e);
            if (!response.isCommitted()) {
                try {
                    response.reset();
                    response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                    response.setContentType("application/json;charset=UTF-8");
                    response.getWriter().write(
                        "{\"success\":false,\"code\":500,\"message\":\"" +
                        e.getMessage().replace("\"", "'") + "\"}"
                    );
                    response.flushBuffer();
                } catch (IOException ioEx) {
                    log.error("Cannot write error response", ioEx);
                }
            }
        }
    }
}