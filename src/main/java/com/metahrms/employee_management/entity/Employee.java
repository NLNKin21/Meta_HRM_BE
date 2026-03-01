package com.metahrms.employee_management.entity;

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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "manager_id")
    private Employee manager;

    @OneToOne(mappedBy = "employee", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private User user;

    @OneToMany(mappedBy = "employee", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Attendance> attendances = new ArrayList<>();

    @OneToMany(mappedBy = "employee", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Payroll> payrolls = new ArrayList<>();

    @OneToMany(mappedBy = "employee", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<LeaveRequest> leaveRequests = new ArrayList<>();

    @OneToMany(mappedBy = "employee", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Contract> contracts = new ArrayList<>();

    @OneToMany(mappedBy = "manager", cascade = CascadeType.ALL)
    private List<Employee> subordinates = new ArrayList<>();
}