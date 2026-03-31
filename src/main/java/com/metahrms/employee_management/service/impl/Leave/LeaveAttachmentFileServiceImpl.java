package com.metahrms.employee_management.service.impl.Leave;

import com.metahrms.employee_management.dto.response.Leave.LeaveAttachmentUploadResponseDto;
import com.metahrms.employee_management.exception.BadRequestException;
import com.metahrms.employee_management.service.Leave.LeaveAttachmentFileService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.util.UUID;

@Service
public class LeaveAttachmentFileServiceImpl implements LeaveAttachmentFileService {

    @Value("${app.upload.leave-dir:uploads/leave}")
    private String uploadDir;

    @Override
    public LeaveAttachmentUploadResponseDto uploadPdf(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new BadRequestException("Vui lòng chọn file PDF");
        }

        String originalName = StringUtils.cleanPath(file.getOriginalFilename());

        if (originalName.isBlank()) {
            throw new BadRequestException("Tên file không hợp lệ");
        }

        // check PDF
        if (!originalName.toLowerCase().endsWith(".pdf")) {
            throw new BadRequestException("Chỉ cho phép file PDF");
        }

        // max 5MB
        if (file.getSize() > 5 * 1024 * 1024) {
            throw new BadRequestException("File tối đa 5MB");
        }

        try {
            Path uploadPath = Paths.get(uploadDir).toAbsolutePath().normalize();
            Files.createDirectories(uploadPath);

            String fileName = UUID.randomUUID() + "_" + originalName;
            Path target = uploadPath.resolve(fileName);

            Files.copy(file.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);

            return LeaveAttachmentUploadResponseDto.builder()
                    .fileName(originalName)
                    .fileUrl("/uploads/leave/" + fileName)
                    .fileType("application/pdf")
                    .fileSize(file.getSize())
                    .build();

        } catch (IOException e) {
            throw new BadRequestException("Upload file thất bại");
        }
    }
}