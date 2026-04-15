package com.metahrms.employee_management.controller.admin.attendance;

import com.metahrms.employee_management.dto.request.common.ApiResponse;
import com.metahrms.employee_management.dto.request.shift.AssignShiftRequest;
import com.metahrms.employee_management.dto.request.shift.CreateShiftRequest;
import com.metahrms.employee_management.dto.request.shift.UpdateShiftRequest;
import com.metahrms.employee_management.dto.response.shift.ShiftEmployeeDTO;
import com.metahrms.employee_management.dto.response.shift.ShiftResponseDTO;
import com.metahrms.employee_management.service.ShiftService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/admin/shifts")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('ADMIN', 'HR')")
@Tag(name = "Admin - Shift Management", description = "Admin APIs for managing work shifts")
public class AdminShiftController {

    private final ShiftService shiftService;

    // ============================================
    // CRUD SHIFTS
    // ============================================

    @PostMapping
    @Operation(summary = "Create new shift",
               description = "Create a new work shift with schedule configuration")
    public ResponseEntity<ApiResponse<ShiftResponseDTO>> create(
            @Valid @RequestBody CreateShiftRequest request
    ) {
        log.info("[ADMIN-SHIFT] Creating shift: name={}", request.getName());

        try {
            ShiftResponseDTO result = shiftService.create(request);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.success(result, "Shift created successfully"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update shift",
               description = "Update an existing shift. Only non-null fields will be updated.")
    public ResponseEntity<ApiResponse<ShiftResponseDTO>> update(
            @PathVariable("id") Integer id,
            @Valid @RequestBody UpdateShiftRequest request
    ) {
        log.info("[ADMIN-SHIFT] Updating shift id={}", id);

        try {
            ShiftResponseDTO result = shiftService.update(id, request);
            return ResponseEntity.ok(ApiResponse.success(result, "Shift updated successfully"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete shift (soft delete)",
               description = "Cannot delete if employees are still assigned to this shift")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable("id") Integer id) {
        log.info("[ADMIN-SHIFT] Deleting shift id={}", id);

        try {
            shiftService.softDelete(id);
            return ResponseEntity.ok(ApiResponse.success(null, "Shift deleted successfully"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get shift by ID")
    public ResponseEntity<ApiResponse<ShiftResponseDTO>> getById(@PathVariable Integer id) {
        try {
            ShiftResponseDTO result = shiftService.getById(id);
            return ResponseEntity.ok(ApiResponse.success(result, "Shift found"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping
    @Operation(summary = "Get all shifts",
               description = "List all shifts with optional filters and pagination")
    public ResponseEntity<ApiResponse<Page<ShiftResponseDTO>>> getAll(
            @Parameter(description = "Filter by active status")
            @RequestParam(name="isActive", required = false) Boolean isActive,

            @Parameter(description = "Search by name or code")
            @RequestParam(name="keyword", required = false) String keyword,

            @RequestParam(name="page", defaultValue = "0") int page,
            @RequestParam(name="size", defaultValue = "20") int size,
            @RequestParam(name="sortBy", defaultValue = "createdAt") String sortBy,
            @RequestParam(name="sortDir", defaultValue = "desc") String sortDir
    ) {
        Sort sort = sortDir.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<ShiftResponseDTO> result = shiftService.getAll(isActive, keyword, pageable);

        return ResponseEntity.ok(
                ApiResponse.success(result, "Retrieved " + result.getTotalElements() + " shift(s)")
        );
    }

    // ============================================
    // ASSIGN / UNASSIGN SHIFT
    // ============================================

    @PutMapping("/assign/employee/{employeeId}")
    @Operation(summary = "Assign shift to one employee",
               description = "Set the work shift for a specific employee")
    public ResponseEntity<ApiResponse<Void>> assignToEmployee(
            @PathVariable Integer employeeId,
            @RequestParam Integer shiftId
    ) {
        log.info("[ADMIN-SHIFT] Assigning shift {} to employee {}", shiftId, employeeId);

        try {
            shiftService.assignShiftToEmployee(employeeId, shiftId);
            return ResponseEntity.ok(ApiResponse.success(null, "Shift assigned successfully"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    @PutMapping("/assign/bulk")
    @Operation(summary = "Assign shift to multiple employees",
               description = "Set the same shift for a list of employees")
    public ResponseEntity<ApiResponse<Void>> assignBulk(
            @Valid @RequestBody AssignShiftRequest request
    ) {
        log.info("[ADMIN-SHIFT] Bulk assigning shift {} to {} employees",
                request.getShiftId(), request.getEmployeeIds().size());

        try {
            shiftService.assignShiftToEmployees(request);
            return ResponseEntity.ok(
                    ApiResponse.success(null,
                            "Shift assigned to " + request.getEmployeeIds().size() + " employee(s)")
            );
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    @DeleteMapping("/assign/employee/{employeeId}")
    @Operation(summary = "Unassign shift from employee",
               description = "Remove shift assignment from employee (set to null)")
    public ResponseEntity<ApiResponse<Void>> unassign(@PathVariable Integer employeeId) {
        log.info("[ADMIN-SHIFT] Unassigning shift from employee {}", employeeId);

        try {
            shiftService.unassignShift(employeeId);
            return ResponseEntity.ok(ApiResponse.success(null, "Shift unassigned successfully"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    // ============================================
    // GET EMPLOYEES BY SHIFT
    // ============================================

    @GetMapping("/{shiftId}/employees")
    @Operation(summary = "Get employees assigned to a shift",
               description = "List all employees currently assigned to a specific shift")
    public ResponseEntity<ApiResponse<List<ShiftEmployeeDTO>>> getEmployees(
            @PathVariable Integer shiftId
    ) {
        try {
            List<ShiftEmployeeDTO> employees = shiftService.getEmployeesByShift(shiftId);
            return ResponseEntity.ok(
                    ApiResponse.success(employees,
                            "Found " + employees.size() + " employee(s)")
            );
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage()));
        }
    }
}