package com.metahrms.employee_management.controller;

import com.metahrms.employee_management.dto.request.common.ApiResponse;
import com.metahrms.employee_management.dto.response.payroll.*;
import com.metahrms.employee_management.service.payroll.PayrollService;
import com.metahrms.employee_management.util.SecurityUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/payroll/me")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('EMPLOYEE', 'ADMIN', 'HR')")
@Tag(name = "Employee - Payslip", description = "Nhân viên xem phiếu lương cá nhân")
public class PayrollController {

    private final PayrollService payrollService;

    @GetMapping("/payslips")
    @Operation(summary = "Lấy danh sách phiếu lương của tôi (đã duyệt/đã trả)")
    public ResponseEntity<ApiResponse<List<PayslipSummaryDTO>>> getMyPayslips() {
        Integer userId = SecurityUtils.getCurrentUserId();
        log.info("[PAYROLL-ME] Get payslips for userId={}", userId);
        try {
            return ResponseEntity.ok(ApiResponse.success(
                payrollService.getMyPayslips(userId),
                "Payslips retrieved"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/payslips/latest")
    @Operation(summary = "Phiếu lương mới nhất")
    public ResponseEntity<ApiResponse<PayslipFullDTO>> getLatest() {
        Integer userId = SecurityUtils.getCurrentUserId();
        try {
            return ResponseEntity.ok(ApiResponse.success(
                payrollService.getMyLatestPayslip(userId),
                "Latest payslip retrieved"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/payslips/{id}")
    @Operation(summary = "Chi tiết 1 phiếu lương của tôi")
    public ResponseEntity<ApiResponse<PayslipFullDTO>> getDetail(@PathVariable Integer id) {
        Integer userId = SecurityUtils.getCurrentUserId();
        try {
            // TODO: validate payslip belongs to current user
            return ResponseEntity.ok(ApiResponse.success(
                payrollService.getPayslipDetail(id),
                "Payslip found"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
}