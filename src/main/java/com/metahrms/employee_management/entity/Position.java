package com.metahrms.employee_management.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "positions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Position extends BaseEntity {

    @Column(name = "position_code", unique = true, nullable = false, length = 20)
    private String positionCode;

    @Column(name = "position_name", nullable = false, length = 100)
    private String positionName;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "min_salary", precision = 15, scale = 2)
    private BigDecimal minSalary;

    @Column(name = "max_salary", precision = 15, scale = 2)
    private BigDecimal maxSalary;

    @Column(name = "is_active", nullable = false)
    @Builder.Default
    private Boolean isActive = true;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_position_id")
    private Position parentPosition;

    @OneToMany(mappedBy = "parentPosition", cascade = CascadeType.ALL)
    @Builder.Default
    private List<Position> childPositions = new ArrayList<>();

    @Column(name = "level_order")
    @Builder.Default
    private Integer levelOrder = 6;

    @Column(name = "sort_order")
    @Builder.Default
    private Integer sortOrder = 1;
     
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "department_id")
    private Department department;

    @OneToMany(mappedBy = "position", cascade = CascadeType.ALL)
    @Builder.Default
    private List<Employee> employees = new ArrayList<>();
}