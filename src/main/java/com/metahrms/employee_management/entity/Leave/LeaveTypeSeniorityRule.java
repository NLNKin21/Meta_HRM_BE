package com.metahrms.employee_management.entity.Leave;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(
    name = "leave_type_seniority_rules",
    uniqueConstraints = {
        @UniqueConstraint(columnNames = {"leave_type_id", "min_years"})
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LeaveTypeSeniorityRule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "leave_type_id", nullable = false)
    private LeaveType leaveType;

    @Column(name = "min_years", nullable = false)
    private Integer minYears;

    @Column(name = "extra_days", nullable = false)
    private Integer extraDays;
}