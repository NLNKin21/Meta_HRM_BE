package com.metahrms.employee_management.dto.request.Department;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Schema(description = "Data Transfer Object for creating or updating a department")
public class DepartmentDto {
    @Schema(description = "Name of the department", example = "Phòng Nhân Sự", required = true)
    @NotBlank(message = "Department name is required")
    String deptName;

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String deptName;
        public Builder deptName(String deptName) {
            this.deptName = deptName;
            return this;
        }

        public DepartmentDto build() {
            DepartmentDto dto = new DepartmentDto();
            dto.deptName = this.deptName;
            return dto;
        }
    }
}
