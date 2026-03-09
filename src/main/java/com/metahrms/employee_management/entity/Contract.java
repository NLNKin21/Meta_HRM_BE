package com.metahrms.employee_management.entity;

import java.time.LocalDate;

import com.metahrms.employee_management.enums.ContractStatus;
import com.metahrms.employee_management.enums.ContractType;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Table(name = "contracts")
@Data
@EqualsAndHashCode(callSuper = true)
public class Contract extends BaseEntity {

    @Column(name = "emp_id")
    private Integer empId;

    @Enumerated(EnumType.STRING)
    @Column(name = "contract_type", length = 50)
    private ContractType contractType;

    @Column(name = "start_date")
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    @Column(name = "file_url", length = 255)
    private String fileUrl;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private ContractStatus status = ContractStatus.ACTIVE;

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

        public Contract build() {
            Contract contract = new Contract();
            contract.empId = this.empId;
            contract.contractType = this.contractType;
            contract.startDate = this.startDate;
            contract.endDate = this.endDate;
            contract.fileUrl = this.fileUrl;
            contract.status = this.status != null ? this.status : ContractStatus.ACTIVE;
            return contract;
        }
    }
}
