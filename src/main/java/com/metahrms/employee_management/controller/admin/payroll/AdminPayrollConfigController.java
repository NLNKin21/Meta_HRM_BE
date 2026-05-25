package com.metahrms.employee_management.controller.admin.payroll;

import com.metahrms.employee_management.dto.request.common.ApiResponse;
import com.metahrms.employee_management.dto.request.payroll.UpdateConfigRequest;
import com.metahrms.employee_management.dto.response.payroll.PayrollConfigDTO;
import com.metahrms.employee_management.service.payroll.PayrollConfigService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/admin/payroll/config")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('ADMIN', 'HR')")
@Tag(name = "Admin - Payroll Config", description = "Cấu hình tham số tính lương")
public class AdminPayrollConfigController {

    private final PayrollConfigService configService;

    @GetMapping
    @Operation(summary = "Lấy tất cả cấu hình lương")
    public ResponseEntity<ApiResponse<List<PayrollConfigDTO>>> getAll() {
        return ResponseEntity.ok(ApiResponse.success(
            configService.getAll(), "Config retrieved"));
    }

    @GetMapping("/group/{group}")
    @Operation(summary = "Lấy cấu hình theo nhóm (INSURANCE, TAX, OT, PENALTY, GENERAL)")
    public ResponseEntity<ApiResponse<List<PayrollConfigDTO>>> getByGroup(
            @PathVariable("group") String group) {
        return ResponseEntity.ok(ApiResponse.success(
            configService.getByGroup(group), "Config retrieved"));
    }

    @GetMapping("/{key}")
    @Operation(summary = "Lấy 1 cấu hình theo key")
    public ResponseEntity<ApiResponse<PayrollConfigDTO>> getByKey(
            @PathVariable("key") String key) {
        try {
            return ResponseEntity.ok(ApiResponse.success(
                configService.getByKey(key), "Config found"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @PutMapping("/{key}")
    @Operation(summary = "Cập nhật cấu hình theo key")
    public ResponseEntity<ApiResponse<PayrollConfigDTO>> update(
            @PathVariable("key") String key,
            @Valid @RequestBody UpdateConfigRequest request) {
        log.info("[PAYROLL-CONFIG] Update key={}, value={}", key, request.getConfigValue());
        try {
            return ResponseEntity.ok(ApiResponse.success(
                configService.update(key, request), "Config updated"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
}