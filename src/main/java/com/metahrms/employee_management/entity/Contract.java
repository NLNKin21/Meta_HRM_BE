package com.metahrms.employee_management.entity;

<<<<<<< HEAD
import com.metahrms.employee_management.enums.ContractStatus;
import com.metahrms.employee_management.enums.ContractType;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "contracts", indexes = {
    @Index(name = "idx_contract_employee", columnList = "employee_id"),
    @Index(name = "idx_contract_status", columnList = "status")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Contract extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "contract_number", unique = true, nullable = false, length = 50)
    private String contractNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id", nullable = false)
    private Employee employee;

    @Enumerated(EnumType.STRING)
    @Column(name = "contract_type", nullable = false, length = 20)
    private ContractType contractType;

    @Column(name = "start_date", nullable = false)
=======
import java.time.LocalDate;

import com.metahrms.employee_management.enums.ContractStatus;
import com.metahrms.employee_management.enums.ContractType;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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

    @Column(name = "contract_type", length = 50)
    private ContractType contractType;

    @Column(name = "start_date")
>>>>>>> 8c6be7d9d227d6c9e9aae8bb6ebeb4808ead22d3
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

<<<<<<< HEAD
    @Column(name = "signing_date")
    private LocalDate signingDate;

    @Column(precision = 15, scale = 2)
    private BigDecimal salary;

    @Column(name = "file_url")
    private String fileUrl;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ContractStatus status = ContractStatus.ACTIVE;

    @Column(columnDefinition = "TEXT")
    private String terms;

    @Column(columnDefinition = "TEXT")
    private String notes;
}
=======
    @Column(name = "file_url", length = 255)
    private String fileUrl;

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
>>>>>>> 8c6be7d9d227d6c9e9aae8bb6ebeb4808ead22d3
