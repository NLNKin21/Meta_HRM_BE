package com.metahrms.employee_management.dto.response.Employee;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;

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

    @Schema(description = "Full name of the employee", example = "Nguyen Van A")
    String fullName;

    @Schema(description = "Email of the employee", example = "hr@gmail.com")
    String email;

    @Schema(description = "Gender (MALE/FEMALE)", example = "MALE")
    String gender;

    @Schema(description = "Phone number", example = "0123456789")
    String phoneNumber;

    @Schema(description = "Department name", example = "Human Resources")
    String department;

    @Schema(description = "Department ID", example = "1")
    Integer deptId;

    @Schema(description = "Residential address", example = "123 Main St, Hanoi")
    String address;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy")
    @Schema(description = "Date of birth", example = "15/05/1990", type = "string", pattern = "dd/MM/yyyy")
    LocalDate dob;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy")
    @Schema(description = "Hire date", example = "10/06/2020", type = "string", pattern = "dd/MM/yyyy")
    LocalDate hireDate;

    @Schema(description = "Role in department (HEAD/STAFF)", example = "STAFF")
    String roleInDept;

    @Schema(description = "Employment status (ACTIVE/ON_LEAVE/TERMINATED)", example = "ACTIVE")
    String status;

    @Schema(description = "Associated username", example = "nguyenvana")
    String username;

    @Schema(description = "Basic salary", example = "100000")
    BigDecimal basicSalary;

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
        private String fullName;
        private String email;
        private String gender;
        private String phoneNumber;
        private String department;
        private Integer deptId;
        private String address;
        private LocalDate dob;
        private LocalDate hireDate;
        private String roleInDept;
        private String status;
        private String username;
        private BigDecimal basicSalary;
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

        public Builder fullName(String fullName) {
            this.fullName = fullName;
            return this;
        }

        public Builder email(String email) {
            this.email = email;
            return this;
        }

        public Builder gender(String gender) {
            this.gender = gender;
            return this;
        }

        public Builder phoneNumber(String phoneNumber) {
            this.phoneNumber = phoneNumber;
            return this;
        }

        public Builder department(String department) {
            this.department = department;
            return this;
        }

        public Builder deptId(Integer deptId) {
            this.deptId = deptId;
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

        public Builder roleInDept(String roleInDept) {
            this.roleInDept = roleInDept;
            return this;
        }

        public Builder status(String status) {
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
            response.fullName = this.fullName;
            response.email = this.email;
            response.gender = this.gender;
            response.phoneNumber = this.phoneNumber;
            response.department = this.department;
            response.deptId = this.deptId;
            response.address = this.address;
            response.dob = this.dob;
            response.hireDate = this.hireDate;
            response.roleInDept = this.roleInDept;
            response.status = this.status;
            response.username = this.username;
            response.basicSalary = this.basicSalary;
            response.managerId = this.managerId;
            response.managerName = this.managerName;
            response.createdAt = this.createdAt;
            return response;
        }
    }
}