package com.metahrms.employee_management.controller;

import com.metahrms.employee_management.dto.request.EmployeeRequest;
import com.metahrms.employee_management.dto.response.ApiResponse;
import com.metahrms.employee_management.dto.response.EmployeeResponse;
import com.metahrms.employee_management.dto.response.PageResponse;
import com.metahrms.employee_management.service.EmployeeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/employees")
@RequiredArgsConstructor
@Tag(name = "Employee Management", description = "APIs for managing employees")
public class EmployeeController {

    private final EmployeeService employeeService;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'HR')")
    @Operation(summary = "Create a new employee", description = "Create a new employee with the provided details")
    public ResponseEntity<ApiResponse<EmployeeResponse>> createEmployee(
            @Valid @RequestBody EmployeeRequest request) {
        EmployeeResponse employee = employeeService.createEmployee(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Employee created successfully", employee));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get employee by ID")
    public ResponseEntity<ApiResponse<EmployeeResponse>> getEmployeeById(
            @PathVariable Long id) {
        EmployeeResponse employee = employeeService.getEmployeeById(id);
        return ResponseEntity.ok(ApiResponse.success(employee));
    }

    @GetMapping("/code/{employeeCode}")
    @Operation(summary = "Get employee by code")
    public ResponseEntity<ApiResponse<EmployeeResponse>> getEmployeeByCode(
            @PathVariable String employeeCode) {
        EmployeeResponse employee = employeeService.getEmployeeByCode(employeeCode);
        return ResponseEntity.ok(ApiResponse.success(employee));
    }

    @GetMapping
    @Operation(summary = "Get all employees with pagination")
    public ResponseEntity<ApiResponse<PageResponse<EmployeeResponse>>> getAllEmployees(
            @Parameter(description = "Page number (0-based)")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size")
            @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Sort by field")
            @RequestParam(defaultValue = "id") String sortBy,
            @Parameter(description = "Sort direction (asc/desc)")
            @RequestParam(defaultValue = "asc") String sortDir) {

        Sort sort = sortDir.equalsIgnoreCase("desc")
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);

        PageResponse<EmployeeResponse> employees = employeeService.getAllEmployees(pageable);
        return ResponseEntity.ok(ApiResponse.success(employees));
    }

    @GetMapping("/search")
    @Operation(summary = "Search employees by keyword")
    public ResponseEntity<ApiResponse<PageResponse<EmployeeResponse>>> searchEmployees(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size);
        PageResponse<EmployeeResponse> employees = employeeService.searchEmployees(keyword, pageable);
        return ResponseEntity.ok(ApiResponse.success(employees));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'HR')")
    @Operation(summary = "Update an employee")
    public ResponseEntity<ApiResponse<EmployeeResponse>> updateEmployee(
            @PathVariable Long id,
            @Valid @RequestBody EmployeeRequest request) {
        EmployeeResponse employee = employeeService.updateEmployee(id, request);
        return ResponseEntity.ok(ApiResponse.success("Employee updated successfully", employee));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Delete an employee (soft delete)")
    public ResponseEntity<ApiResponse<Void>> deleteEmployee(@PathVariable Long id) {
        employeeService.deleteEmployee(id);
        return ResponseEntity.ok(ApiResponse.success("Employee deleted successfully", null));
    }

    @GetMapping("/department/{departmentId}")
    @Operation(summary = "Get employees by department")
    public ResponseEntity<ApiResponse<List<EmployeeResponse>>> getEmployeesByDepartment(
            @PathVariable Long departmentId) {
        List<EmployeeResponse> employees = employeeService.getEmployeesByDepartment(departmentId);
        return ResponseEntity.ok(ApiResponse.success(employees));
    }

    @GetMapping("/{managerId}/subordinates")
    @Operation(summary = "Get subordinates of a manager")
    public ResponseEntity<ApiResponse<List<EmployeeResponse>>> getSubordinates(
            @PathVariable Long managerId) {
        List<EmployeeResponse> subordinates = employeeService.getSubordinates(managerId);
        return ResponseEntity.ok(ApiResponse.success(subordinates));
    }
}