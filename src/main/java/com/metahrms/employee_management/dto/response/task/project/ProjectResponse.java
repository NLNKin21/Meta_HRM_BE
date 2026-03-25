package com.metahrms.employee_management.dto.response.task.project;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "Project information response")
public class ProjectResponse {

    @Schema(description = "Project ID", example = "1")
    Integer id;

    @Schema(description = "Project code", example = "PRJ-2024-001")
    String projectCode;

    @Schema(description = "Project name", example = "HRM System 2024")
    String projectName;

    @Schema(description = "Project description")
    String description;

    @Schema(description = "Department ID", example = "1")
    Integer departmentId;

    @Schema(description = "Department name", example = "IT Department")
    String departmentName;

    @Schema(description = "Manager ID", example = "2")
    Integer managerId;

    @Schema(description = "Manager name", example = "Nguyen Van A")
    String managerName;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy")
    @Schema(description = "Start date", example = "01/01/2024")
    LocalDate startDate;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy")
    @Schema(description = "End date", example = "31/12/2024")
    LocalDate endDate;

    @Schema(description = "Project status (PLANNING/ACTIVE/ON_HOLD/COMPLETED/CANCELLED)", example = "ACTIVE")
    String status;

    @Schema(description = "Is active?", example = "true")
    Boolean isActive;

    @Schema(description = "Total tasks in project", example = "25")
    Integer taskCount;

    @Schema(description = "Completed tasks count", example = "10")
    Integer completedTaskCount;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy HH:mm:ss")
    @Schema(description = "Creation timestamp")
    LocalDateTime createdAt;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy HH:mm:ss")
    @Schema(description = "Last update timestamp")
    LocalDateTime updatedAt;

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Integer id;
        private String projectCode;
        private String projectName;
        private String description;
        private Integer departmentId;
        private String departmentName;
        private Integer managerId;
        private String managerName;
        private LocalDate startDate;
        private LocalDate endDate;
        private String status;
        private Boolean isActive;
        private Integer taskCount;
        private Integer completedTaskCount;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;

        public Builder id(Integer id) { this.id = id; return this; }
        public Builder projectCode(String projectCode) { this.projectCode = projectCode; return this; }
        public Builder projectName(String projectName) { this.projectName = projectName; return this; }
        public Builder description(String description) { this.description = description; return this; }
        public Builder departmentId(Integer departmentId) { this.departmentId = departmentId; return this; }
        public Builder departmentName(String departmentName) { this.departmentName = departmentName; return this; }
        public Builder managerId(Integer managerId) { this.managerId = managerId; return this; }
        public Builder managerName(String managerName) { this.managerName = managerName; return this; }
        public Builder startDate(LocalDate startDate) { this.startDate = startDate; return this; }
        public Builder endDate(LocalDate endDate) { this.endDate = endDate; return this; }
        public Builder status(String status) { this.status = status; return this; }
        public Builder isActive(Boolean isActive) { this.isActive = isActive; return this; }
        public Builder taskCount(Integer taskCount) { this.taskCount = taskCount; return this; }
        public Builder completedTaskCount(Integer completedTaskCount) { this.completedTaskCount = completedTaskCount; return this; }
        public Builder createdAt(LocalDateTime createdAt) { this.createdAt = createdAt; return this; }
        public Builder updatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; return this; }

        public ProjectResponse build() {
            ProjectResponse response = new ProjectResponse();
            response.id = this.id;
            response.projectCode = this.projectCode;
            response.projectName = this.projectName;
            response.description = this.description;
            response.departmentId = this.departmentId;
            response.departmentName = this.departmentName;
            response.managerId = this.managerId;
            response.managerName = this.managerName;
            response.startDate = this.startDate;
            response.endDate = this.endDate;
            response.status = this.status;
            response.isActive = this.isActive;
            response.taskCount = this.taskCount;
            response.completedTaskCount = this.completedTaskCount;
            response.createdAt = this.createdAt;
            response.updatedAt = this.updatedAt;
            return response;
        }
    }
}