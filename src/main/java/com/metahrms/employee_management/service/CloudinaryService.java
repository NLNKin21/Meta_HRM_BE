package com.metahrms.employee_management.service;

import com.cloudinary.Cloudinary;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class CloudinaryService {

    private final Cloudinary cloudinary;

    @Value("${cloudinary.folder}")
    private String folder;

    public String uploadFile(MultipartFile file) throws IOException {
        log.info("📤 Uploading: {} | Type: {}", file.getOriginalFilename(), file.getContentType());

        validateFile(file);

        String contentType = file.getContentType();
        String fileName = UUID.randomUUID().toString();
        String extension = getFileExtension(file.getOriginalFilename(), contentType);
        String fileNameWithExtension = fileName + extension;

        try {
            Map<String, Object> options = new HashMap<>();
            options.put("public_id", folder + "/" + fileNameWithExtension);
            options.put("type", "upload");
            options.put("overwrite", false);

            if ("application/pdf".equals(contentType)) {
                options.put("resource_type", "raw");
                log.info("📄 Uploading PDF: {}", fileNameWithExtension);
            } else if (contentType != null && contentType.startsWith("image/")) {
                options.put("resource_type", "image");
            } else {
                options.put("resource_type", "raw");
            }

            Map<?, ?> uploadResult = cloudinary.uploader().upload(file.getBytes(), options);

            String secureUrl = (String) uploadResult.get("secure_url");
            log.info("✅ Upload success: {}", secureUrl);

            return secureUrl;

        } catch (IOException e) {
            log.error("❌ Upload failed: {}", e.getMessage());
            throw new IOException("Failed to upload file: " + e.getMessage());
        }
    }

    public void deleteFile(String fileUrl) throws IOException {
        if (fileUrl == null || fileUrl.isEmpty()) return;

        try {
            String publicId = extractPublicId(fileUrl);
            String resourceType = getResourceTypeFromUrl(fileUrl);

            Map<String, Object> options = new HashMap<>();
            options.put("resource_type", resourceType);
            options.put("type", "upload");

            Map<?, ?> result = cloudinary.uploader().destroy(publicId, options);
            log.info("🗑️ Delete result: {}", result.get("result"));

        } catch (Exception e) {
            log.error("❌ Delete failed: {}", e.getMessage());
        }
    }

    public String extractPublicId(String fileUrl) {
        if (fileUrl == null || fileUrl.isEmpty()) return null;

        try {
            int uploadIndex = fileUrl.indexOf("/upload/");
            if (uploadIndex == -1) return null;

            String afterUpload = fileUrl.substring(uploadIndex + 8);
            String withoutVersion = afterUpload.replaceFirst("v\\d+/", "");
            
            // Raw files: keep extension
            if (fileUrl.contains("/raw/upload/")) {
                return withoutVersion;
            }
            
            // Image files: remove extension
            return withoutVersion.replaceFirst("\\.[^.]+$", "");

        } catch (Exception e) {
            log.error("❌ Extract failed: {}", fileUrl);
            return null;
        }
    }

    private String getResourceTypeFromUrl(String url) {
        if (url == null) return "raw";
        if (url.contains("/image/upload/")) return "image";
        if (url.contains("/video/upload/")) return "video";
        return "raw";
    }

    private String getFileExtension(String originalFilename, String contentType) {
        if (originalFilename != null && originalFilename.contains(".")) {
            return originalFilename.substring(originalFilename.lastIndexOf("."));
        }
        
        if (contentType != null) {
            switch (contentType) {
                case "application/pdf": return ".pdf";
                case "image/png": return ".png";
                case "image/jpeg":
                case "image/jpg": return ".jpg";
                case "application/vnd.openxmlformats-officedocument.wordprocessingml.document": return ".docx";
                case "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet": return ".xlsx";
                case "application/msword": return ".doc";
            }
        }
        
        return "";
    }

    private void validateFile(MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            throw new IOException("File is empty");
        }

        if (file.getSize() > 10 * 1024 * 1024) {
            throw new IOException("File exceeds 10MB");
        }

        String contentType = file.getContentType();
        if (contentType == null) {
            throw new IOException("Cannot determine file type");
        }

        String[] allowedTypes = {
            "application/pdf",
            "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
            "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
            "application/msword",
            "image/png",
            "image/jpeg"
        };

        boolean isValid = false;
        for (String type : allowedTypes) {
            if (type.equals(contentType)) {
                isValid = true;
                break;
            }
        }

        if (!isValid) {
            throw new IOException("Invalid file type: " + contentType);
        }
    }
}