package com.metahrms.employee_management.service;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.metahrms.employee_management.dto.request.EmployeeDocument.EmployeeDocumentCreateDto;
import com.metahrms.employee_management.dto.request.EmployeeDocument.EmployeeDocumentFilterDto;
import com.metahrms.employee_management.dto.request.EmployeeDocument.EmployeeDocumentUpdateDto;
import com.metahrms.employee_management.dto.response.PagedResponse;
import com.metahrms.employee_management.dto.response.EmployeeDocument.EmployeeDocumentResponse;
import com.metahrms.employee_management.entity.Employee;
import com.metahrms.employee_management.entity.EmployeeDocument;
import com.metahrms.employee_management.exception.ResourceNotFoundException;
import com.metahrms.employee_management.repository.EmployeeDocumentRepository;
import com.metahrms.employee_management.repository.EmployeeRepository;
import com.metahrms.employee_management.specification.EmployeeDocumentSpecification;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class EmployeeDocumentService {
    
    EmployeeDocumentRepository employeeDocumentRepository;
    EmployeeRepository employeeRepository;
    CloudinaryService cloudinaryService;  // ✅ Thay S3Service bằng CloudinaryService

    /**
     * Get documents with filtering and pagination
     */
    public PagedResponse<EmployeeDocumentResponse> getDocuments(EmployeeDocumentFilterDto filterDto) {
        log.info("Fetching documents with filters: empId={}, docType={}", 
                 filterDto.getEmpId(), filterDto.getDocType());

        Specification<EmployeeDocument> spec = EmployeeDocumentSpecification.filterDocuments(
            filterDto.getEmpId(),
            filterDto.getDocType()
        );

        Pageable pageable = PageRequest.of(
            filterDto.getPage(),
            filterDto.getPageSize(),
            Sort.by(Sort.Direction.DESC, "createdAt")
        );

        Page<EmployeeDocument> documentPage = employeeDocumentRepository.findAll(spec, pageable);

        List<EmployeeDocumentResponse> documentResponses = documentPage.getContent().stream()
            .map(this::toDocumentResponse)
            .collect(Collectors.toList());

        return PagedResponse.<EmployeeDocumentResponse>builder()
            .content(documentResponses)
            .currentPage(documentPage.getNumber())
            .pageSize(documentPage.getSize())
            .totalElements(documentPage.getTotalElements())
            .totalPages(documentPage.getTotalPages())
            .hasNext(documentPage.hasNext())
            .hasPrevious(documentPage.hasPrevious())
            .build();
    }

    /**
     * Get document by ID
     */
    public EmployeeDocumentResponse getDocumentById(Integer id) {
        log.info("Fetching document with id: {}", id);

        EmployeeDocument document = employeeDocumentRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Document not found with id: " + id));

        if (Boolean.TRUE.equals(document.getIsDeleted())) {
            throw new ResourceNotFoundException("Document has been deleted");
        }

        return toDocumentResponse(document);
    }

    /**
     * Create document with file upload
     */
    @Transactional
    public EmployeeDocumentResponse createDocument(EmployeeDocumentCreateDto createDto, MultipartFile file) throws IOException {
        log.info("Creating document for employee: {}", createDto.getEmpId());

        // ✅ Validate employee exists
        employeeRepository.findById(createDto.getEmpId())
            .orElseThrow(() -> new ResourceNotFoundException("Employee not found with id: " + createDto.getEmpId()));

        // ✅ Upload file to Cloudinary
        String fileUrl = null;
        String fileKey = null;
        Long fileSize = null;
        String originalName = null;

        if (file != null && !file.isEmpty()) {
            fileUrl = cloudinaryService.uploadFile(file);
            fileKey = cloudinaryService.extractPublicId(fileUrl);
            fileSize = file.getSize();
            originalName = file.getOriginalFilename();
            log.info("File uploaded successfully: {}", fileUrl);
        }

        // ✅ Create document entity
        EmployeeDocument document = EmployeeDocument.builder()
            .empId(createDto.getEmpId())
            .docType(createDto.getDocType())
            .fileUrl(fileUrl)
            .fileKey(fileKey)  // ✅ Lưu Cloudinary public_id
            .originalName(originalName != null ? originalName : createDto.getOriginalName())
            .fileSize(fileSize != null ? fileSize : createDto.getFileSize())
            .build();

        document.setIsDeleted(false);

        EmployeeDocument savedDocument = employeeDocumentRepository.save(document);
        log.info("Document created successfully with id: {}", savedDocument.getId());

        return toDocumentResponse(savedDocument);
    }

    /**
     * Update document (with optional file replacement)
     */
    @Transactional
    public EmployeeDocumentResponse updateDocument(Integer id, EmployeeDocumentUpdateDto updateDto, MultipartFile file) throws IOException {
        log.info("Updating document with id: {}", id);

        EmployeeDocument document = employeeDocumentRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Document not found with id: " + id));

        if (Boolean.TRUE.equals(document.getIsDeleted())) {
            throw new IllegalStateException("Cannot update deleted document");
        }

        // ✅ Update basic fields
        if (updateDto.getDocType() != null) {
            document.setDocType(updateDto.getDocType());
        }

        // ✅ Update file if provided
        if (file != null && !file.isEmpty()) {
            // Xóa file cũ trên Cloudinary
            if (document.getFileKey() != null && !document.getFileKey().isEmpty()) {
                try {
                    cloudinaryService.deleteFile(document.getFileKey());
                    log.info("Old file deleted: {}", document.getFileKey());
                } catch (IOException e) {
                    log.warn("Failed to delete old file: {}", e.getMessage());
                }
            }

            // Upload file mới
            String newFileUrl = cloudinaryService.uploadFile(file);
            String newFileKey = cloudinaryService.extractPublicId(newFileUrl);

            document.setFileUrl(newFileUrl);
            document.setFileKey(newFileKey);
            document.setOriginalName(file.getOriginalFilename());
            document.setFileSize(file.getSize());
            log.info("New file uploaded: {}", newFileUrl);
        }

        EmployeeDocument updatedDocument = employeeDocumentRepository.save(document);
        log.info("Document updated successfully: {}", id);

        return toDocumentResponse(updatedDocument);
    }

    /**
     * Soft delete document (and delete file from Cloudinary)
     */
    @Transactional
    public void deleteDocument(Integer id) throws IOException {
        log.info("Deleting document with id: {}", id);

        EmployeeDocument document = employeeDocumentRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Document not found with id: " + id));

        // ✅ Xóa file trên Cloudinary (nếu có)
        if (document.getFileKey() != null && !document.getFileKey().isEmpty()) {
            try {
                cloudinaryService.deleteFile(document.getFileKey());
                log.info("File deleted from Cloudinary: {}", document.getFileKey());
            } catch (IOException e) {
                log.error("Failed to delete file from Cloudinary: {}", e.getMessage());
            }
        }

        // ✅ Soft delete document
        document.setIsDeleted(true);
        employeeDocumentRepository.save(document);
        log.info("Document soft deleted successfully: {}", id);
    }

    /**
     * Get download URL for document
     */
    public String getDownloadUrl(Integer id) {
        log.info("Getting download URL for document id: {}", id);

        EmployeeDocument document = employeeDocumentRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Document not found with id: " + id));

        if (Boolean.TRUE.equals(document.getIsDeleted())) {
            throw new IllegalStateException("Cannot download deleted document");
        }

        if (document.getFileUrl() == null || document.getFileUrl().isEmpty()) {
            throw new IllegalStateException("Document file URL is not available");
        }

        return document.getFileUrl();  // ✅ Cloudinary URL trực tiếp
    }

    /**
     * Convert entity to response DTO
     */
    private EmployeeDocumentResponse toDocumentResponse(EmployeeDocument document) {
        String employeeName = null;
        if (document.getEmpId() != null) {
            employeeName = employeeRepository.findById(document.getEmpId())
                .map(Employee::getFullName)
                .orElse(null);
        }

        return EmployeeDocumentResponse.builder()
            .id(document.getId())
            .empId(document.getEmpId())
            .employeeName(employeeName)
            .docType(document.getDocType() != null ? document.getDocType().name() : null)
            .fileUrl(document.getFileUrl())  // ✅ Trực tiếp Cloudinary URL
            .fileKey(document.getFileKey())  // ✅ Thêm fileKey
            .originalName(document.getOriginalName())
            .fileSize(document.getFileSize())
            .createdAt(document.getCreatedAt())
            .build();
    }
}