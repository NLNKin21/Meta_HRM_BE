package com.metahrms.employee_management.controller;

import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.metahrms.employee_management.dto.request.Contract.ContractCreateDto;
import com.metahrms.employee_management.dto.request.Contract.ContractFilterDto;
import com.metahrms.employee_management.dto.request.Contract.ContractUpdateDto;
import com.metahrms.employee_management.dto.response.ApiResponse;
import com.metahrms.employee_management.dto.response.PagedResponse;
import com.metahrms.employee_management.dto.response.Contract.ContractResponse;
import com.metahrms.employee_management.enums.ContractStatus;
import com.metahrms.employee_management.enums.ContractType;
import com.metahrms.employee_management.service.ContractService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@Tag(name = "Contract", description = "APIs for managing contracts")
@RestController
@RequestMapping("/contracts")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ContractController {
    ContractService contractService;

    @Operation(summary = "Get all contracts", description = "Retrieve a paginated list of contracts with optional filtering by status, contract type, employee ID, and dates")
    @GetMapping
    public ApiResponse<PagedResponse<ContractResponse>> getContracts(
            @Parameter(description = "Page number (zero-based)", example = "0") @RequestParam(name="page",required = false, defaultValue = "0") Integer page,
            @Parameter(description = "Number of items per page", example = "10") @RequestParam(name = "pageSize",required = false, defaultValue = "10") Integer pageSize,
            @Parameter(description = "Filter by contract status") @RequestParam(name="status",required = false) ContractStatus status,
            @Parameter(description = "Filter by contract type") @RequestParam(name="contractType",required = false) ContractType contractType,
            @Parameter(description = "Filter by employee ID") @RequestParam(name="empId",required = false) Integer empId,
            @Parameter(description = "Filter by start date (format: dd/MM/yyyy)", example = "01/01/2024") @RequestParam(name="startDate", required = false) @DateTimeFormat(pattern = "dd/MM/yyyy") LocalDate startDate,
            @Parameter(description = "Filter by end date (format: dd/MM/yyyy)", example = "31/12/2024") @RequestParam(name="endDate", required = false) @DateTimeFormat(pattern = "dd/MM/yyyy") LocalDate endDate) {

        ContractFilterDto filterDto = ContractFilterDto.builder()
            .page(page)
            .pageSize(pageSize)
            .status(status)
            .contractType(contractType)
            .empId(empId)
            .startDate(startDate)
            .endDate(endDate)
            .build();

        PagedResponse<ContractResponse> contracts = contractService.getContracts(filterDto);

        ApiResponse<PagedResponse<ContractResponse>> apiResponse = new ApiResponse<>();
        apiResponse.setStatus("success");
        apiResponse.setMessage("Get contracts successfully");
        apiResponse.setData(contracts);
        return apiResponse;
    }

    @Operation(summary = "Get contract by ID", description = "Retrieve a single contract by its ID")
    @GetMapping("/{id}")
    public ApiResponse<ContractResponse> getContractById(
            @Parameter(description = "Contract ID", required = true, example = "1") @PathVariable("id") Integer id) {
        ContractResponse contract = contractService.getContractById(id);

        ApiResponse<ContractResponse> apiResponse = new ApiResponse<>();
        apiResponse.setStatus("success");
        apiResponse.setMessage("Get contract successfully");
        apiResponse.setData(contract);
        return apiResponse;
    }

    @Operation(summary = "Create contract", description = "Create a new contract record")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<ContractResponse> createContract(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Contract creation data", required = true)
            @Valid @RequestBody ContractCreateDto createDto) {
        ContractResponse contract = contractService.createContract(createDto);

        ApiResponse<ContractResponse> apiResponse = new ApiResponse<>();
        apiResponse.setStatus("success");
        apiResponse.setMessage("Contract created successfully");
        apiResponse.setData(contract);
        return apiResponse;
    }

    @Operation(summary = "Update contract", description = "Update an existing contract record. All fields are optional for partial updates.")
    @PutMapping("/{id}")
    public ApiResponse<ContractResponse> updateContract(
            @Parameter(description = "Contract ID", required = true, example = "1") @PathVariable("id") Integer id,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Contract update data", required = true)
            @Valid @RequestBody ContractUpdateDto updateDto) {
        ContractResponse contract = contractService.updateContract(id, updateDto);

        ApiResponse<ContractResponse> apiResponse = new ApiResponse<>();
        apiResponse.setStatus("success");
        apiResponse.setMessage("Contract updated successfully");
        apiResponse.setData(contract);
        return apiResponse;
    }

    @Operation(summary = "Delete contract", description = "Soft delete a contract (sets isDeleted = true)")
    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteContract(
            @Parameter(description = "Contract ID", required = true, example = "1") @PathVariable("id") Integer id) {
        contractService.deleteContract(id);

        ApiResponse<Void> apiResponse = new ApiResponse<>();
        apiResponse.setStatus("success");
        apiResponse.setMessage("Contract deleted successfully");
        return apiResponse;
    }
}
