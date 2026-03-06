package com.metahrms.employee_management.entity;

<<<<<<< HEAD
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "departments")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Department extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "dept_code", unique = true, nullable = false, length = 20)
    private String deptCode;

    @Column(name = "dept_name", nullable = false, length = 100)
    private String deptName;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "manager_id")
    private Employee manager;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private Department parent;

    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL)
    private List<Department> children = new ArrayList<>();

    @OneToMany(mappedBy = "department", cascade = CascadeType.ALL)
    private List<Employee> employees = new ArrayList<>();
}
=======

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Table(name = "departments")
@Data
@EqualsAndHashCode(callSuper = true)
public class Department extends BaseEntity {

    @Column(name = "dept_name", length = 100)
    private String deptName;


    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
    private String deptName;
        public Builder deptName(String deptName) {
            this.deptName = deptName;
            return this;
        }

        public Department build() {
            Department department = new Department();
            department.deptName = this.deptName;
            // manager is not stored on Department; derive from Employee.roleInDept == HEAD
            return department;
        }
    }
}
>>>>>>> 8c6be7d9d227d6c9e9aae8bb6ebeb4808ead22d3
