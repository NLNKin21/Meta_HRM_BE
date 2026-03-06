package com.metahrms.employee_management.dto.response.Contract;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@Schema(description = "Response object containing employment contract details")
public class ContractResponse {
    @Schema(description = "Unique identifier of the contract", example = "1")
    Integer id;

    @Schema(description = "Employee ID associated with this contract", example = "123")
    Integer empId;

    @Schema(description = "Full name of the employee", example = "Nguyễn Văn A")
    String employeeName;

    @Schema(description = "Type of the contract", example = "FULL_TIME")
    String contractType;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy")
    @Schema(description = "Contract start date", example = "01/01/2024", type = "string", pattern = "dd/MM/yyyy")
    LocalDate startDate;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy")
    @Schema(description = "Contract end date", example = "31/12/2025", type = "string", pattern = "dd/MM/yyyy")
    LocalDate endDate;

    @Schema(description = "URL to the contract document in S3 storage", example = "https://s3.amazonaws.com/bucket/contracts/contract-123.pdf")
    String fileUrl;

    @Schema(description = "Current status of the contract", example = "ACTIVE")
    String status;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy")
    @Schema(description = "Creation timestamp", example = "28/11/2025", type = "string", pattern = "dd/MM/yyyy")
    LocalDateTime createdAt;

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Integer id;
        private Integer empId;
        private String employeeName;
        private String contractType;
        private LocalDate startDate;
        private LocalDate endDate;
        private String fileUrl;
        private String status;
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

        public Builder contractType(String contractType) {
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

        public Builder status(String status) {
            this.status = status;
            return this;
        }

        public Builder createdAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public ContractResponse build() {
            ContractResponse response = new ContractResponse();
            response.id = this.id;
            response.empId = this.empId;
            response.employeeName = this.employeeName;
            response.contractType = this.contractType;
            response.startDate = this.startDate;
            response.endDate = this.endDate;
            response.fileUrl = this.fileUrl;
            response.status = this.status;
            response.createdAt = this.createdAt;
            return response;
        }
    }
}
