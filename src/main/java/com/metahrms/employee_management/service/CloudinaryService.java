package com.metahrms.employee_management.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class CloudinaryService {

    private final Cloudinary cloudinary;

    @Value("${cloudinary.folder:metahrm}")
    private String baseFolder;


    // ✅ Các extension có thể preview trong iframe
    private static final Set<String> IMAGE_EXTENSIONS = Set.of(
        "jpg", "jpeg", "png", "gif", "webp", "bmp", "svg"
    );
    
    private static final Set<String> PREVIEWABLE_EXTENSIONS = Set.of(
        "jpg", "jpeg", "png", "gif", "webp", "bmp", "svg", "pdf"
    );

    // ============================================
    // UPLOAD METHODS
    // ============================================

    /**
     * ✅ Upload file - TỐI ƯU CHO IFRAME PREVIEW
     * 
     * QUAN TRỌNG:
     * - PDF upload dưới dạng "image" → Cloudinary cho phép inline view
     * - Image upload dưới dạng "image" → luôn preview được
     * - DOCX/XLSX upload dưới dạng "raw" → chỉ download, không preview
     */
    public String uploadFile(MultipartFile file) throws IOException {
        log.info("[CLOUDINARY] Uploading file: name={}, size={}, contentType={}", 
            file.getOriginalFilename(), file.getSize(), file.getContentType());

        String originalFilename = file.getOriginalFilename();
        String extension = getFileExtension(originalFilename);
        
        // ✅ XÁC ĐỊNH resource_type PHÙ HỢP
        String resourceType = determineResourceType(extension, file.getContentType());
        
        log.info("[CLOUDINARY] Determined resource_type='{}' for extension='{}'", 
            resourceType, extension);

        Map<String, Object> params = ObjectUtils.asMap(
            "folder", baseFolder + "/contracts",
            "resource_type", resourceType,
            "access_mode", "public",    // ← THÊM DÒNG NÀY
            "type", "upload",           // ← THÊM DÒNG NÀY
            "use_filename", true,           // Giữ tên file gốc
            "unique_filename", true,        // Thêm suffix unique
            "overwrite", false
        );

        // ✅ Với PDF: thêm flag để Cloudinary xử lý như document có thể view
        if ("pdf".equalsIgnoreCase(extension)) {
            params.put("pages", true);  // Enable PDF processing
        }

        Map<String, Object> result = cloudinary.uploader().upload(file.getBytes(), params);

        String secureUrl = (String) result.get("secure_url");
        String publicId = (String) result.get("public_id");
        String actualResourceType = (String) result.get("resource_type");
        
        log.info("[CLOUDINARY] Upload success: url={}, publicId={}, resourceType={}", 
            secureUrl, publicId, actualResourceType);

        return secureUrl;
    }

    /**
     * ✅ Upload file cho contract - ĐẢM BẢO PREVIEW ĐƯỢC
     * Trả về cả URL và metadata
     */
    public UploadResult uploadContractFile(MultipartFile file) throws IOException {
        log.info("[CLOUDINARY] Uploading contract file: name={}, size={}", 
            file.getOriginalFilename(), file.getSize());

        String originalFilename = file.getOriginalFilename();
        String extension = getFileExtension(originalFilename);
        String contentType = file.getContentType();
        String resourceType = determineResourceType(extension, contentType);

        Map<String, Object> params = ObjectUtils.asMap(
            "folder", baseFolder + "/contracts",
            "resource_type", resourceType,
            "access_mode", "public",    // ← THÊM DÒNG NÀY
            "type", "upload",           // ← THÊM DÒNG NÀY
            "use_filename", true,
            "unique_filename", true,
            "overwrite", false
        );

        Map<String, Object> result = cloudinary.uploader().upload(file.getBytes(), params);

        String secureUrl = (String) result.get("secure_url");
        String publicId = (String) result.get("public_id");
        String format = (String) result.get("format");
        String actualResourceType = (String) result.get("resource_type");

        // ✅ Tạo preview URL
        String previewUrl = generatePreviewUrl(secureUrl, extension, actualResourceType);

        log.info("[CLOUDINARY] Upload contract file success: url={}, previewUrl={}", 
            secureUrl, previewUrl);

        return UploadResult.builder()
            .fileUrl(secureUrl)
            .publicId(publicId)
            .previewUrl(previewUrl)
            .format(format != null ? format : extension)
            .resourceType(actualResourceType)
            .originalFilename(originalFilename)
            .fileSize(file.getSize())
            .previewable(isPreviewable(extension))
            .build();
    }

    /**
     * ✅ Xác định resource_type phù hợp
     * 
     * TRICK: Upload PDF dưới dạng "image" để Cloudinary 
     * trả về URL có thể view inline trong browser
     */
    private String determineResourceType(String extension, String contentType) {
        if (extension == null) return "auto";

        String ext = extension.toLowerCase();

        // ✅ Ảnh → luôn dùng "image"
        if (IMAGE_EXTENSIONS.contains(ext)) {
            return "image";
        }

        // ✅ PDF → dùng "image" để có thể preview inline
        // Cloudinary xử lý PDF dưới resource_type "image" → 
        // URL dạng /image/upload/ → browser hiển thị inline
        if ("pdf".equals(ext)) {
            return "image";
        }

        // ❌ DOCX, XLSX, etc → dùng "raw" (chỉ download)
        return "raw";
    }

    /**
     * ✅ Tạo preview URL
     * Cloudinary có thể transform PDF thành ảnh để preview
     */
    private String generatePreviewUrl(String secureUrl, String extension, String resourceType) {
        if (secureUrl == null) return null;

        String ext = extension != null ? extension.toLowerCase() : "";

        // ✅ Ảnh → URL gốc đã preview được
        if (IMAGE_EXTENSIONS.contains(ext)) {
            return secureUrl;
        }

        // ✅ PDF uploaded as "image" → URL gốc đã view được trong iframe
        if ("pdf".equals(ext) && "image".equals(resourceType)) {
            return secureUrl;
        }

        // ✅ PDF uploaded as "raw" → không preview trực tiếp
        // Dùng Google Docs Viewer làm fallback
        if ("pdf".equals(ext)) {
            return "https://docs.google.com/gview?url=" 
                + secureUrl + "&embedded=true";
        }

        // ✅ DOCX, XLSX → dùng Google Docs Viewer  
        if ("doc".equals(ext) || "docx".equals(ext) 
            || "xls".equals(ext) || "xlsx".equals(ext)) {
            return "https://docs.google.com/gview?url=" 
                + secureUrl + "&embedded=true";
        }

        return secureUrl;
    }

    // ============================================
    // UPLOAD FILE VỚI FOLDER VÀ PUBLIC ID
    // ============================================

    public String uploadFile(MultipartFile file, String folder, String publicId) throws IOException {
        log.info("[CLOUDINARY] Uploading file: folder={}, publicId={}, size={}", 
            folder, publicId, file.getSize());

        Map<String, Object> params = ObjectUtils.asMap(
            "folder", baseFolder + "/" + folder,
            "public_id", publicId,
            "overwrite", true,
            "resource_type", "image"
        );

        Map<String, Object> result = cloudinary.uploader().upload(file.getBytes(), params);
        String secureUrl = (String) result.get("secure_url");
        log.info("[CLOUDINARY] Upload success: {}", secureUrl);
        return secureUrl;
    }

    public String uploadBase64(String base64Image, String folder, String publicId) throws IOException {
        log.info("[CLOUDINARY] Uploading base64: folder={}, publicId={}", folder, publicId);

        String dataUri = base64Image;
        if (!base64Image.startsWith("data:")) {
            dataUri = "data:image/jpeg;base64," + base64Image;
        }

        Map<String, Object> params = ObjectUtils.asMap(
            "folder", baseFolder + "/" + folder,
            "public_id", publicId,
            "overwrite", true,
            "resource_type", "image"
        );

        Map<String, Object> result = cloudinary.uploader().upload(dataUri, params);
        String secureUrl = (String) result.get("secure_url");
        log.info("[CLOUDINARY] Upload success: {}", secureUrl);
        return secureUrl;
    }

    // ============================================
    // DELETE & HELPER METHODS
    // ============================================

    public void deleteFile(String publicId) throws IOException {
        if (publicId == null || publicId.isEmpty()) {
            log.warn("[CLOUDINARY] Cannot delete: publicId is null/empty");
            return;
        }

        try {
            log.info("[CLOUDINARY] Deleting file: {}", publicId);
            
            // ✅ Thử xóa với resource_type "image" trước (PDF + ảnh)
            Map<String, Object> result = cloudinary.uploader()
                .destroy(publicId, ObjectUtils.asMap("resource_type", "image"));
            
            String deleteResult = (String) result.get("result");
            
            if (!"ok".equals(deleteResult)) {
                // ✅ Nếu không tìm thấy, thử với "raw"
                log.info("[CLOUDINARY] Not found as image, trying raw: {}", publicId);
                result = cloudinary.uploader()
                    .destroy(publicId, ObjectUtils.asMap("resource_type", "raw"));
                deleteResult = (String) result.get("result");
            }

            if ("ok".equals(deleteResult)) {
                log.info("[CLOUDINARY] File deleted successfully: {}", publicId);
            } else {
                log.warn("[CLOUDINARY] File not found or already deleted: {}", publicId);
            }
        } catch (Exception e) {
            log.error("[CLOUDINARY] Delete failed for: {}", publicId, e);
            throw new IOException("Failed to delete file from Cloudinary", e);
        }
    }

    public String extractPublicId(String cloudinaryUrl) {
        if (cloudinaryUrl == null || cloudinaryUrl.isEmpty()) return null;

        try {
            String marker = "/upload/";
            int uploadIndex = cloudinaryUrl.indexOf(marker);
            if (uploadIndex == -1) {
                log.warn("[CLOUDINARY] Cannot extract publicId from URL: {}", cloudinaryUrl);
                return null;
            }

            String afterUpload = cloudinaryUrl.substring(uploadIndex + marker.length());

            if (afterUpload.matches("^v\\d+/.*")) {
                afterUpload = afterUpload.substring(afterUpload.indexOf("/") + 1);
            }

            int dotIndex = afterUpload.lastIndexOf(".");
            if (dotIndex != -1) {
                afterUpload = afterUpload.substring(0, dotIndex);
            }

            log.debug("[CLOUDINARY] Extracted publicId: {}", afterUpload);
            return afterUpload;
        } catch (Exception e) {
            log.error("[CLOUDINARY] Failed to extract publicId from: {}", cloudinaryUrl, e);
            return null;
        }
    }

    // ============================================
    // UTILITY METHODS
    // ============================================

    private String getFileExtension(String filename) {
        if (filename == null || !filename.contains(".")) return null;
        return filename.substring(filename.lastIndexOf(".") + 1).toLowerCase();
    }

    private boolean isPreviewable(String extension) {
        if (extension == null) return false;
        return PREVIEWABLE_EXTENSIONS.contains(extension.toLowerCase());
    }

    // ============================================
    // CONVENIENCE METHODS
    // ============================================

    public String uploadAvatar(MultipartFile file, Long employeeId) throws IOException {
        String publicId = "emp_" + employeeId + "_avatar";
        return uploadFile(file, "avatars", publicId);
    }

    public String uploadFaceImage(String base64Image, Long employeeId) throws IOException {
        String publicId = "emp_" + employeeId + "_face_" + System.currentTimeMillis();
        return uploadBase64(base64Image, "faces", publicId);
    }

    public String uploadCheckInPhoto(String base64Image, Long employeeId) throws IOException {
        String publicId = "checkin_" + employeeId + "_" + System.currentTimeMillis();
        return uploadBase64(base64Image, "attendance", publicId);
    }

    public String uploadCheckOutPhoto(String base64Image, Long employeeId) throws IOException {
        String publicId = "checkout_" + employeeId + "_" + System.currentTimeMillis();
        return uploadBase64(base64Image, "attendance", publicId);
    }

    // ============================================
    // UPLOAD RESULT DTO
    // ============================================

    @lombok.Data
    @lombok.Builder
    @lombok.NoArgsConstructor
    @lombok.AllArgsConstructor
    public static class UploadResult {
        private String fileUrl;         // URL gốc trên Cloudinary
        private String publicId;        // Public ID để xóa
        private String previewUrl;      // URL để iframe preview
        private String format;          // pdf, jpg, png, docx...
        private String resourceType;    // image, raw, video
        private String originalFilename;
        private long fileSize;
        private boolean previewable;    // Có thể preview trong iframe không
    }
}