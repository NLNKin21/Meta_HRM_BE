package com.metahrms.employee_management.dto.response.Leave;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LeaveAttachmentUploadResponseDto {
    private String fileName;
    private String fileUrl;
    private String fileType;
    private Long fileSize;
}