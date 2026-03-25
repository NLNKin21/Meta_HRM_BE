package com.metahrms.employee_management.dto.request.task.project;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@Schema(description = "Request to update a project")
public class ProjectUpdateRequest {

    @Size(max = 200, message = "Project name must not exceed 200 characters")
    @Schema(description = "Project name", example = "HRM System 2024")
    String projectName;

    @Schema(description = "Project description")
    String description;

    @Schema(description = "Manager employee ID", example = "2")
    Integer managerId;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy")
    @Schema(description = "Start date", example = "01/01/2024")
    LocalDate startDate;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy")
    @Schema(description = "End date", example = "31/12/2024")
    LocalDate endDate;

    @Schema(description = "Project status (PLANNING/ACTIVE/ON_HOLD/COMPLETED/CANCELLED)", example = "ACTIVE")
    String status;

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String projectName;
        private String description;
        private Integer managerId;
        private LocalDate startDate;
        private LocalDate endDate;
        private String status;

        public Builder projectName(String projectName) { this.projectName = projectName; return this; }
        public Builder description(String description) { this.description = description; return this; }
        public Builder managerId(Integer managerId) { this.managerId = managerId; return this; }
        public Builder startDate(LocalDate startDate) { this.startDate = startDate; return this; }
        public Builder endDate(LocalDate endDate) { this.endDate = endDate; return this; }
        public Builder status(String status) { this.status = status; return this; }

        public ProjectUpdateRequest build() {
            ProjectUpdateRequest request = new ProjectUpdateRequest();
            request.projectName = this.projectName;
            request.description = this.description;
            request.managerId = this.managerId;
            request.startDate = this.startDate;
            request.endDate = this.endDate;
            request.status = this.status;
            return request;
        }
    }
}