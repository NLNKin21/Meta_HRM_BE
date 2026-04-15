package com.metahrms.employee_management.controller.admin.payroll;

import com.metahrms.employee_management.dto.request.common.ApiResponse;
import com.metahrms.employee_management.dto.request.payroll.CreateDeductionRequest;
import com.metahrms.employee_management.dto.response.payroll.DeductionDTO;
import com.metahrms.employee_management.service.payroll.DeductionService;

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
@RequestMapping("/admin/payroll/deductions")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('ADMIN', 'HR')")
@Tag(name = "Admin - Deductions", description = "Quản lý khấu trừ lương nhân viên")
public class AdminDeductionController {

    private final DeductionService deductionService;

    @PostMapping
    @Operation(summary = "Tạo khoản khấu trừ")
    public ResponseEntity<ApiResponse<DeductionDTO>> create(
            @Valid @RequestBody CreateDeductionRequest request) {
        try {
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.success(deductionService.create(request), "Deduction created"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @PutMapping("/{id}/approve")
    @Operation(summary = "Duyệt khoản khấu trừ")
    public ResponseEntity<ApiResponse<Void>> approve(@PathVariable Integer id) {
        try {
            deductionService.approve(id);
            return ResponseEntity.ok(ApiResponse.success(null, "Deduction approved"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Xoá khoản khấu trừ (chỉ xoá được nếu chưa duyệt)")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Integer id) {
        try {
            deductionService.delete(id);
            return ResponseEntity.ok(ApiResponse.success(null, "Deduction deleted"));
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping
    @Operation(summary = "Lấy danh sách khấu trừ theo tháng/năm")
    public ResponseEntity<ApiResponse<List<DeductionDTO>>> getByPeriod(
            @RequestParam Integer month,
            @RequestParam Integer year) {
        return ResponseEntity.ok(ApiResponse.success(
            deductionService.getByPeriod(month, year),
            "Deductions retrieved"));
    }

    @GetMapping("/employee/{employeeId}")
    @Operation(summary = "Lấy lịch sử khấu trừ của 1 nhân viên")
    public ResponseEntity<ApiResponse<List<DeductionDTO>>> getByEmployee(
            @PathVariable Integer employeeId) {
        return ResponseEntity.ok(ApiResponse.success(
            deductionService.getByEmployee(employeeId),
            "Deductions retrieved"));
    }
}