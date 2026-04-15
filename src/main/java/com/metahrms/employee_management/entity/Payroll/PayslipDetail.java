package com.metahrms.employee_management.entity.Payroll;

import com.metahrms.employee_management.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "payslip_details")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = true)
public class PayslipDetail extends BaseEntity {

    @Column(name = "payslip_id", nullable = false)
    private Integer payslipId;

    @Column(name = "item_type", nullable = false, length = 20)
    private String itemType; // EARNING or DEDUCTION

    @Column(name = "item_code", nullable = false, length = 50)
    private String itemCode;

    @Column(name = "item_name", nullable = false, length = 200)
    private String itemName;

    @Column(name = "amount", nullable = false, precision = 12, scale = 2)
    private BigDecimal amount = BigDecimal.ZERO;

    @Column(name = "quantity", precision = 8, scale = 2)
    private BigDecimal quantity;

    @Column(name = "rate", precision = 15, scale = 4)
    private BigDecimal rate;

    @Column(name = "note", columnDefinition = "TEXT")
    private String note;

    @Column(name = "sort_order")
    private Integer sortOrder = 0;

    // Relationship
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "payslip_id", insertable = false, updatable = false)
    private Payslip payslip;
}
