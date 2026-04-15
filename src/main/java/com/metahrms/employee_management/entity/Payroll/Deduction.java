package com.metahrms.employee_management.entity.Payroll;

import com.metahrms.employee_management.entity.BaseEntity;
import com.metahrms.employee_management.entity.Employee;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "deductions")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = true)
public class Deduction extends BaseEntity {

    @Column(name = "employee_id", nullable = false)
    private Integer employeeId;

    @Column(name = "deduction_type", nullable = false, length = 30)
    private String deductionType;

    @Column(name = "name", nullable = false, length = 200)
    private String name;

    @Column(name = "amount", nullable = false, precision = 12, scale = 2)
    private BigDecimal amount;

    @Column(name = "month", nullable = false)
    private Integer month;

    @Column(name = "year", nullable = false)
    private Integer year;

    @Column(name = "reason", columnDefinition = "TEXT")
    private String reason;

    @Column(name = "is_approved")
    private Boolean isApproved = false;

    @Column(name = "approved_by")
    private Integer approvedBy;

    @Column(name = "created_by")
    private Integer createdBy;

    // Relationship
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id", insertable = false, updatable = false)
    private Employee employee;
}