package com.metahrms.employee_management.dto.request.EmployeeDocument;

import com.metahrms.employee_management.enums.DocumentType;

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
@Schema(description = "Data Transfer Object for updating an existing employee document")
public class EmployeeDocumentUpdateDto {

    @Schema(description = "Type of the document", example = "ID_CARD")
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
        private DocumentType docType;
        private String fileUrl;
        private String originalName;
        private Long fileSize;

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

        public EmployeeDocumentUpdateDto build() {
            EmployeeDocumentUpdateDto dto = new EmployeeDocumentUpdateDto();
            dto.docType = this.docType;
            dto.fileUrl = this.fileUrl;
            dto.originalName = this.originalName;
            dto.fileSize = this.fileSize;
            return dto;
        }
    }
}
