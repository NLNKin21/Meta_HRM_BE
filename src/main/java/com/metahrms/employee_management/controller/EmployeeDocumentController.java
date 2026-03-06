package com.metahrms.employee_management.controller;

import com.metahrms.employee_management.dto.request.EmployeeDocumentRequest;
import com.metahrms.employee_management.dto.response.EmployeeDocumentResponse;
import com.metahrms.employee_management.service.EmployeeDocumentService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/documents")
@RequiredArgsConstructor
public class EmployeeDocumentController {

    private final EmployeeDocumentService documentService;

    @PostMapping
    public EmployeeDocumentResponse create(
            @RequestBody EmployeeDocumentRequest request) {
        return documentService.create(request);
    }

    @GetMapping("/employee/{employeeId}")
    public List<EmployeeDocumentResponse> getByEmployee(
            @PathVariable Long employeeId) {
        return documentService.getByEmployee(employeeId);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        documentService.delete(id);
    }
}