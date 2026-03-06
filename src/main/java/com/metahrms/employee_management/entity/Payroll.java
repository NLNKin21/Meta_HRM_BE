<<<<<<< HEAD
package com.metahrms.employee_management.entity;

import com.metahrms.employee_management.enums.PayrollStatus;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "payrolls", indexes = {
    @Index(name = "idx_payroll_period", columnList = "month, year"),
    @Index(name = "idx_payroll_employee", columnList = "employee_id")
}, uniqueConstraints = {
    @UniqueConstraint(columnNames = {"employee_id", "month", "year"})
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Payroll extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id", nullable = false)
    private Employee employee;

    @Column(nullable = false)
    private Integer month;

    @Column(nullable = false)
    private Integer year;

    @Column(name = "working_days")
    private Integer workingDays;

    @Column(name = "actual_working_days")
    private Integer actualWorkingDays;

    @Column(name = "basic_salary", precision = 15, scale = 2, nullable = false)
    private BigDecimal basicSalary;

    @Column(precision = 15, scale = 2)
    private BigDecimal allowance = BigDecimal.ZERO;

    @Column(precision = 15, scale = 2)
    private BigDecimal bonus = BigDecimal.ZERO;

    @Column(name = "overtime_pay", precision = 15, scale = 2)
    private BigDecimal overtimePay = BigDecimal.ZERO;

    @Column(precision = 15, scale = 2)
    private BigDecimal deduction = BigDecimal.ZERO;

    @Column(name = "insurance_deduction", precision = 15, scale = 2)
    private BigDecimal insuranceDeduction = BigDecimal.ZERO;

    @Column(name = "tax_deduction", precision = 15, scale = 2)
    private BigDecimal taxDeduction = BigDecimal.ZERO;

    @Column(name = "net_salary", precision = 15, scale = 2)
    private BigDecimal netSalary;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private PayrollStatus status = PayrollStatus.DRAFT;

    @Column(name = "paid_date")
    private LocalDate paidDate;

    @Column(name = "payment_method", length = 50)
    private String paymentMethod;

    @Column(columnDefinition = "TEXT")
    private String notes;

    // ==================== BUSINESS METHODS ====================
    
    @PrePersist
    @PreUpdate
    public void calculateNetSalary() {
        BigDecimal totalEarnings = this.basicSalary
            .add(this.allowance != null ? this.allowance : BigDecimal.ZERO)
            .add(this.bonus != null ? this.bonus : BigDecimal.ZERO)
            .add(this.overtimePay != null ? this.overtimePay : BigDecimal.ZERO);
        
        BigDecimal totalDeductions = BigDecimal.ZERO
            .add(this.deduction != null ? this.deduction : BigDecimal.ZERO)
            .add(this.insuranceDeduction != null ? this.insuranceDeduction : BigDecimal.ZERO)
            .add(this.taxDeduction != null ? this.taxDeduction : BigDecimal.ZERO);
        
        this.netSalary = totalEarnings.subtract(totalDeductions);
    }
}
=======
// package com.metahrms.employee_management.entity;

// import com.metahrms.employee_management.enums.PayrollStatus;
// import jakarta.persistence.*;
// import lombok.*;

// import java.math.BigDecimal;
// import java.time.LocalDate;

// @Entity
// @Table(name = "payrolls", indexes = {
//     @Index(name = "idx_payroll_period", columnList = "month, year"),
//     @Index(name = "idx_payroll_employee", columnList = "employee_id")
// }, uniqueConstraints = {
//     @UniqueConstraint(columnNames = {"employee_id", "month", "year"})
// })
// @Getter
// @Setter
// @NoArgsConstructor
// @AllArgsConstructor
// @Builder
// public class Payroll extends BaseEntity {

//     @Id
//     @GeneratedValue(strategy = GenerationType.IDENTITY)
//     private Long id;

//     @ManyToOne(fetch = FetchType.LAZY)
//     @JoinColumn(name = "employee_id", nullable = false)
//     private Employee employee;

//     @Column(nullable = false)
//     private Integer month;

//     @Column(nullable = false)
//     private Integer year;

//     @Column(name = "working_days")
//     private Integer workingDays;

//     @Column(name = "actual_working_days")
//     private Integer actualWorkingDays;

//     @Column(name = "basic_salary", precision = 15, scale = 2, nullable = false)
//     private BigDecimal basicSalary;

//     @Column(precision = 15, scale = 2)
//     private BigDecimal allowance = BigDecimal.ZERO;

//     @Column(precision = 15, scale = 2)
//     private BigDecimal bonus = BigDecimal.ZERO;

//     @Column(name = "overtime_pay", precision = 15, scale = 2)
//     private BigDecimal overtimePay = BigDecimal.ZERO;

//     @Column(precision = 15, scale = 2)
//     private BigDecimal deduction = BigDecimal.ZERO;

//     @Column(name = "insurance_deduction", precision = 15, scale = 2)
//     private BigDecimal insuranceDeduction = BigDecimal.ZERO;

//     @Column(name = "tax_deduction", precision = 15, scale = 2)
//     private BigDecimal taxDeduction = BigDecimal.ZERO;

//     @Column(name = "net_salary", precision = 15, scale = 2)
//     private BigDecimal netSalary;

//     @Enumerated(EnumType.STRING)
//     @Column(nullable = false, length = 20)
//     private PayrollStatus status = PayrollStatus.DRAFT;

//     @Column(name = "paid_date")
//     private LocalDate paidDate;

//     @Column(name = "payment_method", length = 50)
//     private String paymentMethod;

//     @Column(columnDefinition = "TEXT")
//     private String notes;

//     // ==================== BUSINESS METHODS ====================
    
//     @PrePersist
//     @PreUpdate
//     public void calculateNetSalary() {
//         BigDecimal totalEarnings = this.basicSalary
//             .add(this.allowance != null ? this.allowance : BigDecimal.ZERO)
//             .add(this.bonus != null ? this.bonus : BigDecimal.ZERO)
//             .add(this.overtimePay != null ? this.overtimePay : BigDecimal.ZERO);
        
//         BigDecimal totalDeductions = BigDecimal.ZERO
//             .add(this.deduction != null ? this.deduction : BigDecimal.ZERO)
//             .add(this.insuranceDeduction != null ? this.insuranceDeduction : BigDecimal.ZERO)
//             .add(this.taxDeduction != null ? this.taxDeduction : BigDecimal.ZERO);
        
//         this.netSalary = totalEarnings.subtract(totalDeductions);
//     }
// }
>>>>>>> 8c6be7d9d227d6c9e9aae8bb6ebeb4808ead22d3
