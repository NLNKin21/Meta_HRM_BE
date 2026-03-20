package com.metahrms.employee_management.dto.response.Employee;

import com.metahrms.employee_management.enums.RoleInDepartment;
import com.metahrms.employee_management.enums.Gender;
import com.metahrms.employee_management.enums.EmployeeStatus;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@Schema(description = "Employee summary for department view")
public class EmployeeSummaryDto {

    @Schema(description = "Employee ID", example = "1")
    Integer id;

    @Schema(description = "Full name of employee", example = "Nguyễn Văn A")
    String fullName;

    @Schema(description = "Gender", example = "MALE")
    Gender gender;

    @Schema(description = "Phone number", example = "0901234567")
    String phoneNumber;

    @Schema(description = "Hire date", example = "2023-01-15")
    LocalDate hireDate;

    @Schema(description = "Employee status", example = "ACTIVE")
    EmployeeStatus status;

    @Schema(description = "Position ID", example = "3")
    Integer positionId;

    @Schema(description = "Position name", example = "Trưởng phòng")
    String positionName;

    @Schema(description = "Position level (lower = higher rank)", example = "2")
    Integer positionLevel;

    @Schema(description = "Role in department", example = "HEAD")
    RoleInDepartment roleInDept;

    @Schema(description = "Is department manager", example = "true")
    Boolean isManager;

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Integer id;
        private String fullName;
        private Gender gender;
        private String phoneNumber;
        private LocalDate hireDate;
        private EmployeeStatus status;
        private Integer positionId;
        private String positionName;
        private Integer positionLevel;
        private RoleInDepartment roleInDept;
        private Boolean isManager;

        public Builder id(Integer id) {
            this.id = id;
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

        public Builder phoneNumber(String phoneNumber) {
            this.phoneNumber = phoneNumber;
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

        public Builder positionId(Integer positionId) {
            this.positionId = positionId;
            return this;
        }

        public Builder positionName(String positionName) {
            this.positionName = positionName;
            return this;
        }

        public Builder positionLevel(Integer positionLevel) {
            this.positionLevel = positionLevel;
            return this;
        }

        public Builder roleInDept(RoleInDepartment roleInDept) {
            this.roleInDept = roleInDept;
            return this;
        }

        public Builder isManager(Boolean isManager) {
            this.isManager = isManager;
            return this;
        }

        public EmployeeSummaryDto build() {
            EmployeeSummaryDto dto = new EmployeeSummaryDto();
            dto.id = this.id;
            dto.fullName = this.fullName;
            dto.gender = this.gender;
            dto.phoneNumber = this.phoneNumber;
            dto.hireDate = this.hireDate;
            dto.status = this.status;
            dto.positionId = this.positionId;
            dto.positionName = this.positionName;
            dto.positionLevel = this.positionLevel;
            dto.roleInDept = this.roleInDept;
            dto.isManager = this.isManager;
            return dto;
        }
    }
}