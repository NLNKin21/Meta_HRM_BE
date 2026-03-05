package com.metahrms.employee_management.entity;

import com.metahrms.employee_management.enums.DocumentType;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Table(name = "employee_documents")
@Data
@EqualsAndHashCode(callSuper = true)
public class EmployeeDocument extends BaseEntity {

    @Column(name = "emp_id")
    private Integer empId;

    @Column(name = "doc_type", length = 100)
    private DocumentType docType;

    @Column(name = "file_url", length = 255)
    private String fileUrl;

    @Column(name = "original_name", length = 255)
    private String originalName;

    @Column(name = "file_size")
    private Long fileSize;

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

        public EmployeeDocument build() {
            EmployeeDocument employeeDocument = new EmployeeDocument();
            employeeDocument.empId = this.empId;
            employeeDocument.docType = this.docType;
            employeeDocument.fileUrl = this.fileUrl;
            employeeDocument.originalName = this.originalName;
            employeeDocument.fileSize = this.fileSize;
            return employeeDocument;
        }
    }
}
