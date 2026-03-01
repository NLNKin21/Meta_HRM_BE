package com.metahrms.employee_management.dto.response;

import com.metahrms.employee_management.enums.EmployeeStatus;
import com.metahrms.employee_management.enums.Gender;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmployeeResponse {
    
    private Long id;
    private String employeeCode;
    private String fullName;
    private Gender gender;
    private LocalDate dateOfBirth;
    private String phoneNumber;
    private String email;
    private String personalEmail;
    private String idCardNumber;
    private String address;
    private String permanentAddress;
    private LocalDate hireDate;
    private LocalDate terminationDate;
    private BigDecimal basicSalary;
    private EmployeeStatus status;
    private String avatarUrl;
    private Integer annualLeaveDays;
    private Integer remainingLeaveDays;
    
    // Department info
    private Long departmentId;
    private String departmentName;
    private String departmentCode;
    
    // Position info
    private Long positionId;
    private String positionName;
    private String positionCode;
    
    // Manager info
    private Long managerId;
    private String managerName;
    
    // User info
    private Long userId;
    private String username;
    
    // Audit info
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}