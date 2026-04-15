package com.metahrms.employee_management.entity.Payroll;

import com.metahrms.employee_management.entity.BaseEntity;
import com.metahrms.employee_management.entity.Employee;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "employee_tax_info")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = true)
public class EmployeeTaxInfo extends BaseEntity {

    @Column(name = "employee_id", nullable = false, unique = true)
    private Integer employeeId;

    @Column(name = "tax_code", length = 20)
    private String taxCode;

    @Column(name = "number_of_dependents")
    private Integer numberOfDependents = 0;

    @Column(name = "social_insurance_no", length = 30)
    private String socialInsuranceNo;

    @Column(name = "social_insurance_salary", precision = 12, scale = 2)
    private BigDecimal socialInsuranceSalary;

    // Ngân hàng
    @Column(name = "bank_name", length = 100)
    private String bankName;

    @Column(name = "bank_branch", length = 200)
    private String bankBranch;

    @Column(name = "bank_account_number", length = 30)
    private String bankAccountNumber;

    @Column(name = "bank_account_holder", length = 150)
    private String bankAccountHolder;

    @Column(name = "note", columnDefinition = "TEXT")
    private String note;

    @Column(name = "updated_by")
    private Integer updatedBy;

    // Relationship
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id", insertable = false, updatable = false)
    private Employee employee;
}