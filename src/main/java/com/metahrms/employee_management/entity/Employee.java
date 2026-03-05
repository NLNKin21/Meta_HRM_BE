package com.metahrms.employee_management.entity;

import java.time.LocalDate;
import java.math.BigDecimal;

import com.metahrms.employee_management.enums.EmployeeStatus;
import com.metahrms.employee_management.enums.Gender;
import com.metahrms.employee_management.enums.RoleInDepartment;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Table(name = "employees")
@Data
@EqualsAndHashCode(callSuper = true)
public class Employee extends BaseEntity {

    @Column(name = "user_id")
    private Integer userId;

    @Column(name = "dept_id")
    private Integer deptId;

    @Column(name = "full_name", length = 150)
    private String fullName;

    @Column(length = 10)
    private Gender gender;

    @Column(name = "dob")
    private LocalDate dob;

    @Column(name = "phone_number", length = 20)
    private String phoneNumber;

    @Column(length = 255)
    private String address;

    @Column(name = "hire_date")
    private LocalDate hireDate;

    @Column(name = "basic_salary", precision = 12, scale = 2)
    private BigDecimal basicSalary;

    @Column(length = 20)
    private EmployeeStatus status = EmployeeStatus.ACTIVE;

    @Column(name = "role_in_dept", length = 20)
    private RoleInDepartment roleInDept = RoleInDepartment.STAFF;


    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Integer userId;
        private Integer deptId;
        private String fullName;
        private Gender gender;
        private LocalDate dob;
        private String phoneNumber;
        private String address;
        private LocalDate hireDate;
        private BigDecimal basicSalary;
        private EmployeeStatus status;
        private RoleInDepartment roleInDept;

        public Builder userId(Integer userId) {
            this.userId = userId;
            return this;
        }

        public Builder deptId(Integer deptId) {
            this.deptId = deptId;
            return this;
        }

        public Builder fullName(String fullName) {
            this.fullName = fullName;
            return this;
        }

        public Builder gender(Gender gender) {
            this.gender = gender;
            return this;
        }

        public Builder dob(LocalDate dob) {
            this.dob = dob;
            return this;
        }

        public Builder phoneNumber(String phoneNumber) {
            this.phoneNumber = phoneNumber;
            return this;
        }

        public Builder address(String address) {
            this.address = address;
            return this;
        }

        public Builder hireDate(LocalDate hireDate) {
            this.hireDate = hireDate;
            return this;
        }

        public Builder basicSalary(BigDecimal basicSalary) {
            this.basicSalary = basicSalary;
            return this;
        }

        public Builder status(EmployeeStatus status) {
            this.status = status;
            return this;
        }

        public Builder roleInDept(RoleInDepartment roleInDept) {
            this.roleInDept = roleInDept;
            return this;
        }

        public Employee build() {
            Employee employee = new Employee();
            employee.userId = this.userId;
            employee.deptId = this.deptId;
            employee.fullName = this.fullName;
            employee.gender = this.gender;
            employee.dob = this.dob;
            employee.phoneNumber = this.phoneNumber;
            employee.address = this.address;
            employee.hireDate = this.hireDate;
            employee.basicSalary = this.basicSalary;
            employee.status = this.status != null ? this.status : EmployeeStatus.ACTIVE;
            employee.roleInDept = this.roleInDept != null ? this.roleInDept : RoleInDepartment.STAFF;
            return employee;
        }
    }
}
