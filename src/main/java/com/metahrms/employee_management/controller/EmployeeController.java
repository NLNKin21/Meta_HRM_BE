package com.metahrms.employee_management.controller;

import java.io.IOException;
import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.metahrms.employee_management.dto.request.Contract.ContractCreateDto;
import com.metahrms.employee_management.dto.request.Employee.EmployeeCreateDto;
import com.metahrms.employee_management.dto.request.Employee.EmployeeFilterDto;
import com.metahrms.employee_management.dto.request.Employee.EmployeeUpdateDto;
import com.metahrms.employee_management.dto.response.ApiResponse;
import com.metahrms.employee_management.dto.response.Employee.EmployeeResponse;
import com.metahrms.employee_management.dto.response.PagedResponse;
import com.metahrms.employee_management.enums.EmployeeStatus;
import com.metahrms.employee_management.service.EmployeeService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

@Tag(name = "Employee", description = "APIs for managing employees")
@RestController
@RequestMapping("/employees")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class EmployeeController {

    EmployeeService employeeService;

    /**
     * Get all employees with filtering
     */
    @Operation(
        summary = "Get all employees",
        description = "Retrieve a paginated list of employees with optional filtering"
    )
    @GetMapping
    public ApiResponse<PagedResponse<EmployeeResponse>> getEmployees(
            @Parameter(description = "Page number (zero-based)", example = "0")
            @RequestParam(name = "page", required = false, defaultValue = "0") Integer page,

            @Parameter(description = "Number of items per page", example = "10")
            @RequestParam(name = "pageSize", required = false, defaultValue = "10") Integer pageSize,

            @Parameter(description = "Filter by employee status")
            @RequestParam(name = "status", required = false) EmployeeStatus status,

            @Parameter(description = "Filter by department ID")
            @RequestParam(name = "deptId", required = false) Integer deptId,

            @Parameter(description = "Filter by hire date (format: dd/MM/yyyy)", example = "01/01/2024")
            @RequestParam(name = "hireDate", required = false)
            @DateTimeFormat(pattern = "dd/MM/yyyy") LocalDate hireDate,

            @Parameter(description = "Search in employee name (case-insensitive)")
            @RequestParam(name = "search", required = false) String search) {

        log.info("GET /employees - page: {}, size: {}, status: {}, deptId: {}",
                page, pageSize, status, deptId);

        EmployeeFilterDto filterDto = EmployeeFilterDto.builder()
                .page(page)
                .pageSize(pageSize)
                .status(status)
                .deptId(deptId)
                .hireDate(hireDate)
                .search(search)
                .build();

        PagedResponse<EmployeeResponse> employees = employeeService.getEmployees(filterDto);

        return ApiResponse.<PagedResponse<EmployeeResponse>>builder()
                .code(200)
                .status("success")
                .message("Get employees successfully")
                .data(employees)
                .build();
    }

    /**
     * Get employee by ID
     * EmployeeResponse cần có thêm:
     * - managerId
     * - managerName
     * Service sẽ tự query trưởng phòng theo deptId và map vào response.
     */
    @Operation(
        summary = "Get employee by ID",
        description = "Retrieve a single employee by their ID"
    )
    @GetMapping("/{id}")
    public ApiResponse<EmployeeResponse> getEmployeeById(
            @Parameter(description = "Employee ID", required = true, example = "1")
            @PathVariable("id") Integer id) {

        log.info("GET /employees/{}", id);

        EmployeeResponse employee = employeeService.getEmployeeById(id);

        return ApiResponse.<EmployeeResponse>builder()
                .code(200)
                .status("success")
                .message("Get employee successfully")
                .data(employee)
                .build();
    }

    /**
     * Get current user's employee info
     * EmployeeResponse cần có thêm:
     * - managerId
     * - managerName
     */
    @Operation(
        summary = "Get current user's employee info",
        description = "Retrieve the employee information for the currently authenticated user"
    )
    @GetMapping("/me")
    public ApiResponse<EmployeeResponse> getCurrentUserEmployee() {
        log.info("GET /employees/me");

        EmployeeResponse employee = employeeService.getCurrentUserEmployee();

        return ApiResponse.<EmployeeResponse>builder()
                .code(200)
                .status("success")
                .message("Get current user employee info successfully")
                .data(employee)
                .build();
    }

    /**
     * Create employee (simple - without contract)
     */
    @Operation(
        summary = "Create employee",
        description = "Create a new employee record (without contract)"
    )
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<EmployeeResponse> createEmployee(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                description = "Employee creation data",
                required = true
            )
            @Valid @RequestBody EmployeeCreateDto createDto) {

        log.info("POST /employees - Creating employee: {}", createDto.getFullName());

        EmployeeResponse employee = employeeService.createEmployee(createDto);

        return ApiResponse.<EmployeeResponse>builder()
                .code(201)
                .status("success")
                .message("Employee created successfully")
                .data(employee)
                .build();
    }

    /**
     * Create employee with contract (multipart/form-data)
     */
    @Operation(
        summary = "Create employee with contract",
        description = "Create a new employee with optional contract and file upload (Cloudinary)"
    )
    @PostMapping(value = "/with-contract", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<EmployeeResponse> createEmployeeWithContract(
            @Parameter(description = "Employee data (JSON)", required = true)
            @RequestPart("employeeData") @Valid EmployeeCreateDto employeeData,

            @Parameter(description = "Contract data (JSON)", required = false)
            @RequestPart(value = "contractData", required = false) ContractCreateDto contractData,

            @Parameter(description = "Contract file (PDF, DOCX, PNG, JPG)", required = false)
            @RequestPart(value = "contractFile", required = false) MultipartFile contractFile) {

        log.info("POST /employees/with-contract - Creating employee: {} with contract: {}",
                employeeData.getFullName(), contractData != null);

        try {
            EmployeeResponse employee = employeeService.createEmployeeWithContract(
                    employeeData,
                    contractData,
                    contractFile
            );

            String message = "Tạo nhân viên thành công";
            if (contractData != null) {
                message += " kèm hợp đồng";
            }

            return ApiResponse.<EmployeeResponse>builder()
                    .code(201)
                    .status("success")
                    .message(message)
                    .data(employee)
                    .build();

        } catch (IOException e) {
            log.error("Failed to create employee with contract: {}", e.getMessage());
            return ApiResponse.<EmployeeResponse>builder()
                    .code(400)
                    .status("error")
                    .message("Lỗi upload file hợp đồng: " + e.getMessage())
                    .build();
        }
    }

    /**
     * Update employee
     */
    @Operation(
        summary = "Update employee",
        description = "Update an existing employee record. All fields are optional for partial updates."
    )
    @PutMapping("/{id}")
    public ApiResponse<EmployeeResponse> updateEmployee(
            @Parameter(description = "Employee ID", required = true, example = "1")
            @PathVariable("id") Integer id,

            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                description = "Employee update data",
                required = true
            )
            @Valid @RequestBody EmployeeUpdateDto updateDto) {

        log.info("PUT /employees/{}", id);

        EmployeeResponse employee = employeeService.updateEmployee(id, updateDto);

        return ApiResponse.<EmployeeResponse>builder()
                .code(200)
                .status("success")
                .message("Employee updated successfully")
                .data(employee)
                .build();
    }

    /**
     * Delete employee (soft delete)
     */
    @Operation(
        summary = "Delete employee",
        description = "Soft delete an employee (sets isDeleted = true)"
    )
    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteEmployee(
            @Parameter(description = "Employee ID", required = true, example = "1")
            @PathVariable("id") Integer id) {

        log.info("DELETE /employees/{}", id);

        employeeService.deleteEmployee(id);

        return ApiResponse.<Void>builder()
                .code(200)
                .status("success")
                .message("Employee deleted successfully")
                .build();
    }
}