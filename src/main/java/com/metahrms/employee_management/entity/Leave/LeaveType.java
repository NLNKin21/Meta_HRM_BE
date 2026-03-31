package com.metahrms.employee_management.entity.Leave;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "leave_types")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LeaveType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 100)
    private String code;

    @Column(nullable = false, length = 150)
    private String name;

    @Column(nullable = false)
    private Integer maxDaysPerYear;

    @Column(nullable = false)
    private Integer defaultDaysPerYear;

    @Column(nullable = false)
    private Boolean paidLeave;

    @Column(nullable = false)
    private Boolean requiresApproval;

    @Column(nullable = false)
    private Boolean requiresDocument;

    @Column(nullable = false)
    private Boolean active;

    @Column(nullable = false)
    private Boolean deductBalance;

    @Column(nullable = false)
    private Boolean deductFromAnnualLeaveBalance;

    @Column(nullable = false)
    private Boolean autoApprove;

    @Column(nullable = false)
    private Boolean allowCarryForward;

    @Column(nullable = false)
    private Boolean allowEncashment;

    @Column(nullable = false)
    private Boolean countsInAttendance;

    @Column(nullable = false)
    private Boolean countsInCompanyPayroll;

    @Column(nullable = false)
    private Boolean deductSalary;

    @Column(nullable = false)
    private Boolean socialInsurancePaid;

    @Column(nullable = false)
    private Boolean increaseBySeniority;

    @OneToMany(mappedBy = "leaveType", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("minYears ASC")
    @Builder.Default
    private List<LeaveTypeSeniorityRule> seniorityRules = new ArrayList<>();
}