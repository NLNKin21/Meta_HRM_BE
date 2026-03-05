// package com.metahrms.employee_management.entity;

// import jakarta.persistence.*;
// import lombok.*;

// import java.math.BigDecimal;
// import java.util.ArrayList;
// import java.util.List;

// @Entity
// @Table(name = "positions")
// @Getter
// @Setter
// @NoArgsConstructor
// @AllArgsConstructor
// @Builder
// public class Position extends BaseEntity {

//     @Id
//     @GeneratedValue(strategy = GenerationType.IDENTITY)
//     private Long id;

//     @Column(name = "position_code", unique = true, nullable = false, length = 20)
//     private String positionCode;

//     @Column(name = "position_name", nullable = false, length = 100)
//     private String positionName;

//     @Column(columnDefinition = "TEXT")
//     private String description;

//     @Column(name = "min_salary", precision = 15, scale = 2)
//     private BigDecimal minSalary;

//     @Column(name = "max_salary", precision = 15, scale = 2)
//     private BigDecimal maxSalary;

//     @Column(name = "is_active", nullable = false)
//     private Boolean isActive = true;

//     @ManyToOne(fetch = FetchType.LAZY)
//     @JoinColumn(name = "department_id")
//     private Department department;

//     @OneToMany(mappedBy = "position", cascade = CascadeType.ALL)
//     private List<Employee> employees = new ArrayList<>();
// }