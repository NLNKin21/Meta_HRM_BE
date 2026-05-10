package com.metahrms.employee_management.dto.request.Contract;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.metahrms.employee_management.enums.ContractStatus;

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
@Schema(description = "Filter criteria for searching and filtering employment contracts")
public class ContractFilterDto {

    @Schema(description = "Page number for pagination (zero-based)", example = "0")
    Integer page = 0;

    @Schema(description = "Number of items per page", example = "10")
    Integer pageSize = 10;

    @Schema(description = "Filter by contract status", example = "ACTIVE")
    ContractStatus status;

    @Schema(description = "Filter by contract type ID", example = "1")
    Integer contractTypeId;

    @Schema(description = "Filter by employee ID", example = "123")
    Integer empId;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy")
    @Schema(
        description = "Filter by start date",
        example = "01/01/2024",
        type = "string",
        pattern = "dd/MM/yyyy"
    )
    LocalDate startDate;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy")
    @Schema(
        description = "Filter by end date",
        example = "31/12/2024",
        type = "string",
        pattern = "dd/MM/yyyy"
    )
    LocalDate endDate;

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Integer page = 0;
        private Integer pageSize = 10;
        private ContractStatus status;
        private Integer contractTypeId;
        private Integer empId;
        private LocalDate startDate;
        private LocalDate endDate;

        public Builder page(Integer page) {
            this.page = page;
            return this;
        }

        public Builder pageSize(Integer pageSize) {
            this.pageSize = pageSize;
            return this;
        }

        public Builder status(ContractStatus status) {
            this.status = status;
            return this;
        }

        public Builder contractTypeId(Integer contractTypeId) {
            this.contractTypeId = contractTypeId;
            return this;
        }

        public Builder empId(Integer empId) {
            this.empId = empId;
            return this;
        }

        public Builder startDate(LocalDate startDate) {
            this.startDate = startDate;
            return this;
        }

        public Builder endDate(LocalDate endDate) {
            this.endDate = endDate;
            return this;
        }

        public ContractFilterDto build() {
            ContractFilterDto dto = new ContractFilterDto();
            dto.page = this.page;
            dto.pageSize = this.pageSize;
            dto.status = this.status;
            dto.contractTypeId = this.contractTypeId;
            dto.empId = this.empId;
            dto.startDate = this.startDate;
            dto.endDate = this.endDate;
            return dto;
        }
    }
}