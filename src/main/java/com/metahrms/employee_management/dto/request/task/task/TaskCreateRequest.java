package com.metahrms.employee_management.dto.request.task.task;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@Schema(description = "Request to create a new task")
public class TaskCreateRequest {

    @NotBlank(message = "Task title is required")
    @Size(max = 255, message = "Title must not exceed 255 characters")
    @Schema(description = "Task title", example = "Develop employee module", required = true)
    String title;

    @Schema(description = "Task description", example = "Build CRUD for employee management")
    String description;

    @Schema(description = "Task type (TASK/BUG/FEATURE/IMPROVEMENT)", example = "FEATURE")
    String taskType;

    @Schema(description = "Priority (LOW/MEDIUM/HIGH/URGENT)", example = "HIGH")
    String priority;

    @NotNull(message = "Assignee is required")
    @Schema(description = "Assignee employee ID", example = "2", required = true)
    Integer assigneeId;

    @Schema(description = "Approver employee ID", example = "3")
    Integer approverId;

    @NotNull(message = "Department is required")
    @Schema(description = "Department ID", example = "1", required = true)
    Integer departmentId;

    @Schema(description = "Project ID", example = "1")
    Integer projectId;

    @DecimalMin(value = "0.0", message = "Estimated hours must be positive")
    @Schema(description = "Estimated hours", example = "80.00")
    BigDecimal estimatedHours;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy")
    @Schema(description = "Start date", example = "15/01/2024", type = "string", pattern = "dd/MM/yyyy")
    LocalDate startDate;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy")
    @Schema(description = "Due date", example = "15/02/2024", type = "string", pattern = "dd/MM/yyyy")
    LocalDate dueDate;

    @Schema(description = "Is urgent?", example = "false")
    Boolean isUrgent;

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String title;
        private String description;
        private String taskType;
        private String priority;
        private Integer assigneeId;
        private Integer approverId;
        private Integer departmentId;
        private Integer projectId;
        private BigDecimal estimatedHours;
        private LocalDate startDate;
        private LocalDate dueDate;
        private Boolean isUrgent;

        public Builder title(String title) { this.title = title; return this; }
        public Builder description(String description) { this.description = description; return this; }
        public Builder taskType(String taskType) { this.taskType = taskType; return this; }
        public Builder priority(String priority) { this.priority = priority; return this; }
        public Builder assigneeId(Integer assigneeId) { this.assigneeId = assigneeId; return this; }
        public Builder approverId(Integer approverId) { this.approverId = approverId; return this; }
        public Builder departmentId(Integer departmentId) { this.departmentId = departmentId; return this; }
        public Builder projectId(Integer projectId) { this.projectId = projectId; return this; }
        public Builder estimatedHours(BigDecimal estimatedHours) { this.estimatedHours = estimatedHours; return this; }
        public Builder startDate(LocalDate startDate) { this.startDate = startDate; return this; }
        public Builder dueDate(LocalDate dueDate) { this.dueDate = dueDate; return this; }
        public Builder isUrgent(Boolean isUrgent) { this.isUrgent = isUrgent; return this; }

        public TaskCreateRequest build() {
            TaskCreateRequest request = new TaskCreateRequest();
            request.title = this.title;
            request.description = this.description;
            request.taskType = this.taskType;
            request.priority = this.priority;
            request.assigneeId = this.assigneeId;
            request.approverId = this.approverId;
            request.departmentId = this.departmentId;
            request.projectId = this.projectId;
            request.estimatedHours = this.estimatedHours;
            request.startDate = this.startDate;
            request.dueDate = this.dueDate;
            request.isUrgent = this.isUrgent;
            return request;
        }
    }
}