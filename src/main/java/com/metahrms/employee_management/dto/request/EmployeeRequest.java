package com.metahrms.employee_management.dto.request;

import com.metahrms.employee_management.enums.EmployeeStatus;
import com.metahrms.employee_management.enums.Gender;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmployeeRequest {

    @NotBlank(message = "Full name is required")
    @Size(min = 2, max = 100, message = "Full name must be between 2 and 100 characters")
    private String fullName;

    @NotNull(message = "Gender is required")
    private Gender gender;

    @Past(message = "Date of birth must be in the past")
    private LocalDate dateOfBirth;

    @Pattern(regexp = "^(\\+84|0)[0-9]{9,10}$", message = "Invalid phone number format")
    private String phoneNumber;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;

    @Email(message = "Invalid personal email format")
    private String personalEmail;

    @Size(max = 20, message = "ID card number must not exceed 20 characters")
    private String idCardNumber;

    @Size(max = 500, message = "Address must not exceed 500 characters")
    private String address;

    @Size(max = 500, message = "Permanent address must not exceed 500 characters")
    private String permanentAddress;

    @NotNull(message = "Hire date is required")
    @PastOrPresent(message = "Hire date cannot be in the future")
    private LocalDate hireDate;

    @DecimalMin(value = "0.0", inclusive = false, message = "Basic salary must be greater than 0")
    private BigDecimal basicSalary;

    private EmployeeStatus status;

    @NotNull(message = "Department is required")
    private Long departmentId;

    @NotNull(message = "Position is required")
    private Long positionId;

    private Long managerId;

    @Min(value = 0, message = "Annual leave days cannot be negative")
    @Max(value = 365, message = "Annual leave days cannot exceed 365")
    private Integer annualLeaveDays = 12;
}