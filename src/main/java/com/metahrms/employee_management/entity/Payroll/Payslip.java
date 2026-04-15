package com.metahrms.employee_management.entity.Payroll;

import com.metahrms.employee_management.entity.BaseEntity;
import com.metahrms.employee_management.entity.Employee;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "payslips",
       uniqueConstraints = @UniqueConstraint(columnNames = {"employee_id", "month", "year"}))
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = true)
public class Payslip extends BaseEntity {

    @Column(name = "employee_id", nullable = false)
    private Integer employeeId;

    @Column(name = "month", nullable = false)
    private Integer month;

    @Column(name = "year", nullable = false)
    private Integer year;

    @Column(name = "status", nullable = false, length = 20)
    private String status = "DRAFT";

    // ====== NGÀY CÔNG ======
    @Column(name = "standard_work_days")
    private Integer standardWorkDays = 22;

    @Column(name = "actual_work_days")
    private Integer actualWorkDays = 0;

    @Column(name = "paid_leave_days", precision = 4, scale = 1)
    private BigDecimal paidLeaveDays = BigDecimal.ZERO;

    @Column(name = "unpaid_leave_days", precision = 4, scale = 1)
    private BigDecimal unpaidLeaveDays = BigDecimal.ZERO;

    @Column(name = "absent_days")
    private Integer absentDays = 0;

    @Column(name = "overtime_hours_weekday", precision = 5, scale = 2)
    private BigDecimal overtimeHoursWeekday = BigDecimal.ZERO;

    @Column(name = "overtime_hours_weekend", precision = 5, scale = 2)
    private BigDecimal overtimeHoursWeekend = BigDecimal.ZERO;

    @Column(name = "overtime_hours_holiday", precision = 5, scale = 2)
    private BigDecimal overtimeHoursHoliday = BigDecimal.ZERO;

    @Column(name = "total_overtime_hours", precision = 5, scale = 2)
    private BigDecimal totalOvertimeHours = BigDecimal.ZERO;

    @Column(name = "total_late_times")
    private Integer totalLateTimes = 0;

    @Column(name = "total_late_minutes")
    private Integer totalLateMinutes = 0;

    // ====== THU NHẬP ======
    @Column(name = "basic_salary", precision = 12, scale = 2)
    private BigDecimal basicSalary = BigDecimal.ZERO;

    @Column(name = "actual_basic_salary", precision = 12, scale = 2)
    private BigDecimal actualBasicSalary = BigDecimal.ZERO;

    @Column(name = "total_allowances", precision = 12, scale = 2)
    private BigDecimal totalAllowances = BigDecimal.ZERO;

    @Column(name = "overtime_pay", precision = 12, scale = 2)
    private BigDecimal overtimePay = BigDecimal.ZERO;

    @Column(name = "total_bonus", precision = 12, scale = 2)
    private BigDecimal totalBonus = BigDecimal.ZERO;

    @Column(name = "gross_salary", precision = 12, scale = 2)
    private BigDecimal grossSalary = BigDecimal.ZERO;

    // ====== BẢO HIỂM ======
    @Column(name = "insurance_salary", precision = 12, scale = 2)
    private BigDecimal insuranceSalary = BigDecimal.ZERO;

    @Column(name = "social_insurance", precision = 12, scale = 2)
    private BigDecimal socialInsurance = BigDecimal.ZERO;

    @Column(name = "health_insurance", precision = 12, scale = 2)
    private BigDecimal healthInsurance = BigDecimal.ZERO;

    @Column(name = "unemployment_insurance", precision = 12, scale = 2)
    private BigDecimal unemploymentInsurance = BigDecimal.ZERO;

    @Column(name = "total_insurance", precision = 12, scale = 2)
    private BigDecimal totalInsurance = BigDecimal.ZERO;

    // ====== THUẾ ======
    @Column(name = "pre_tax_income", precision = 12, scale = 2)
    private BigDecimal preTaxIncome = BigDecimal.ZERO;

    @Column(name = "personal_deduction", precision = 12, scale = 2)
    private BigDecimal personalDeduction = BigDecimal.ZERO;

    @Column(name = "dependent_deduction", precision = 12, scale = 2)
    private BigDecimal dependentDeduction = BigDecimal.ZERO;

    @Column(name = "taxable_income", precision = 12, scale = 2)
    private BigDecimal taxableIncome = BigDecimal.ZERO;

    @Column(name = "personal_income_tax", precision = 12, scale = 2)
    private BigDecimal personalIncomeTax = BigDecimal.ZERO;

    // ====== KHẤU TRỪ ======
    @Column(name = "late_penalty", precision = 12, scale = 2)
    private BigDecimal latePenalty = BigDecimal.ZERO;

    @Column(name = "other_deductions", precision = 12, scale = 2)
    private BigDecimal otherDeductions = BigDecimal.ZERO;

    @Column(name = "total_deduction_amount", precision = 12, scale = 2)
    private BigDecimal totalDeductionAmount = BigDecimal.ZERO;

    // ====== KẾT QUẢ ======
    @Column(name = "net_salary", precision = 12, scale = 2)
    private BigDecimal netSalary = BigDecimal.ZERO;

    // ====== CHI PHÍ CÔNG TY ======
    @Column(name = "company_social_insurance", precision = 12, scale = 2)
    private BigDecimal companySocialInsurance = BigDecimal.ZERO;

    @Column(name = "company_health_insurance", precision = 12, scale = 2)
    private BigDecimal companyHealthInsurance = BigDecimal.ZERO;

    @Column(name = "company_unemployment", precision = 12, scale = 2)
    private BigDecimal companyUnemployment = BigDecimal.ZERO;

    @Column(name = "total_company_cost", precision = 12, scale = 2)
    private BigDecimal totalCompanyCost = BigDecimal.ZERO;

    // ====== META ======
    @Column(name = "note", columnDefinition = "TEXT")
    private String note;

    @Column(name = "calculated_by")
    private Integer calculatedBy;

    @Column(name = "calculated_at")
    private LocalDateTime calculatedAt;

    @Column(name = "approved_by")
    private Integer approvedBy;

    @Column(name = "approved_at")
    private LocalDateTime approvedAt;

    @Column(name = "rejected_by")
    private Integer rejectedBy;

    @Column(name = "rejected_at")
    private LocalDateTime rejectedAt;

    @Column(name = "reject_reason", columnDefinition = "TEXT")
    private String rejectReason;

    @Column(name = "paid_at")
    private LocalDateTime paidAt;

    @Column(name = "payment_method", length = 20)
    private String paymentMethod = "BANK_TRANSFER";

    // ====== RELATIONSHIPS ======
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id", insertable = false, updatable = false)
    private Employee employee;

    @OneToMany(mappedBy = "payslip", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("sortOrder ASC")
    @Builder.Default
    private List<PayslipDetail> details = new ArrayList<>();
}