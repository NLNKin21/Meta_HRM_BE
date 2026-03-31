package com.metahrms.employee_management.service.Leave;

import com.metahrms.employee_management.dto.response.Leave.LeaveAttachmentUploadResponseDto;
import org.springframework.web.multipart.MultipartFile;

public interface LeaveAttachmentFileService {
    LeaveAttachmentUploadResponseDto uploadPdf(MultipartFile file);
}