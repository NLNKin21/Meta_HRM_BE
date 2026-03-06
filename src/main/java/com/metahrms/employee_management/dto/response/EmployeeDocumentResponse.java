package com.metahrms.employee_management.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class EmployeeDocumentResponse {

    private Long id;
    private String documentName;
    private String documentType;
    private String fileUrl;
    private String description;
    private LocalDateTime uploadedAt;
}