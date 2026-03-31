package com.metahrms.employee_management.controller.Leave;

import com.metahrms.employee_management.dto.response.ApiResponse;
import com.metahrms.employee_management.dto.response.Leave.LeaveAttachmentUploadResponseDto;
import com.metahrms.employee_management.service.Leave.LeaveAttachmentFileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/leave-attachments")
@RequiredArgsConstructor
public class LeaveAttachmentController {

    private final LeaveAttachmentFileService fileService;

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<LeaveAttachmentUploadResponseDto> upload(
            @RequestPart("file") MultipartFile file
    ) {
        return ApiResponse.success(
                fileService.uploadPdf(file),
                "Upload PDF thành công"
        );
    }
}