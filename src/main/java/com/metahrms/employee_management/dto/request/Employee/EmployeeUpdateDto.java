package com.metahrms.employee_management.dto.request.Employee;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.metahrms.employee_management.enums.EmployeeStatus;
import com.metahrms.employee_management.enums.Gender;
import com.metahrms.employee_management.enums.RoleInDepartment;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Pattern;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Schema(description = "Data transfer object for updating employee information. All fields are optional for partial updates.")
public class EmployeeUpdateDto {

    @Schema(description = "Department ID", example = "1")
    Integer deptId;

    @Schema(description = "Employee's full name", example = "John Doe")
    String fullName;

    @Schema(description = "Employee's gender", example = "MALE")
    Gender gender;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy")
    @Schema(
        description = "Date of birth",
        example = "15/05/1990",
        type = "string",
        pattern = "dd/MM/yyyy"
    )
    LocalDate dob;

    @Pattern(regexp = "^[0-9]{10,20}$", message = "Phone number must be between 10 and 20 digits")
    @Schema(description = "Phone number (10-20 digits)", example = "0123456789")
    String phoneNumber;

    @Schema(description = "Employee's address", example = "123 Main St, Hanoi, Vietnam")
    String address;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy")
    @Schema(
        description = "Hire date",
        example = "10/06/2020",
        type = "string",
        pattern = "dd/MM/yyyy"
    )
    LocalDate hireDate;

    @Schema(description = "Employee status", example = "ACTIVE")
    EmployeeStatus status;

    @Schema(description = "Role in department", example = "STAFF")
    RoleInDepartment roleInDept;

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Integer deptId;
        private String fullName;
        private Gender gender;
        private LocalDate dob;
        private String phoneNumber;
        private String address;
        private LocalDate hireDate;
        private EmployeeStatus status;
        private RoleInDepartment roleInDept;

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

        public Builder status(EmployeeStatus status) {
            this.status = status;
            return this;
        }

        public Builder roleInDept(RoleInDepartment roleInDept) {
            this.roleInDept = roleInDept;
            return this;
        }

        public EmployeeUpdateDto build() {
            EmployeeUpdateDto dto = new EmployeeUpdateDto();
            dto.deptId = this.deptId;
            dto.fullName = this.fullName;
            dto.gender = this.gender;
            dto.dob = this.dob;
            dto.phoneNumber = this.phoneNumber;
            dto.address = this.address;
            dto.hireDate = this.hireDate;
            dto.status = this.status;
            dto.roleInDept = this.roleInDept;
            return dto;
        }
    }
}
