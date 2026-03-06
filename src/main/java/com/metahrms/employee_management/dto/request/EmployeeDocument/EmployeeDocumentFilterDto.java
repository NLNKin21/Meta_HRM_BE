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
@Schema(description = "Filter criteria for searching and filtering employee documents")
public class EmployeeDocumentFilterDto {

    @Schema(description = "Page number for pagination (zero-based)", example = "0")
    Integer page = 0;

    @Schema(description = "Number of items per page", example = "10")
    Integer pageSize = 10;

    @Schema(description = "Filter by employee ID", example = "123")
    Integer empId;

    @Schema(description = "Filter by document type", example = "ID_CARD")
    DocumentType docType;

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Integer page = 0;
        private Integer pageSize = 10;
        private Integer empId;
        private DocumentType docType;

        public Builder page(Integer page) {
            this.page = page;
            return this;
        }

        public Builder pageSize(Integer pageSize) {
            this.pageSize = pageSize;
            return this;
        }

        public Builder empId(Integer empId) {
            this.empId = empId;
            return this;
        }

        public Builder docType(DocumentType docType) {
            this.docType = docType;
            return this;
        }

        public EmployeeDocumentFilterDto build() {
            EmployeeDocumentFilterDto dto = new EmployeeDocumentFilterDto();
            dto.page = this.page;
            dto.pageSize = this.pageSize;
            dto.empId = this.empId;
            dto.docType = this.docType;
            return dto;
        }
    }
}
