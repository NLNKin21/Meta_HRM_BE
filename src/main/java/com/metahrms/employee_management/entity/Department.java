package com.metahrms.employee_management.entity;


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
