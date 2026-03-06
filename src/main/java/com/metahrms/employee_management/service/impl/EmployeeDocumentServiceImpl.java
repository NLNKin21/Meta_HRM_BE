package com.metahrms.employee_management.service.impl;

import com.metahrms.employee_management.dto.request.EmployeeDocumentRequest;
import com.metahrms.employee_management.dto.response.EmployeeDocumentResponse;
import com.metahrms.employee_management.entity.Employee;
import com.metahrms.employee_management.entity.EmployeeDocument;
import com.metahrms.employee_management.repository.EmployeeDocumentRepository;
import com.metahrms.employee_management.repository.EmployeeRepository;
import com.metahrms.employee_management.service.EmployeeDocumentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EmployeeDocumentServiceImpl implements EmployeeDocumentService {

    private final EmployeeDocumentRepository documentRepository;
    private final EmployeeRepository employeeRepository;

    @Override
    public EmployeeDocumentResponse create(EmployeeDocumentRequest request) {

        Employee employee = employeeRepository.findById(request.getEmployeeId())
                .orElseThrow(() -> new RuntimeException("Employee not found"));

        EmployeeDocument document = EmployeeDocument.builder()
                .documentName(request.getDocumentName())
                .documentType(request.getDocumentType())
                .fileUrl(request.getFileUrl())
                .description(request.getDescription())
                .uploadedAt(LocalDateTime.now())
                .employee(employee)
                .build();

        documentRepository.save(document);

        return mapToResponse(document);
    }

    @Override
    public List<EmployeeDocumentResponse> getByEmployee(Long employeeId) {

        return documentRepository.findByEmployeeId(employeeId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public void delete(Long id) {
        documentRepository.deleteById(id);
    }

    private EmployeeDocumentResponse mapToResponse(EmployeeDocument doc) {
        return EmployeeDocumentResponse.builder()
                .id(doc.getId())
                .documentName(doc.getDocumentName())
                .documentType(doc.getDocumentType())
                .fileUrl(doc.getFileUrl())
                .description(doc.getDescription())
                .uploadedAt(doc.getUploadedAt())
                .build();
    }
}