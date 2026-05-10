package com.metahrms.employee_management.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.metahrms.employee_management.dto.request.Contract.ContractTypeCreateDto;
import com.metahrms.employee_management.dto.request.Contract.ContractTypeFilterDto;
import com.metahrms.employee_management.dto.request.Contract.ContractTypeUpdateDto;
import com.metahrms.employee_management.dto.response.ApiResponse;
import com.metahrms.employee_management.dto.response.PagedResponse;
import com.metahrms.employee_management.dto.response.Contract.ContractTypeResponse;
import com.metahrms.employee_management.service.ContractTypeService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

@Tag(name = "Contract Type", description = "APIs for managing contract type configurations")
@RestController
@RequestMapping("/contract-types")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class ContractTypeController {

    ContractTypeService contractTypeService;

    // ─────────────────────────────────────────────────────────────
    // GET LIST (phân trang + filter)
    // ─────────────────────────────────────────────────────────────
    @Operation(summary = "Get contract types (paginated)",
               description = "Get all contract types with optional keyword search and active filter")
    @GetMapping
    public ApiResponse<PagedResponse<ContractTypeResponse>> getContractTypes(
            @RequestParam(name="page", defaultValue = "0")  Integer page,
            @RequestParam(name="pageSize", defaultValue = "10") Integer pageSize,
            @RequestParam(name="keyword", required = false)    String keyword,
            @RequestParam(name="isActive", required = false)    Boolean isActive) {

        log.info("GET /contract-types - page:{} size:{} keyword:{} isActive:{}",
                page, pageSize, keyword, isActive);

        ContractTypeFilterDto filterDto = new ContractTypeFilterDto(page, pageSize, keyword, isActive);

        return ApiResponse.<PagedResponse<ContractTypeResponse>>builder()
                .code(200)
                .status("success")
                .message("Get contract types successfully")
                .data(contractTypeService.getContractTypes(filterDto))
                .build();
    }

    // ─────────────────────────────────────────────────────────────
    // GET ALL ACTIVE (dropdown)
    // ─────────────────────────────────────────────────────────────
    @Operation(summary = "Get all active contract types",
               description = "Get flat list of active contract types for dropdown selection")
    @GetMapping("/active")
    public ApiResponse<List<ContractTypeResponse>> getAllActiveTypes() {
        log.info("GET /contract-types/active");

        return ApiResponse.<List<ContractTypeResponse>>builder()
                .code(200)
                .status("success")
                .message("Get active contract types successfully")
                .data(contractTypeService.getAllActiveTypes())
                .build();
    }

    // ─────────────────────────────────────────────────────────────
    // GET BY ID
    // ─────────────────────────────────────────────────────────────
    @Operation(summary = "Get contract type by ID")
    @GetMapping("/{id}")
    public ApiResponse<ContractTypeResponse> getContractTypeById(
            @Parameter(description = "Contract type ID", example = "1")
            @PathVariable Integer id) {

        log.info("GET /contract-types/{}", id);

        return ApiResponse.<ContractTypeResponse>builder()
                .code(200)
                .status("success")
                .message("Get contract type successfully")
                .data(contractTypeService.getContractTypeById(id))
                .build();
    }

    // ─────────────────────────────────────────────────────────────
    // CREATE
    // ─────────────────────────────────────────────────────────────
    @Operation(summary = "Create new contract type")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<ContractTypeResponse> createContractType(
            @Valid @RequestBody ContractTypeCreateDto createDto) {

        log.info("POST /contract-types - code: {}", createDto.getTypeCode());

        return ApiResponse.<ContractTypeResponse>builder()
                .code(201)
                .status("success")
                .message("Contract type created successfully")
                .data(contractTypeService.createContractType(createDto))
                .build();
    }

    // ─────────────────────────────────────────────────────────────
    // UPDATE
    // ─────────────────────────────────────────────────────────────
    @Operation(summary = "Update contract type")
    @PutMapping("/{id}")
    public ApiResponse<ContractTypeResponse> updateContractType(
            @Parameter(description = "Contract type ID", example = "1")
            @PathVariable Integer id,
            @Valid @RequestBody ContractTypeUpdateDto updateDto) {

        log.info("PUT /contract-types/{}", id);

        return ApiResponse.<ContractTypeResponse>builder()
                .code(200)
                .status("success")
                .message("Contract type updated successfully")
                .data(contractTypeService.updateContractType(id, updateDto))
                .build();
    }

    // ─────────────────────────────────────────────────────────────
    // TOGGLE ACTIVE
    // ─────────────────────────────────────────────────────────────
    @Operation(summary = "Toggle active/inactive status")
    @PatchMapping("/{id}/toggle")
    public ApiResponse<ContractTypeResponse> toggleActive(
            @Parameter(description = "Contract type ID", example = "1")
            @PathVariable Integer id) {

        log.info("PATCH /contract-types/{}/toggle", id);

        return ApiResponse.<ContractTypeResponse>builder()
                .code(200)
                .status("success")
                .message("Contract type status toggled successfully")
                .data(contractTypeService.toggleActive(id))
                .build();
    }

    // ─────────────────────────────────────────────────────────────
    // DELETE (soft delete)
    // ─────────────────────────────────────────────────────────────
    @Operation(summary = "Delete contract type (soft delete)",
               description = "Cannot delete if already used in contracts. Deactivate instead.")
    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteContractType(
            @Parameter(description = "Contract type ID", example = "1")
            @PathVariable Integer id) {

        log.info("DELETE /contract-types/{}", id);

        contractTypeService.deleteContractType(id);

        return ApiResponse.<Void>builder()
                .code(200)
                .status("success")
                .message("Contract type deleted successfully")
                .build();
    }
}
