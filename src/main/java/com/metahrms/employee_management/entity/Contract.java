package com.metahrms.employee_management.entity;

import java.time.LocalDate;

import com.metahrms.employee_management.enums.ContractStatus;
import com.metahrms.employee_management.enums.ContractType;

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
@Table(name = "contracts")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class Contract extends BaseEntity {

    @Column(name = "emp_id", nullable = false)
    private Integer empId;

    @Enumerated(EnumType.STRING)
    @Column(name = "contract_type", nullable = false, length = 50)
    private ContractType contractType;

    @Column(name = "start_date")
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    @Column(name = "file_url", length = 500)  // ✅ Tăng lên 500 (Cloudinary URL dài)
    private String fileUrl;

    @Column(name = "file_key", length = 255)  // ✅ Thêm field lưu Cloudinary public_id
    private String fileKey;

    @Column(name = "preview_url")
    private String previewUrl;

    @Column(name = "file_format")
    private String fileFormat;

    @Column(name = "previewable")
    private Boolean previewable;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private ContractStatus status;

    // ✅ Custom Builder (giữ nguyên logic của bạn)
    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Integer empId;
        private ContractType contractType;
        private LocalDate startDate;
        private LocalDate endDate;
        private String fileUrl;
        private String fileKey;  // ✅ Thêm vào builder
        private String previewUrl;
        private String fileFormat;
        private Boolean previewable;
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

        public Builder fileKey(String fileKey) {  // ✅ Thêm method
            this.fileKey = fileKey;
            return this;
        }

        public Builder previewUrl(String previewUrl) {
            this.previewUrl = previewUrl;
            return this;
        }
        public Builder previewable(Boolean previewable) {
            this.previewable = previewable;
            return this;
        }
        public Builder fileFormat(String fileFormat) {
            this.fileFormat = fileFormat;
            return this;
        }


        public Builder status(ContractStatus status) {
            this.status = status;
            return this;
        }

        public Contract build() {
            Contract contract = new Contract();
            contract.empId = this.empId;
            contract.contractType = this.contractType;
            contract.startDate = this.startDate;
            contract.endDate = this.endDate;
            contract.fileUrl = this.fileUrl;
            contract.fileKey = this.fileKey;  // ✅ Set fileKey
            contract.previewUrl = this.previewUrl;
            contract.fileFormat = this.fileFormat;
            contract.previewable = this.previewable;
            contract.status = this.status != null ? this.status : ContractStatus.ACTIVE;
            return contract;
        }
    }
}