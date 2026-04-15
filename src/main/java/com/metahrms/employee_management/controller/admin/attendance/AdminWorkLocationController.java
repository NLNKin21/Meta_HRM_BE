package com.metahrms.employee_management.controller.admin.attendance;

import com.metahrms.employee_management.dto.request.common.ApiResponse;
import com.metahrms.employee_management.dto.request.location.CreateWorkLocationRequest;
import com.metahrms.employee_management.dto.request.location.UpdateWorkLocationRequest;
import com.metahrms.employee_management.dto.response.location.WorkLocationResponseDTO;
import com.metahrms.employee_management.service.WorkLocationService;

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

@Slf4j
@RestController
@RequestMapping("/admin/locations")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('ADMIN', 'HR')")
@Tag(name = "Admin - Work Location", description = "Admin APIs for managing work locations")
public class AdminWorkLocationController {

    private final WorkLocationService workLocationService;

    // ============================================
    // CREATE
    // ============================================
    @PostMapping
    @Operation(summary = "Create new work location",
               description = "Create a new GPS-based work location for attendance check-in/out")
    public ResponseEntity<ApiResponse<WorkLocationResponseDTO>> create(
            @Valid @RequestBody CreateWorkLocationRequest request
    ) {
        log.info("[ADMIN-LOCATION] Creating location: name={}", request.getName());

        try {
            WorkLocationResponseDTO result = workLocationService.create(request);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.success(result, "Location created successfully"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    // ============================================
    // UPDATE
    // ============================================
    @PutMapping("/{id}")
    @Operation(summary = "Update work location",
               description = "Update an existing work location. Only non-null fields will be updated.")
    public ResponseEntity<ApiResponse<WorkLocationResponseDTO>> update(
            @PathVariable("id") Integer id,
            @Valid @RequestBody UpdateWorkLocationRequest request
    ) {
        log.info("[ADMIN-LOCATION] Updating location id={}", id);

        try {
            WorkLocationResponseDTO result = workLocationService.update(id, request);
            return ResponseEntity.ok(ApiResponse.success(result, "Location updated successfully"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    // ============================================
    // DELETE (soft)
    // ============================================
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete work location (soft delete)",
               description = "Soft delete a location. It will be deactivated and marked as deleted.")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable("id") Integer id) {
        log.info("[ADMIN-LOCATION] Deleting location id={}", id);

        try {
            workLocationService.softDelete(id);
            return ResponseEntity.ok(ApiResponse.success(null, "Location deleted successfully"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    // ============================================
    // GET BY ID
    // ============================================
    @GetMapping("/{id}")
    @Operation(summary = "Get work location by ID")
    public ResponseEntity<ApiResponse<WorkLocationResponseDTO>> getById(@PathVariable Integer id) {
        try {
            WorkLocationResponseDTO result = workLocationService.getById(id);
            return ResponseEntity.ok(ApiResponse.success(result, "Location found"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    // ============================================
    // GET ALL (paging + filter)
    // ============================================
    @GetMapping
    @Operation(summary = "Get all work locations",
               description = "List all locations with optional filters and pagination")
    public ResponseEntity<ApiResponse<Page<WorkLocationResponseDTO>>> getAll(
            @Parameter(description = "Filter by active status")
            @RequestParam(name="isActive", required = false) Boolean isActive,

            @Parameter(description = "Search by name, code, or address")
            @RequestParam(name="keyword", required = false) String keyword,

            @Parameter(description = "Page number (0-based)")
            @RequestParam(name="page", defaultValue = "0") int page,

            @Parameter(description = "Page size")
            @RequestParam(name="size", defaultValue = "20") int size,

            @Parameter(description = "Sort field")
            @RequestParam(name="sortBy", defaultValue = "createdAt") String sortBy,

            @Parameter(description = "Sort direction: asc/desc")
            @RequestParam(name="sortDir", defaultValue = "desc") String sortDir
    ) {
        Sort sort = sortDir.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<WorkLocationResponseDTO> result = workLocationService.getAll(isActive, keyword, pageable);

        return ResponseEntity.ok(
                ApiResponse.success(result, "Retrieved " + result.getTotalElements() + " location(s)")
        );
    }

    // ============================================
    // ACTIVATE / DEACTIVATE
    // ============================================
    @PatchMapping("/{id}/activate")
    @Operation(summary = "Activate a work location")
    public ResponseEntity<ApiResponse<Void>> activate(@PathVariable Integer id) {
        try {
            workLocationService.activate(id);
            return ResponseEntity.ok(ApiResponse.success(null, "Location activated"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    @PatchMapping("/{id}/deactivate")
    @Operation(summary = "Deactivate a work location")
    public ResponseEntity<ApiResponse<Void>> deactivate(@PathVariable Integer id) {
        try {
            workLocationService.deactivate(id);
            return ResponseEntity.ok(ApiResponse.success(null, "Location deactivated"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage()));
        }
    }
}