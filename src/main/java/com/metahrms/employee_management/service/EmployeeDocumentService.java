package com.metahrms.employee_management.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.metahrms.employee_management.dto.request.EmployeeDocument.EmployeeDocumentCreateDto;
import com.metahrms.employee_management.dto.request.EmployeeDocument.EmployeeDocumentFilterDto;
import com.metahrms.employee_management.dto.request.EmployeeDocument.EmployeeDocumentUpdateDto;
import com.metahrms.employee_management.dto.response.PagedResponse;
import com.metahrms.employee_management.dto.response.EmployeeDocument.EmployeeDocumentResponse;
import com.metahrms.employee_management.entity.Employee;
import com.metahrms.employee_management.entity.EmployeeDocument;
import com.metahrms.employee_management.repository.EmployeeDocumentRepository;
import com.metahrms.employee_management.repository.EmployeeRepository;
import com.metahrms.employee_management.specification.EmployeeDocumentSpecification;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class EmployeeDocumentService {
    EmployeeDocumentRepository employeeDocumentRepository;
    EmployeeRepository employeeRepository;
    S3Service s3Service;

    public PagedResponse<EmployeeDocumentResponse> getDocuments(EmployeeDocumentFilterDto filterDto) {
        // Build specification for filtering
        Specification<EmployeeDocument> spec = EmployeeDocumentSpecification.filterDocuments(
            filterDto.getEmpId(),
            filterDto.getDocType()
        );

        // Create pageable with sorting by createdAt descending
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

    public EmployeeDocumentResponse getDocumentById(Integer id) {
        EmployeeDocument document = employeeDocumentRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Document not found with id: " + id));

        if (document.getIsDeleted()) {
            throw new RuntimeException("Document has been deleted");
        }

        return toDocumentResponse(document);
    }

    public EmployeeDocumentResponse createDocument(EmployeeDocumentCreateDto createDto) {
        // Validate employee exists
        employeeRepository.findById(createDto.getEmpId())
            .orElseThrow(() -> new RuntimeException("Employee not found with id: " + createDto.getEmpId()));

        EmployeeDocument document = EmployeeDocument.builder()
            .empId(createDto.getEmpId())
            .docType(createDto.getDocType())
            .fileUrl(createDto.getFileUrl())
            .originalName(createDto.getOriginalName())
            .fileSize(createDto.getFileSize())
            .build();

        document.setIsDeleted(false);

        EmployeeDocument savedDocument = employeeDocumentRepository.save(document);
        return toDocumentResponse(savedDocument);
    }

    public EmployeeDocumentResponse updateDocument(Integer id, EmployeeDocumentUpdateDto updateDto) {
        EmployeeDocument document = employeeDocumentRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Document not found with id: " + id));

        if (document.getIsDeleted()) {
            throw new RuntimeException("Cannot update deleted document");
        }

        if (updateDto.getDocType() != null) {
            document.setDocType(updateDto.getDocType());
        }
        if (updateDto.getFileUrl() != null) {
            document.setFileUrl(updateDto.getFileUrl());
        }
        if (updateDto.getOriginalName() != null) {
            document.setOriginalName(updateDto.getOriginalName());
        }
        if (updateDto.getFileSize() != null) {
            document.setFileSize(updateDto.getFileSize());
        }

        EmployeeDocument updatedDocument = employeeDocumentRepository.save(document);
        return toDocumentResponse(updatedDocument);
    }

    public void deleteDocument(Integer id) {
        EmployeeDocument document = employeeDocumentRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Document not found with id: " + id));

        // Soft delete
        document.setIsDeleted(true);
        employeeDocumentRepository.save(document);
    }

    public EmployeeDocumentResponse toDocumentResponse(EmployeeDocument document) {
        // Get employee information
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
            .fileUrl(s3Service.getS3Url(document.getFileUrl()))
            .originalName(document.getOriginalName())
            .fileSize(document.getFileSize())
            .createdAt(document.getCreatedAt())
            .build();
    }

    public String getDownloadUrl(Integer id) {
        EmployeeDocument document = employeeDocumentRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Document not found with id: " + id));

        if (document.getIsDeleted()) {
            throw new RuntimeException("Cannot download deleted document");
        }

        if (document.getFileUrl() == null || document.getFileUrl().isEmpty()) {
            throw new RuntimeException("Document file URL is not available");
        }

        return s3Service.getS3Url(document.getFileUrl());
    }
}
