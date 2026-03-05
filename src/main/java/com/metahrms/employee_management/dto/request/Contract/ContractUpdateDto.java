package com.metahrms.employee_management.dto.request.Contract;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.metahrms.employee_management.enums.ContractStatus;
import com.metahrms.employee_management.enums.ContractType;

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
@Schema(description = "Data Transfer Object for updating an existing employment contract")
public class ContractUpdateDto {

    @Schema(description = "Type of the contract", example = "FULL_TIME")
    ContractType contractType;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy")
    @Schema(description = "Contract start date", example = "01/01/2024", type = "string", pattern = "dd/MM/yyyy")
    LocalDate startDate;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy")
    @Schema(description = "Contract end date", example = "31/12/2025", type = "string", pattern = "dd/MM/yyyy")
    LocalDate endDate;

    @Schema(description = "URL to the contract document in S3 storage", example = "1/contracts/contract-123.pdf")
    String fileUrl;

    @Schema(description = "Current status of the contract", example = "ACTIVE")
    ContractStatus status;

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private ContractType contractType;
        private LocalDate startDate;
        private LocalDate endDate;
        private String fileUrl;
        private ContractStatus status;

        public Builder contractType(ContractType contractType) {
            this.contractType = contractType;
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

        public Builder fileUrl(String fileUrl) {
            this.fileUrl = fileUrl;
            return this;
        }

        public Builder status(ContractStatus status) {
            this.status = status;
            return this;
        }

        public ContractUpdateDto build() {
            ContractUpdateDto dto = new ContractUpdateDto();
            dto.contractType = this.contractType;
            dto.startDate = this.startDate;
            dto.endDate = this.endDate;
            dto.fileUrl = this.fileUrl;
            dto.status = this.status;
            return dto;
        }
    }
}
