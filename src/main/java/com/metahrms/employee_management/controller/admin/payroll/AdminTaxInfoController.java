package com.metahrms.employee_management.controller.admin.payroll;

import com.metahrms.employee_management.dto.request.common.ApiResponse;
import com.metahrms.employee_management.dto.request.payroll.UpdateTaxInfoRequest;
import com.metahrms.employee_management.dto.response.payroll.EmployeeTaxInfoDTO;
import com.metahrms.employee_management.service.payroll.EmployeeTaxInfoService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/admin/payroll/tax-info")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('ADMIN', 'HR')")
@Tag(name = "Admin - Tax Info", description = "Thông tin thuế và ngân hàng nhân viên")
public class AdminTaxInfoController {

    private final EmployeeTaxInfoService taxInfoService;

    @GetMapping("/employee/{employeeId}")
    @Operation(summary = "Lấy thông tin thuế của nhân viên")
    public ResponseEntity<ApiResponse<EmployeeTaxInfoDTO>> getByEmployee(
            @PathVariable Integer employeeId) {
        try {
            return ResponseEntity.ok(ApiResponse.success(
                taxInfoService.getByEmployee(employeeId), "Tax info retrieved"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @PutMapping("/employee/{employeeId}")
    @Operation(summary = "Cập nhật thông tin thuế và ngân hàng của nhân viên",
               description = "Tạo mới nếu chưa có, cập nhật nếu đã có (upsert)")
    public ResponseEntity<ApiResponse<EmployeeTaxInfoDTO>> upsert(
            @PathVariable Integer employeeId,
            @Valid @RequestBody UpdateTaxInfoRequest request) {
        try {
            return ResponseEntity.ok(ApiResponse.success(
                taxInfoService.upsert(employeeId, request), "Tax info updated"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
}