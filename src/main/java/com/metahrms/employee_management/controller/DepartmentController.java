package com.metahrms.employee_management.controller;

import com.metahrms.employee_management.dto.response.Department.DepartmentResponse;
import com.metahrms.employee_management.dto.response.Department.DepartmentSummaryDto;
import com.metahrms.employee_management.dto.response.Department.TeamMemberResponse;
import com.metahrms.employee_management.dto.response.Department.TeamWorkloadResponse;
import com.metahrms.employee_management.service.DepartmentService;
import com.metahrms.employee_management.dto.request.Department.DepartmentDto;
import com.metahrms.employee_management.dto.response.ApiResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.AccessLevel;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Department", description = "APIs for managing departments")
@RestController
@RequestMapping("/departments")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class DepartmentController {

    DepartmentService departmentService;

    @Operation(
        summary = "Get all departments",
        description = "Retrieve a list of all departments"
    )
    @GetMapping()
    public ApiResponse<List<DepartmentSummaryDto>> getDepartmentSummaries() {
        List<DepartmentSummaryDto> summaries = departmentService.getDepartmentSummaries();
        return ApiResponse.<List<DepartmentSummaryDto>>builder()
                .status("success")
                .message("Get department summaries successfully")
                .data(summaries)
                .build();
    }

    @Operation(
        summary = "Get department by ID",
        description = "Retrieve a single department by its ID"
    )
    @GetMapping("/{id}")
    public ApiResponse<DepartmentResponse> getDepartmentById(
            @Parameter(description = "Department ID", required = true, example = "1") @PathVariable("id") Integer id) {
        DepartmentResponse department = departmentService.getDepartmentById(id);

        return ApiResponse.<DepartmentResponse>builder()
                .status("success")
                .message("Get department successfully")
                .data(department)
                .build();
    }

    @Operation(
        summary = "Get employees by department", 
        description = "Retrieve a list of employees belonging to a specific department"
    )
    @GetMapping("/{id}/employees")
    public ResponseEntity<ApiResponse<List<?>>> getEmployeesByDepartment(@PathVariable("id") Integer id) {
        return ResponseEntity.ok(ApiResponse.<List<?>>builder()
                .status("success")
               .message("Get employees by department successfully")
               .data(departmentService.getEmployeesByDepartmentId(id))
                .build());
    }


    @Operation(
        summary = "Create department",
        description = "Create a new department"
    )
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<DepartmentResponse> createDepartment(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                description = "Department creation data",
                required = true,
                content = @Content(schema = @Schema(implementation = DepartmentDto.class))
            )
            @Valid @RequestBody DepartmentDto createDto) {
        DepartmentResponse department = departmentService.createDepartment(createDto);

        return ApiResponse.<DepartmentResponse>builder()
                .status("success")
                .message("Department created successfully")
                .data(department)
                .build();
    }

    @Operation(
        summary = "Update department",
        description = "Update an existing department"
    )
    @PutMapping("/{id}")
    public ApiResponse<DepartmentResponse> updateDepartment(
            @Parameter(description = "Department ID", required = true, example = "1") @PathVariable("id") Integer id,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                description = "Department update data",
                required = true,
                content = @Content(schema = @Schema(implementation = DepartmentDto.class))
            )
            @Valid @RequestBody DepartmentDto updateDto) {

        DepartmentResponse department = departmentService.updateDepartment(id, updateDto);

        return ApiResponse.<DepartmentResponse>builder()
                .status("success")
                .message("Department updated successfully")
                .data(department)
                .build();
    }

    /**
     * Get all members in a department
     */
    @GetMapping("/{departmentId}/members")
    @Operation(summary = "Get department members", description = "Returns all employees in a specific department")
    public ResponseEntity<ApiResponse<List<TeamMemberResponse>>> getDepartmentMembers(
            @Parameter(description = "Department ID", required = true)
            @PathVariable("departmentId") Integer departmentId) {
        
        List<TeamMemberResponse> members = departmentService.getDepartmentMembers(departmentId);
        return ResponseEntity.ok(
            ApiResponse.success(members, "Retrieved department members successfully")
        );
    }

    /**
     * Get team workload (tasks count per employee)
     */
    @GetMapping("/{departmentId}/workload")
    @Operation(summary = "Get team workload", description = "Returns workload statistics for each team member")
    public ResponseEntity<ApiResponse<List<TeamWorkloadResponse>>> getTeamWorkload(
            @Parameter(description = "Department ID", required = true)
            @PathVariable("departmentId") Integer departmentId) {
        
        List<TeamWorkloadResponse> workload = departmentService.getTeamWorkload(departmentId);
        return ResponseEntity.ok(
            ApiResponse.success(workload, "Retrieved team workload successfully")
        );
    }


    @Operation(
        summary = "Delete department",
        description = "Delete a department"
    )
    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteDepartment(
            @Parameter(description = "Department ID", required = true, example = "1") @PathVariable("id") Integer id) {
        departmentService.deleteDepartment(id);

        return ApiResponse.<Void>builder()
                .status("success")
                .message("Department deleted successfully")
                .build();
    }
}
