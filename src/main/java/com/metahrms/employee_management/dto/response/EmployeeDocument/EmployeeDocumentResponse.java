package com.metahrms.employee_management.dto.response.EmployeeDocument;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@Schema(description = "Response object containing employee document details")
public class EmployeeDocumentResponse {
    @Schema(description = "Unique identifier of the document", example = "1")
    Integer id;

    @Schema(description = "Employee ID associated with this document", example = "123")
    Integer empId;

    @Schema(description = "Full name of the employee", example = "Nguyễn Văn A")
    String employeeName;

    @Schema(description = "Type of the document", example = "ID_CARD")
    String docType;

    @Schema(description = "URL to the document file in S3 storage", example = "https://s3.amazonaws.com/bucket/documents/doc-123.pdf")
    String fileUrl;

    @Schema(description = "Original filename of the uploaded document", example = "CMND_NguyenVanA.pdf")
    String originalName;

    @Schema(description = "File size in bytes", example = "2048576")
    Long fileSize;

    @Schema(description = "Document upload timestamp", example = "15/11/2025")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy")
    LocalDateTime createdAt;

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Integer id;
        private Integer empId;
        private String employeeName;
        private String docType;
        private String fileUrl;
        private String originalName;
        private Long fileSize;
        private LocalDateTime createdAt;

        public Builder id(Integer id) {
            this.id = id;
            return this;
        }

        public Builder empId(Integer empId) {
            this.empId = empId;
            return this;
        }

        public Builder employeeName(String employeeName) {
            this.employeeName = employeeName;
            return this;
        }

        public Builder docType(String docType) {
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

        public Builder createdAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public EmployeeDocumentResponse build() {
            EmployeeDocumentResponse response = new EmployeeDocumentResponse();
            response.id = this.id;
            response.empId = this.empId;
            response.employeeName = this.employeeName;
            response.docType = this.docType;
            response.fileUrl = this.fileUrl;
            response.originalName = this.originalName;
            response.fileSize = this.fileSize;
            response.createdAt = this.createdAt;
            return response;
        }
    }
}
