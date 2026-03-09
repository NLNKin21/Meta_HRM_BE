package com.metahrms.employee_management.controller;

import java.util.Map;

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

import jakarta.servlet.http.HttpServletRequest;

import com.metahrms.employee_management.dto.request.EmployeeDocument.EmployeeDocumentCreateDto;
import com.metahrms.employee_management.dto.request.EmployeeDocument.EmployeeDocumentFilterDto;
import com.metahrms.employee_management.dto.request.EmployeeDocument.EmployeeDocumentUpdateDto;
import com.metahrms.employee_management.dto.response.ApiResponse;
import com.metahrms.employee_management.dto.response.EmployeeDocument.EmployeeDocumentResponse;
import com.metahrms.employee_management.dto.response.PagedResponse;
import com.metahrms.employee_management.enums.DocumentType;
import com.metahrms.employee_management.service.EmployeeDocumentService;
import com.metahrms.employee_management.enums.UserRole;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@Tag(name = "Employee Document", description = "APIs for managing employee documents")
@RestController
@RequestMapping("/documents")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class EmployeeDocumentController {
    EmployeeDocumentService employeeDocumentService;

    @Operation(summary = "Get all documents", description = "Retrieve a paginated list of employee documents with optional filtering by employee ID and document type")
    @GetMapping
    public ApiResponse<PagedResponse<EmployeeDocumentResponse>> getDocuments(
            @Parameter(description = "Page number (zero-based)", example = "0") @RequestParam(name="page",required = false, defaultValue = "0") Integer page,
            @Parameter(description = "Number of items per page", example = "10") @RequestParam(name="pageSize",required = false, defaultValue = "10") Integer pageSize,
            @Parameter(description = "Filter by employee ID") @RequestParam(name="empId",required = false) Integer empId,
            @Parameter(description = "Filter by document type") @RequestParam(name="docType",required = false) DocumentType docType,
            HttpServletRequest request) {

        @SuppressWarnings("unchecked")
        Map<String, Object> user = (Map<String, Object>) request.getAttribute("user");
        Integer id = Integer.parseInt(String.valueOf(user.get("id")));
        UserRole role = UserRole.valueOf(String.valueOf(user.get("role")));

        EmployeeDocumentFilterDto filterDto;

        if (role != UserRole.ADMIN) {
            filterDto = EmployeeDocumentFilterDto.builder()
                .page(page)
                .pageSize(pageSize)
                .empId(id)
                .docType(docType)
                .build();
        } else {
            filterDto = EmployeeDocumentFilterDto.builder()
                .page(page)
                .pageSize(pageSize)
                .empId(empId)
                .docType(docType)
                .build();
        }

        PagedResponse<EmployeeDocumentResponse> documents = employeeDocumentService.getDocuments(filterDto);

        ApiResponse<PagedResponse<EmployeeDocumentResponse>> apiResponse = new ApiResponse<>();
        apiResponse.setStatus("success");
        apiResponse.setMessage("Get documents successfully");
        apiResponse.setData(documents);
        return apiResponse;
    }

    @Operation(summary = "Get document by ID", description = "Retrieve a single employee document by its ID")
    @GetMapping("/{id}")
    public ApiResponse<EmployeeDocumentResponse> getDocumentById(
            @Parameter(description = "Document ID", required = true, example = "1") @PathVariable("id") Integer id) {
        EmployeeDocumentResponse document = employeeDocumentService.getDocumentById(id);

        ApiResponse<EmployeeDocumentResponse> apiResponse = new ApiResponse<>();
        apiResponse.setStatus("success");
        apiResponse.setMessage("Get document successfully");
        apiResponse.setData(document);
        return apiResponse;
    }

    @Operation(summary = "Create employee document", description = "Create a new employee document record")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<EmployeeDocumentResponse> createDocument(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Document creation data", required = true)
            @Valid @RequestBody EmployeeDocumentCreateDto createDto) {
        EmployeeDocumentResponse document = employeeDocumentService.createDocument(createDto);

        ApiResponse<EmployeeDocumentResponse> apiResponse = new ApiResponse<>();
        apiResponse.setStatus("success");
        apiResponse.setMessage("Document created successfully");
        apiResponse.setData(document);
        return apiResponse;
    }

    @Operation(summary = "Update employee document", description = "Update an existing employee document record. All fields are optional for partial updates.")
    @PutMapping("/{id}")
    public ApiResponse<EmployeeDocumentResponse> updateDocument(
            @Parameter(description = "Document ID", required = true, example = "1") @PathVariable("id") Integer id,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Document update data", required = true)
            @Valid @RequestBody EmployeeDocumentUpdateDto updateDto) {
        EmployeeDocumentResponse document = employeeDocumentService.updateDocument(id, updateDto);

        ApiResponse<EmployeeDocumentResponse> apiResponse = new ApiResponse<>();
        apiResponse.setStatus("success");
        apiResponse.setMessage("Document updated successfully");
        apiResponse.setData(document);
        return apiResponse;
    }

    @Operation(summary = "Delete employee document", description = "Soft delete an employee document (sets isDeleted = true)")
    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteDocument(
            @Parameter(description = "Document ID", required = true, example = "1") @PathVariable("id") Integer id) {
        employeeDocumentService.deleteDocument(id);

        ApiResponse<Void> apiResponse = new ApiResponse<>();
        apiResponse.setStatus("success");
        apiResponse.setMessage("Document deleted successfully");
        return apiResponse;
    }

    @Operation(summary = "Download employee document", description = "Get a presigned download URL for an employee document (valid for 12 hours)")
    @GetMapping("/{id}/download")
    public ApiResponse<String> downloadDocument(
            @Parameter(description = "Document ID", required = true, example = "1") @PathVariable("id") Integer id) {
        String downloadUrl = employeeDocumentService.getDownloadUrl(id);

        return ApiResponse.<String>builder()
            .status("success")
            .message("Download URL generated successfully")
            .data(downloadUrl)
            .build();
    }
}
