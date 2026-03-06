package com.metahrms.employee_management.service;

import com.metahrms.employee_management.dto.request.EmployeeDocumentRequest;
import com.metahrms.employee_management.dto.response.EmployeeDocumentResponse;

import java.util.List;

public interface EmployeeDocumentService {

    EmployeeDocumentResponse create(EmployeeDocumentRequest request);

    List<EmployeeDocumentResponse> getByEmployee(Long employeeId);

    void delete(Long id);
}