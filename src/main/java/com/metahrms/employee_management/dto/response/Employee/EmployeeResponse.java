package com.metahrms.employee_management.dto.response.Employee;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.metahrms.employee_management.enums.EmployeeStatus;
import com.metahrms.employee_management.enums.Gender;
import com.metahrms.employee_management.enums.RoleInDepartment;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@Schema(description = "Employee information response")
public class EmployeeResponse {

    @Schema(description = "Employee ID", example = "1")
    Integer id;

    @Schema(description = "User ID liên kết với bảng users", example = "26")
    Integer userId;

    @Schema(description = "Department ID", example = "1")
    Integer deptId;

    @Schema(description = "Department name", example = "Human Resources")
    String deptName;

    @Schema(description = "Full name of the employee", example = "Nguyen Van A")
    String fullName;

    @Schema(description = "Email of the employee", example = "hr@gmail.com")
    String email;

    @Schema(description = "Gender", example = "MALE")
    Gender gender;

    @Schema(description = "Phone number", example = "0123456789")
    String phoneNumber;

    @Schema(description = "Residential address", example = "123 Main St, Hanoi")
    String address;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy")
    @Schema(description = "Date of birth", example = "15/05/1990", type = "string", pattern = "dd/MM/yyyy")
    LocalDate dob;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy")
    @Schema(description = "Hire date", example = "10/06/2020", type = "string", pattern = "dd/MM/yyyy")
    LocalDate hireDate;

    @Schema(description = "Role in department", example = "STAFF")
    RoleInDepartment roleInDept;

    @Schema(description = "Employment status", example = "ACTIVE")
    EmployeeStatus status;

    @Schema(description = "Associated username", example = "nguyenvana")
    String username;

    @Schema(description = "Basic salary", example = "100000")
    BigDecimal basicSalary;

    @Schema(description = "Position ID", example = "3")
    Integer positionId;

    @Schema(description = "Position name", example = "Nhân viên")
    String positionName;

    @Schema(description = "Manager ID / trưởng phòng dùng để duyệt nghỉ phép", example = "12")
    Integer managerId;

    @Schema(description = "Manager name / tên trưởng phòng", example = "Nguyen Van B")
    String managerName;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy")
    @Schema(description = "Creation timestamp", example = "28/11/2025", type = "string", pattern = "dd/MM/yyyy")
    LocalDateTime createdAt;

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Integer id;
        private Integer userId;
        private Integer deptId;
        private String deptName;
        private String fullName;
        private String email;
        private Gender gender;
        private String phoneNumber;
        private String address;
        private LocalDate dob;
        private LocalDate hireDate;
        private RoleInDepartment roleInDept;
        private EmployeeStatus status;
        private String username;
        private BigDecimal basicSalary;
        private Integer positionId;
        private String positionName;
        private Integer managerId;
        private String managerName;
        private LocalDateTime createdAt;

        public Builder id(Integer id) {
            this.id = id;
            return this;
        }

        public Builder userId(Integer userId) {
            this.userId = userId;
            return this;
        }

        public Builder deptId(Integer deptId) {
            this.deptId = deptId;
            return this;
        }

        public Builder deptName(String deptName) {
            this.deptName = deptName;
            return this;
        }

        public Builder fullName(String fullName) {
            this.fullName = fullName;
            return this;
        }

        public Builder email(String email) {
            this.email = email;
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

        public Builder address(String address) {
            this.address = address;
            return this;
        }

        public Builder dob(LocalDate dob) {
            this.dob = dob;
            return this;
        }

        public Builder hireDate(LocalDate hireDate) {
            this.hireDate = hireDate;
            return this;
        }

        public Builder roleInDept(RoleInDepartment roleInDept) {
            this.roleInDept = roleInDept;
            return this;
        }

        public Builder status(EmployeeStatus status) {
            this.status = status;
            return this;
        }

        public Builder username(String username) {
            this.username = username;
            return this;
        }

        public Builder basicSalary(BigDecimal basicSalary) {
            this.basicSalary = basicSalary;
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

        public Builder managerId(Integer managerId) {
            this.managerId = managerId;
            return this;
        }

        public Builder managerName(String managerName) {
            this.managerName = managerName;
            return this;
        }

        public Builder createdAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public EmployeeResponse build() {
            EmployeeResponse response = new EmployeeResponse();
            response.id = this.id;
            response.userId = this.userId;
            response.deptId = this.deptId;
            response.deptName = this.deptName;
            response.fullName = this.fullName;
            response.email = this.email;
            response.gender = this.gender;
            response.phoneNumber = this.phoneNumber;
            response.address = this.address;
            response.dob = this.dob;
            response.hireDate = this.hireDate;
            response.roleInDept = this.roleInDept;
            response.status = this.status;
            response.username = this.username;
            response.basicSalary = this.basicSalary;
            response.positionId = this.positionId;
            response.positionName = this.positionName;
            response.managerId = this.managerId;
            response.managerName = this.managerName;
            response.createdAt = this.createdAt;
            return response;
        }
    }
}