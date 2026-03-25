package com.metahrms.employee_management.dto.request.task.task;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@Schema(description = "Request to update a task")
public class TaskUpdateRequest {

    @Size(max = 255, message = "Title must not exceed 255 characters")
    @Schema(description = "Task title", example = "Develop employee module")
    String title;

    @Schema(description = "Task description")
    String description;

    @Schema(description = "Task type (TASK/BUG/FEATURE/IMPROVEMENT)", example = "FEATURE")
    String taskType;

    @Schema(description = "Priority (LOW/MEDIUM/HIGH/URGENT)", example = "HIGH")
    String priority;

    @Schema(description = "Assignee employee ID", example = "2")
    Integer assigneeId;

    @Schema(description = "Approver employee ID", example = "3")
    Integer approverId;

    @Schema(description = "Project ID", example = "1")
    Integer projectId;

    @DecimalMin(value = "0.0", message = "Estimated hours must be positive")
    @Schema(description = "Estimated hours", example = "80.00")
    BigDecimal estimatedHours;

    @DecimalMin(value = "0.0", message = "Actual hours must be positive")
    @Schema(description = "Actual hours worked", example = "45.50")
    BigDecimal actualHours;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy")
    @Schema(description = "Start date", example = "15/01/2024")
    LocalDate startDate;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy")
    @Schema(description = "Due date", example = "15/02/2024")
    LocalDate dueDate;

    @Min(value = 0, message = "Completion rate must be between 0 and 100")
    @Max(value = 100, message = "Completion rate must be between 0 and 100")
    @Schema(description = "Completion rate (%)", example = "75")
    Integer completionRate;

    @Schema(description = "Is urgent?", example = "true")
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
        private Integer projectId;
        private BigDecimal estimatedHours;
        private BigDecimal actualHours;
        private LocalDate startDate;
        private LocalDate dueDate;
        private Integer completionRate;
        private Boolean isUrgent;

        public Builder title(String title) { this.title = title; return this; }
        public Builder description(String description) { this.description = description; return this; }
        public Builder taskType(String taskType) { this.taskType = taskType; return this; }
        public Builder priority(String priority) { this.priority = priority; return this; }
        public Builder assigneeId(Integer assigneeId) { this.assigneeId = assigneeId; return this; }
        public Builder approverId(Integer approverId) { this.approverId = approverId; return this; }
        public Builder projectId(Integer projectId) { this.projectId = projectId; return this; }
        public Builder estimatedHours(BigDecimal estimatedHours) { this.estimatedHours = estimatedHours; return this; }
        public Builder actualHours(BigDecimal actualHours) { this.actualHours = actualHours; return this; }
        public Builder startDate(LocalDate startDate) { this.startDate = startDate; return this; }
        public Builder dueDate(LocalDate dueDate) { this.dueDate = dueDate; return this; }
        public Builder completionRate(Integer completionRate) { this.completionRate = completionRate; return this; }
        public Builder isUrgent(Boolean isUrgent) { this.isUrgent = isUrgent; return this; }

        public TaskUpdateRequest build() {
            TaskUpdateRequest request = new TaskUpdateRequest();
            request.title = this.title;
            request.description = this.description;
            request.taskType = this.taskType;
            request.priority = this.priority;
            request.assigneeId = this.assigneeId;
            request.approverId = this.approverId;
            request.projectId = this.projectId;
            request.estimatedHours = this.estimatedHours;
            request.actualHours = this.actualHours;
            request.startDate = this.startDate;
            request.dueDate = this.dueDate;
            request.completionRate = this.completionRate;
            request.isUrgent = this.isUrgent;
            return request;
        }
    }
}