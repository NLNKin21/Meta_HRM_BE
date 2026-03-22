package com.metahrms.employee_management.controller;

import java.io.IOException;
import java.util.Map;

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

import com.metahrms.employee_management.dto.request.EmployeeDocument.EmployeeDocumentCreateDto;
import com.metahrms.employee_management.dto.request.EmployeeDocument.EmployeeDocumentFilterDto;
import com.metahrms.employee_management.dto.request.EmployeeDocument.EmployeeDocumentUpdateDto;
import com.metahrms.employee_management.dto.response.ApiResponse;
import com.metahrms.employee_management.dto.response.EmployeeDocument.EmployeeDocumentResponse;
import com.metahrms.employee_management.dto.response.PagedResponse;
import com.metahrms.employee_management.enums.DocumentType;
import com.metahrms.employee_management.enums.UserRole;
import com.metahrms.employee_management.service.EmployeeDocumentService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

@Tag(name = "Employee Document", description = "APIs for managing employee documents")
@RestController
@RequestMapping("/documents")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class EmployeeDocumentController {
    
    EmployeeDocumentService employeeDocumentService;

    /**
     * Get all documents with filtering and role-based access
     */
    @Operation(
        summary = "Get all documents",
        description = "Retrieve a paginated list of employee documents. ADMIN can see all, others only see their own."
    )
    @GetMapping
    public ApiResponse<PagedResponse<EmployeeDocumentResponse>> getDocuments(
            @Parameter(description = "Page number (zero-based)", example = "0")
            @RequestParam(name = "page", required = false, defaultValue = "0") Integer page,
            
            @Parameter(description = "Number of items per page", example = "10")
            @RequestParam(name = "pageSize", required = false, defaultValue = "10") Integer pageSize,
            
            @Parameter(description = "Filter by employee ID (ADMIN only)")
            @RequestParam(name = "empId", required = false) Integer empId,
            
            @Parameter(description = "Filter by document type")
            @RequestParam(name = "docType", required = false) DocumentType docType,
            
            HttpServletRequest request) {

        log.info("GET /documents - page: {}, pageSize: {}, empId: {}, docType: {}", 
                 page, pageSize, empId, docType);

        // ✅ Extract user info from request
        @SuppressWarnings("unchecked")
        Map<String, Object> user = (Map<String, Object>) request.getAttribute("user");
        Integer userId = Integer.parseInt(String.valueOf(user.get("id")));
        UserRole userRole = UserRole.valueOf(String.valueOf(user.get("role")));

        log.debug("User ID: {}, Role: {}", userId, userRole);

        // ✅ Build filter DTO based on role
        EmployeeDocumentFilterDto filterDto;

        if (userRole == UserRole.ADMIN) {
            // ADMIN: có thể filter theo empId từ query param
            filterDto = EmployeeDocumentFilterDto.builder()
                .page(page)
                .pageSize(pageSize)
                .empId(empId)  // Dùng empId từ param
                .docType(docType)
                .build();
        } else {
            // Non-ADMIN: chỉ xem tài liệu của chính mình
            filterDto = EmployeeDocumentFilterDto.builder()
                .page(page)
                .pageSize(pageSize)
                .empId(userId)  // Force empId = userId
                .docType(docType)
                .build();
        }

        PagedResponse<EmployeeDocumentResponse> documents = employeeDocumentService.getDocuments(filterDto);

        return ApiResponse.success(documents, "Get documents successfully");
    }

    /**
     * Get document by ID
     */
    @Operation(
        summary = "Get document by ID",
        description = "Retrieve a single employee document by its ID"
    )
    @GetMapping("/{id}")
    public ApiResponse<EmployeeDocumentResponse> getDocumentById(
            @Parameter(description = "Document ID", required = true, example = "1")
            @PathVariable("id") Integer id) {

        log.info("GET /documents/{}", id);

        EmployeeDocumentResponse document = employeeDocumentService.getDocumentById(id);

        return ApiResponse.success(document, "Get document successfully");
    }

    /**
     * Create document with file upload
     */
    @Operation(
        summary = "Create employee document",
        description = "Create a new employee document with file upload"
    )
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<EmployeeDocumentResponse> createDocument(
            @Parameter(description = "Document data (JSON)", required = true)
            @RequestPart("data") @Valid EmployeeDocumentCreateDto createDto,
            
            @Parameter(description = "Document file (PDF, DOCX, PNG, JPG)", required = false)
            @RequestPart(value = "file", required = false) MultipartFile file) {

        log.info("POST /documents - empId: {}, docType: {}", createDto.getEmpId(), createDto.getDocType());

        try {
            EmployeeDocumentResponse document = employeeDocumentService.createDocument(createDto, file);

            return ApiResponse.<EmployeeDocumentResponse>builder()
                .code(201)
                .status("success")
                .message("Document created successfully")
                .data(document)
                .build();

        } catch (IOException e) {
            log.error("Failed to create document: {}", e.getMessage());
            return ApiResponse.badRequest("Failed to upload file: " + e.getMessage());
        }
    }

    /**
     * Update document with optional file replacement
     */
    @Operation(
        summary = "Update employee document",
        description = "Update an existing document (with optional file replacement)"
    )
    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<EmployeeDocumentResponse> updateDocument(
            @Parameter(description = "Document ID", required = true, example = "1")
            @PathVariable("id") Integer id,
            
            @Parameter(description = "Document update data (JSON)", required = true)
            @RequestPart("data") @Valid EmployeeDocumentUpdateDto updateDto,
            
            @Parameter(description = "New document file (optional)")
            @RequestPart(value = "file", required = false) MultipartFile file) {

        log.info("PUT /documents/{}", id);

        try {
            EmployeeDocumentResponse document = employeeDocumentService.updateDocument(id, updateDto, file);

            return ApiResponse.success(document, "Document updated successfully");

        } catch (IOException e) {
            log.error("Failed to update document: {}", e.getMessage());
            return ApiResponse.badRequest("Failed to upload file: " + e.getMessage());
        }
    }

    /**
     * Soft delete document
     */
    @Operation(
        summary = "Delete employee document",
        description = "Soft delete a document (sets isDeleted = true) and removes file from Cloudinary"
    )
    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteDocument(
            @Parameter(description = "Document ID", required = true, example = "1")
            @PathVariable("id") Integer id) {

        log.info("DELETE /documents/{}", id);

        try {
            employeeDocumentService.deleteDocument(id);
            return ApiResponse.successMessage("Document deleted successfully");

        } catch (IOException e) {
            log.error("Failed to delete document: {}", e.getMessage());
            return ApiResponse.badRequest("Failed to delete file: " + e.getMessage());
        }
    }

    /**
     * Get download URL for document
     */
    @Operation(
        summary = "Get download URL",
        description = "Get direct download URL for an employee document (Cloudinary URL)"
    )
    @GetMapping("/{id}/download")
    public ApiResponse<String> getDownloadUrl(
            @Parameter(description = "Document ID", required = true, example = "1")
            @PathVariable("id") Integer id) {

        log.info("GET /documents/{}/download", id);

        String downloadUrl = employeeDocumentService.getDownloadUrl(id);

        return ApiResponse.success(downloadUrl, "Download URL retrieved successfully");
    }
}