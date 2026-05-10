package com.metahrms.employee_management.controller;

import java.io.IOException;
import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.metahrms.employee_management.dto.request.Contract.ContractCreateDto;
import com.metahrms.employee_management.dto.request.Contract.ContractFilterDto;
import com.metahrms.employee_management.dto.request.Contract.ContractUpdateDto;
import com.metahrms.employee_management.dto.response.ApiResponse;
import com.metahrms.employee_management.dto.response.PagedResponse;
import com.metahrms.employee_management.dto.response.Contract.ContractResponse;
import com.metahrms.employee_management.enums.ContractStatus;
import com.metahrms.employee_management.service.ContractService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

@Tag(name = "Contract", description = "APIs for managing contracts")
@RestController
@RequestMapping("/contracts")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class ContractController {
    ContractService contractService;

    @Operation(
        summary = "Get all contracts",
        description = "Retrieve a paginated list of contracts with optional filtering"
    )
    @GetMapping
    public ApiResponse<PagedResponse<ContractResponse>> getContracts(
            @Parameter(description = "Page number (zero-based)", example = "0")
            @RequestParam(name = "page", required = false, defaultValue = "0") Integer page,
            
            @Parameter(description = "Number of items per page", example = "10")
            @RequestParam(name = "pageSize", required = false, defaultValue = "10") Integer pageSize,
            
            @Parameter(description = "Filter by contract status")
            @RequestParam(name = "status", required = false) ContractStatus status,
            
            @Parameter(description = "Filter by contract type")
            @RequestParam(name = "contractType", required = false) Integer contractTypeId,
            
            @Parameter(description = "Filter by employee ID")
            @RequestParam(name = "empId", required = false) Integer empId,
            
            @Parameter(description = "Filter by start date (format: dd/MM/yyyy)", example = "01/01/2024")
            @RequestParam(name = "startDate", required = false)
            @DateTimeFormat(pattern = "dd/MM/yyyy") LocalDate startDate,
            
            @Parameter(description = "Filter by end date (format: dd/MM/yyyy)", example = "31/12/2024")
            @RequestParam(name = "endDate", required = false)
            @DateTimeFormat(pattern = "dd/MM/yyyy") LocalDate endDate) {

        log.info("GET /contracts - page: {}, size: {}", page, pageSize);

        ContractFilterDto filterDto = ContractFilterDto.builder()
            .page(page)
            .pageSize(pageSize)
            .status(status)
            .contractTypeId(contractTypeId)
            .empId(empId)
            .startDate(startDate)
            .endDate(endDate)
            .build();


        PagedResponse<ContractResponse> contracts = contractService.getContracts(filterDto);

        return ApiResponse.<PagedResponse<ContractResponse>>builder()
            .code(200)
            .status("success")
            .message("Get contracts successfully")
            .data(contracts)
            .build();
    }

    @Operation(
        summary = "Get contract by ID",
        description = "Retrieve a single contract by its ID"
    )
    @GetMapping("/{id}")
    public ApiResponse<ContractResponse> getContractById(
            @Parameter(description = "Contract ID", required = true, example = "1")
            @PathVariable("id") Integer id) {

        log.info("GET /contracts/{}", id);

        ContractResponse contract = contractService.getContractById(id);

        return ApiResponse.<ContractResponse>builder()
            .code(200)
            .status("success")
            .message("Get contract successfully")
            .data(contract)
            .build();
    }

    @Operation(
        summary = "Create contract",
        description = "Create a new contract with file upload"
    )
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<ContractResponse> createContract(
            @Parameter(description = "Contract data (JSON)", required = true)
            @RequestPart("data") @Valid ContractCreateDto createDto,
            
            @Parameter(description = "Contract file (PDF, DOCX, XLSX, PNG, JPG)", required = false)
            @RequestPart(value = "file", required = false) MultipartFile file) {

        log.info("POST /contracts - Employee ID: {}", createDto.getEmpId());

        try {
            ContractResponse contract = contractService.createContract(createDto, file);

            return ApiResponse.<ContractResponse>builder()
                .code(201)
                .status("success")
                .message("Contract created successfully")
                .data(contract)
                .build();

        } catch (IOException e) {
            log.error("Failed to create contract: {}", e.getMessage());
            return ApiResponse.<ContractResponse>builder()
                .code(400)
                .status("error")
                .message("Failed to upload file: " + e.getMessage())
                .build();
        }
    }

    @Operation(
        summary = "Update contract",
        description = "Update an existing contract (with optional file replacement)"
    )
    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<ContractResponse> updateContract(
            @Parameter(description = "Contract ID", required = true, example = "1")
            @PathVariable("id") Integer id,
            
            @Parameter(description = "Contract update data (JSON)", required = true)
            @RequestPart("data") @Valid ContractUpdateDto updateDto,
            
            @Parameter(description = "New contract file (optional)")
            @RequestPart(value = "file", required = false) MultipartFile file) {

        log.info("PUT /contracts/{}", id);

        try {
            ContractResponse contract = contractService.updateContract(id, updateDto, file);

            return ApiResponse.<ContractResponse>builder()
                .code(200)
                .status("success")
                .message("Contract updated successfully")
                .data(contract)
                .build();

        } catch (IOException e) {
            log.error("Failed to update contract: {}", e.getMessage());
            return ApiResponse.<ContractResponse>builder()
                .code(400)
                .status("error")
                .message("Failed to upload file: " + e.getMessage())
                .build();
        }
    }

    @Operation(
        summary = "Delete contract",
        description = "Soft delete a contract (sets isDeleted = true) and removes file from Cloudinary"
    )
    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteContract(
            @Parameter(description = "Contract ID", required = true, example = "1")
            @PathVariable("id") Integer id) {

        log.info("DELETE /contracts/{}", id);

        try {
            contractService.deleteContract(id);

            return ApiResponse.<Void>builder()
                .code(200)
                .status("success")
                .message("Contract deleted successfully")
                .build();

        } catch (IOException e) {
            log.error("Failed to delete contract: {}", e.getMessage());
            return ApiResponse.<Void>builder()
                .code(400)
                .status("error")
                .message("Failed to delete file: " + e.getMessage())
                .build();
        }
    }
}