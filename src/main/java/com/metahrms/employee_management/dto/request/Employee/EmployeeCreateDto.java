package com.metahrms.employee_management.dto.request.Employee;

import java.time.LocalDate;
import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.metahrms.employee_management.enums.EmployeeStatus;
import com.metahrms.employee_management.enums.Gender;
import com.metahrms.employee_management.enums.RoleInDepartment;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Schema(description = "Data transfer object for creating a new employee")
public class EmployeeCreateDto {
    @NotNull(message = "User ID is required")
    @Schema(description = "ID of the user account associated with this employee", example = "1", required = true)
    Integer userId;

    @NotNull(message = "Department ID is required")
    @Schema(description = "ID of the department this employee belongs to", example = "1", required = true)
    Integer deptId;

    @Schema(description = "ID of the position for this employee", example = "3")
    Integer positionId; // optional

    @NotBlank(message = "Full name is required")
    @Schema(description = "Full name of the employee", example = "Nguyen Van A", required = true)
    String fullName;

    @NotNull(message = "Gender is required")
    @Schema(description = "Gender of the employee (MALE/FEMALE)", example = "MALE", required = true)
    Gender gender;

    @NotNull(message = "Date of birth is required")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy")
    @Schema(description = "Date of birth", example = "28/11/2000", type = "string", pattern = "dd/MM/yyyy", required = true)
    LocalDate dob;

    @Pattern(regexp = "^[0-9]{10,20}$", message = "Phone number must be between 10 and 20 digits")
    @Schema(description = "Phone number (10-20 digits)", example = "0123456789")
    String phoneNumber;

    @Schema(description = "Residential address", example = "123 Main St, Hanoi")
    String address;

    @NotNull(message = "Hire date is required")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy")
    @Schema(description = "Hire date", example = "01/01/2025", type = "string", pattern = "dd/MM/yyyy", required = true)
    LocalDate hireDate;

    @Schema(description = "Employment status (ACTIVE/ON_LEAVE/TERMINATED). Defaults to ACTIVE if not provided", example = "ACTIVE")
    EmployeeStatus status;

    @Schema(description = "Role in department (HEAD/STAFF). Defaults to STAFF if not provided", example = "STAFF")
    RoleInDepartment roleInDept;

    @NotNull(message = "Basic salary is required")
    @Schema(description = "Basic monthly salary", example = "15000000", required = true)
    BigDecimal basicSalary;

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Integer userId;
        private Integer deptId;
        private Integer positionId;
        private String fullName;
        private Gender gender;
        private LocalDate dob;
        private String phoneNumber;
        private String address;
        private LocalDate hireDate;
        private EmployeeStatus status;
        private RoleInDepartment roleInDept;
        private BigDecimal basicSalary;

        public Builder userId(Integer userId) {
            this.userId = userId;
            return this;
        }

        public Builder deptId(Integer deptId) {
            this.deptId = deptId;
            return this;
        }

        public Builder positionId(Integer positionId) {
            this.positionId = positionId;
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

        public Builder status(EmployeeStatus status) {
            this.status = status;
            return this;
        }

        public Builder roleInDept(RoleInDepartment roleInDept) {
            this.roleInDept = roleInDept;
            return this;
        }

        public Builder basicSalary(BigDecimal basicSalary) {
            this.basicSalary = basicSalary;
            return this;
        }

        public EmployeeCreateDto build() {
            EmployeeCreateDto dto = new EmployeeCreateDto();
            dto.userId = this.userId;
            dto.deptId = this.deptId;
            dto.positionId = this.positionId;
            dto.fullName = this.fullName;
            dto.gender = this.gender;
            dto.dob = this.dob;
            dto.phoneNumber = this.phoneNumber;
            dto.address = this.address;
            dto.hireDate = this.hireDate;
            dto.status = this.status;
            dto.roleInDept = this.roleInDept;
            dto.basicSalary = this.basicSalary;
            return dto;
        }
    }
}
