package com.metahrms.employee_management.dto.request.EmployeeDocument;

import com.metahrms.employee_management.enums.DocumentType;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Schema(description = "Data Transfer Object for creating a new employee document")
public class EmployeeDocumentCreateDto {

    @Schema(description = "Employee ID to associate the document with", example = "123")
    Integer empId;

    @Schema(description = "Type of the document", example = "ID_CARD", required = true)
    @NotNull(message = "Document type is required")
    DocumentType docType;

    @Schema(description = "URL to the document file in S3 storage", example = "https://s3.amazonaws.com/bucket/documents/doc-123.pdf")
    String fileUrl;

    @Schema(description = "Original filename of the uploaded document", example = "CMND_NguyenVanA.pdf")
    String originalName;

    @Schema(description = "File size in bytes", example = "2048576")
    Long fileSize;

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Integer empId;
        private DocumentType docType;
        private String fileUrl;
        private String originalName;
        private Long fileSize;

        public Builder empId(Integer empId) {
            this.empId = empId;
            return this;
        }

        public Builder docType(DocumentType docType) {
            this.docType = docType;
            return this;
        }

        public Builder fileUrl(String fileUrl) {
            this.fileUrl = fileUrl;
            return this;
        }

        public Builder originalName(String originalName) {
            this.originalName = originalName;
            return this;
        }

        public Builder fileSize(Long fileSize) {
            this.fileSize = fileSize;
            return this;
        }

        public EmployeeDocumentCreateDto build() {
            EmployeeDocumentCreateDto dto = new EmployeeDocumentCreateDto();
            dto.empId = this.empId;
            dto.docType = this.docType;
            dto.fileUrl = this.fileUrl;
            dto.originalName = this.originalName;
            dto.fileSize = this.fileSize;
            return dto;
        }
    }
}
