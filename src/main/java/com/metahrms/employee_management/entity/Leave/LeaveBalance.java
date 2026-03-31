package com.metahrms.employee_management.entity.Leave;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(
    name = "leave_balances",
    uniqueConstraints = @UniqueConstraint(columnNames = {"employee_id", "leave_type_id", "year"})
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LeaveBalance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "employee_id", nullable = false)
    private Integer employeeId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "leave_type_id", nullable = false)
    private LeaveType leaveType;

    @Column(nullable = false)
    private Integer year;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal allocatedDays;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal usedDays;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal pendingDays;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal carryForwardDays;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal encashedDays;
}