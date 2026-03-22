package com.metahrms.employee_management.dto.response.EmployeeDocument;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Schema(description = "Employee document response")
public class EmployeeDocumentResponse {
    
    @Schema(description = "Document ID", example = "1")
    Integer id;

    @Schema(description = "Employee ID", example = "123")
    Integer empId;

    @Schema(description = "Employee name", example = "Nguyễn Văn A")
    String employeeName;

    @Schema(description = "Document type", example = "ID_CARD")
    String docType;

    @Schema(description = "File URL", example = "https://res.cloudinary.com/dyjfpbj5e/raw/upload/...")
    String fileUrl;

    @Schema(description = "Cloudinary public ID", example = "hrm-documents/abc-123")
    String fileKey;  // ✅ Thêm field

    @Schema(description = "Original file name", example = "cmnd.pdf")
    String originalName;

    @Schema(description = "File size in bytes", example = "102400")
    Long fileSize;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy HH:mm:ss")
    @Schema(description = "Created timestamp", example = "28/11/2024 14:30:00")
    LocalDateTime createdAt;

    // ✅ Custom Builder
    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Integer id;
        private Integer empId;
        private String employeeName;
        private String docType;
        private String fileUrl;
        private String fileKey;
        private String originalName;
        private Long fileSize;
        private LocalDateTime createdAt;

        public Builder id(Integer id) { this.id = id; return this; }
        public Builder empId(Integer empId) { this.empId = empId; return this; }
        public Builder employeeName(String employeeName) { this.employeeName = employeeName; return this; }
        public Builder docType(String docType) { this.docType = docType; return this; }
        public Builder fileUrl(String fileUrl) { this.fileUrl = fileUrl; return this; }
        public Builder fileKey(String fileKey) { this.fileKey = fileKey; return this; }
        public Builder originalName(String originalName) { this.originalName = originalName; return this; }
        public Builder fileSize(Long fileSize) { this.fileSize = fileSize; return this; }
        public Builder createdAt(LocalDateTime createdAt) { this.createdAt = createdAt; return this; }

        public EmployeeDocumentResponse build() {
            EmployeeDocumentResponse response = new EmployeeDocumentResponse();
            response.id = this.id;
            response.empId = this.empId;
            response.employeeName = this.employeeName;
            response.docType = this.docType;
            response.fileUrl = this.fileUrl;
            response.fileKey = this.fileKey;
            response.originalName = this.originalName;
            response.fileSize = this.fileSize;
            response.createdAt = this.createdAt;
            return response;
        }
    }
}
