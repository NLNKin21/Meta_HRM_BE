package com.metahrms.employee_management.dto.request;

import lombok.Data;

@Data
public class EmployeeDocumentRequest {

    private String documentName;
    private String documentType;
    private String fileUrl;
    private String description;
    private Long employeeId;
}