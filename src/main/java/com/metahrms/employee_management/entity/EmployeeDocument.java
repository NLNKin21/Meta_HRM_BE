package com.metahrms.employee_management.entity;

import com.metahrms.employee_management.enums.DocumentType;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "employee_documents")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class EmployeeDocument extends BaseEntity {

    @Column(name = "emp_id", nullable = false)
    private Integer empId;

    @Enumerated(EnumType.STRING)
    @Column(name = "doc_type", nullable = false, length = 50)
    private DocumentType docType;

    @Column(name = "file_url", length = 500)
    private String fileUrl;

    @Column(name = "file_key", length = 255)  // ✅ Thêm field
    private String fileKey;

    @Column(name = "original_name", length = 255)
    private String originalName;

    @Column(name = "file_size")
    private Long fileSize;

    // ✅ Custom Builder
    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Integer empId;
        private DocumentType docType;
        private String fileUrl;
        private String fileKey;
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

        public Builder fileKey(String fileKey) {
            this.fileKey = fileKey;
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

        public EmployeeDocument build() {
            EmployeeDocument document = new EmployeeDocument();
            document.empId = this.empId;
            document.docType = this.docType;
            document.fileUrl = this.fileUrl;
            document.fileKey = this.fileKey;
            document.originalName = this.originalName;
            document.fileSize = this.fileSize;
            return document;
        }
    }
}