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

    // ✅ Thêm field isActive
    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String deptName;
        private Boolean isActive = true;

        public Builder deptName(String deptName) {
            this.deptName = deptName;
            return this;
        }

        public Builder isActive(Boolean isActive) {
            this.isActive = isActive;
            return this;
        }

        public Department build() {
            Department department = new Department();
            department.deptName = this.deptName;
            department.isActive = this.isActive;
            return department;
        }
    }
}