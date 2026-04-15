package com.metahrms.employee_management.controller.admin.payroll;

import com.metahrms.employee_management.dto.request.common.ApiResponse;
import com.metahrms.employee_management.dto.request.payroll.CreateAllowanceRequest;
import com.metahrms.employee_management.dto.request.payroll.UpdateAllowanceRequest;
import com.metahrms.employee_management.dto.response.payroll.AllowanceDTO;
import com.metahrms.employee_management.service.payroll.AllowanceService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/admin/payroll/allowances")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('ADMIN', 'HR')")
@Tag(name = "Admin - Allowances", description = "Quản lý phụ cấp nhân viên")
public class AdminAllowanceController {

    private final AllowanceService allowanceService;

    @PostMapping
    @Operation(summary = "Thêm phụ cấp cho nhân viên")
    public ResponseEntity<ApiResponse<AllowanceDTO>> create(
            @Valid @RequestBody CreateAllowanceRequest request) {
        try {
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.success(allowanceService.create(request), "Allowance created"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    @Operation(summary = "Cập nhật phụ cấp")
    public ResponseEntity<ApiResponse<AllowanceDTO>> update(
            @PathVariable Integer id,
            @Valid @RequestBody UpdateAllowanceRequest request) {
        try {
            return ResponseEntity.ok(ApiResponse.success(
                allowanceService.update(id, request), "Allowance updated"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Xoá phụ cấp")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Integer id) {
        try {
            allowanceService.delete(id);
            return ResponseEntity.ok(ApiResponse.success(null, "Allowance deleted"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/employee/{employeeId}")
    @Operation(summary = "Lấy phụ cấp của 1 nhân viên")
    public ResponseEntity<ApiResponse<List<AllowanceDTO>>> getByEmployee(
            @PathVariable Integer employeeId) {
        return ResponseEntity.ok(ApiResponse.success(
            allowanceService.getByEmployee(employeeId),
            "Allowances retrieved"));
    }

    @GetMapping
    @Operation(summary = "Lấy tất cả phụ cấp theo tháng/năm")
    public ResponseEntity<ApiResponse<List<AllowanceDTO>>> getAll(
            @RequestParam(defaultValue = "#{T(java.time.LocalDate).now().monthValue}") Integer month,
            @RequestParam(defaultValue = "#{T(java.time.LocalDate).now().year}") Integer year) {
        return ResponseEntity.ok(ApiResponse.success(
            allowanceService.getAll(month, year),
            "Allowances retrieved"));
    }
}