package com.metahrms.employee_management.controller.admin.payroll;

import com.metahrms.employee_management.dto.request.common.ApiResponse;
import com.metahrms.employee_management.dto.request.payroll.CreateBonusRequest;
import com.metahrms.employee_management.dto.response.payroll.BonusDTO;
import com.metahrms.employee_management.service.payroll.BonusService;

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
@RequestMapping("/admin/payroll/bonuses")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('ADMIN', 'HR')")
@Tag(name = "Admin - Bonuses", description = "Quản lý thưởng nhân viên")
public class AdminBonusController {

    private final BonusService bonusService;

    @PostMapping
    @Operation(summary = "Tạo khoản thưởng cho nhân viên")
    public ResponseEntity<ApiResponse<BonusDTO>> create(
            @Valid @RequestBody CreateBonusRequest request) {
        try {
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.success(bonusService.create(request), "Bonus created"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @PutMapping("/{id}/approve")
    @Operation(summary = "Duyệt khoản thưởng")
    public ResponseEntity<ApiResponse<Void>> approve(@PathVariable Integer id) {
        try {
            bonusService.approve(id);
            return ResponseEntity.ok(ApiResponse.success(null, "Bonus approved"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Xoá khoản thưởng (chỉ được xoá nếu chưa duyệt)")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Integer id) {
        try {
            bonusService.delete(id);
            return ResponseEntity.ok(ApiResponse.success(null, "Bonus deleted"));
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping
    @Operation(summary = "Lấy danh sách thưởng theo tháng/năm")
    public ResponseEntity<ApiResponse<List<BonusDTO>>> getByPeriod(
            @RequestParam Integer month,
            @RequestParam Integer year) {
        return ResponseEntity.ok(ApiResponse.success(
            bonusService.getByPeriod(month, year),
            "Bonuses retrieved"));
    }

    @GetMapping("/employee/{employeeId}")
    @Operation(summary = "Lấy lịch sử thưởng của 1 nhân viên")
    public ResponseEntity<ApiResponse<List<BonusDTO>>> getByEmployee(
            @PathVariable Integer employeeId) {
        return ResponseEntity.ok(ApiResponse.success(
            bonusService.getByEmployee(employeeId),
            "Bonuses retrieved"));
    }
}