package com.metahrms.employee_management.dto.request.Contract;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.metahrms.employee_management.enums.ContractStatus;
import com.metahrms.employee_management.enums.ContractType;

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
@Schema(description = "Data transfer object for creating a new employment contract")
public class ContractCreateDto {

    @NotNull(message = "Employee ID is required")
    @Schema(description = "ID of the employee this contract is for", example = "1", required = true)
    Integer empId;

    @NotNull(message = "Contract type is required")
    @Schema(description = "Type of contract (PROBATION/FIXED_TERM/INDEFINITE)", example = "FIXED_TERM", required = true)
    ContractType contractType;

    @NotNull(message = "Start date is required")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy")
    @Schema(description = "Contract start date", example = "01/01/2024", type = "string", pattern = "dd/MM/yyyy", required = true)
    LocalDate startDate;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy")
    @Schema(description = "Contract end date (optional for indefinite contracts)", example = "31/12/2025", type = "string", pattern = "dd/MM/yyyy")
    LocalDate endDate;

    @Schema(description = "URL to the contract document file", example = "1/contracts/contract123.pdf")
    String fileUrl;

    @Schema(description = "Contract status (ACTIVE/EXPIRED/TERMINATED). Defaults to ACTIVE if not provided", example = "ACTIVE")
    ContractStatus status;

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Integer empId;
        private ContractType contractType;
        private LocalDate startDate;
        private LocalDate endDate;
        private String fileUrl;
        private ContractStatus status;

        public Builder empId(Integer empId) {
            this.empId = empId;
            return this;
        }

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

        public ContractCreateDto build() {
            ContractCreateDto dto = new ContractCreateDto();
            dto.empId = this.empId;
            dto.contractType = this.contractType;
            dto.startDate = this.startDate;
            dto.endDate = this.endDate;
            dto.fileUrl = this.fileUrl;
            dto.status = this.status;
            return dto;
        }
    }
}
