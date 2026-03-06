package com.metahrms.employee_management.entity;

<<<<<<< HEAD
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.metahrms.employee_management.enums.EmployeeStatus;
import com.metahrms.employee_management.enums.Gender;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "employees", indexes = {
        @Index(name = "idx_employee_code", columnList = "employee_code"),
        @Index(name = "idx_employee_email", columnList = "email"),
        @Index(name = "idx_employee_status", columnList = "status")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Employee extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "employee_code", unique = true, nullable = false, length = 20)
    private String employeeCode;

    @Column(name = "full_name", nullable = false, length = 100)
    private String fullName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private Gender gender;

    @Column(name = "date_of_birth")
    private LocalDate dateOfBirth;

    @Column(name = "phone_number", length = 15)
    private String phoneNumber;

    @Column(unique = true, nullable = false, length = 100)
    private String email;

    @Column(name = "personal_email", length = 100)
    private String personalEmail;

    @Column(name = "id_card_number", length = 20)
    private String idCardNumber;

    @Column(columnDefinition = "TEXT")
    private String address;

    @Column(name = "permanent_address", columnDefinition = "TEXT")
    private String permanentAddress;

    @Column(name = "hire_date", nullable = false)
    private LocalDate hireDate;

    @Column(name = "termination_date")
    private LocalDate terminationDate;

    @Column(name = "basic_salary", precision = 15, scale = 2)
    private BigDecimal basicSalary;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private EmployeeStatus status = EmployeeStatus.ACTIVE;

    @Column(name = "avatar_url")
    private String avatarUrl;

    @Column(name = "annual_leave_days")
    private Integer annualLeaveDays = 12;

    @Column(name = "remaining_leave_days")
    private Integer remainingLeaveDays = 12;

    // ==================== RELATIONSHIPS ====================

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "department_id")
    private Department department;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "position_id")
    private Position position;

    // Tránh vòng lặp manager <-> subordinates
    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "manager_id")
    private Employee manager;

    @JsonIgnore
    @OneToMany(mappedBy = "manager", cascade = CascadeType.ALL)
    private List<Employee> subordinates = new ArrayList<>();

    @JsonIgnore
    @OneToOne(mappedBy = "employee", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private User user;

    @JsonIgnore
    @OneToMany(mappedBy = "employee", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Attendance> attendances = new ArrayList<>();

    @JsonIgnore
    @OneToMany(mappedBy = "employee", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Payroll> payrolls = new ArrayList<>();

    @JsonIgnore
    @OneToMany(mappedBy = "employee", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<LeaveRequest> leaveRequests = new ArrayList<>();

    @JsonIgnore
    @OneToMany(mappedBy = "employee", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Contract> contracts = new ArrayList<>();
}
=======
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
>>>>>>> 8c6be7d9d227d6c9e9aae8bb6ebeb4808ead22d3
