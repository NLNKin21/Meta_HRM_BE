package com.metahrms.employee_management.dto.response.Department;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@Schema(description = "Response object containing department details")
public class DepartmentResponse {
    @Schema(description = "Unique identifier of the department", example = "1")
    Integer id;

    @Schema(description = "Name of the department", example = "Phòng Nhân Sự")
    String deptName;

    @Schema(description = "Department creation timestamp", example = "10/11/2025")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy")
    LocalDateTime createdAt;

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Integer id;
        private String deptName;
        private LocalDateTime createdAt;

        public Builder id(Integer id) {
            this.id = id;
            return this;
        }

        public Builder deptName(String deptName) {
            this.deptName = deptName;
            return this;
        }

        public Builder createdAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public DepartmentResponse build() {
            DepartmentResponse response = new DepartmentResponse();
            response.id = this.id;
            response.deptName = this.deptName;
            response.createdAt = this.createdAt;
            return response;
        }
    }
}