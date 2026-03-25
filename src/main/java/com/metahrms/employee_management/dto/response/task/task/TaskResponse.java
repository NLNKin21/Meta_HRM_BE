package com.metahrms.employee_management.dto.response.task.task;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "Task information response (for list view)")
public class TaskResponse {

    @Schema(description = "Task ID", example = "1")
    Integer id;

    @Schema(description = "Task code", example = "TSK-2024-001")
    String taskCode;

    @Schema(description = "Task title", example = "Develop employee module")
    String title;

    @Schema(description = "Task type (TASK/BUG/FEATURE/IMPROVEMENT)", example = "FEATURE")
    String taskType;

    @Schema(description = "Priority (LOW/MEDIUM/HIGH/URGENT)", example = "HIGH")
    String priority;

    // ========== STATUS ==========
    @Schema(description = "Status ID", example = "1")
    Integer statusId;

    @Schema(description = "Status name", example = "Chờ xử lý")
    String statusName;

    @Schema(description = "Status color", example = "#2196F3")
    String statusColor;

    @Schema(description = "Status icon", example = "Assignment")
    String statusIcon;

    // ========== ASSIGNEE ==========
    @Schema(description = "Assignee ID", example = "2")
    Integer assigneeId;

    @Schema(description = "Assignee name", example = "Nguyen Van A")
    String assigneeName;

    @Schema(description = "Assignee email", example = "nguyenvana@example.com")
    String assigneeEmail;

    // ========== REPORTER ==========
    @Schema(description = "Reporter ID", example = "1")
    Integer reporterId;

    @Schema(description = "Reporter name", example = "Tran Thi B")
    String reporterName;

    // ========== DEPARTMENT ==========
    @Schema(description = "Department ID", example = "1")
    Integer departmentId;

    @Schema(description = "Department name", example = "IT Department")
    String departmentName;

    // ========== PROJECT ==========
    @Schema(description = "Project ID", example = "1")
    Integer projectId;

    @Schema(description = "Project name", example = "HRM System 2024")
    String projectName;

    // ========== TIME ==========
    @Schema(description = "Estimated hours", example = "80.00")
    BigDecimal estimatedHours;

    @Schema(description = "Actual hours", example = "45.50")
    BigDecimal actualHours;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy")
    @Schema(description = "Start date", example = "15/01/2024", type = "string", pattern = "dd/MM/yyyy")
    LocalDate startDate;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy")
    @Schema(description = "Due date", example = "15/02/2024", type = "string", pattern = "dd/MM/yyyy")
    LocalDate dueDate;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy HH:mm:ss")
    @Schema(description = "Completed date", example = "10/02/2024 14:30:00", type = "string")
    LocalDateTime completedAt;

    // ========== PROGRESS ==========
    @Schema(description = "Completion rate (%)", example = "75")
    Integer completionRate;

    @Schema(description = "Is late?", example = "false")
    Boolean isLate;

    @Schema(description = "Is urgent?", example = "true")
    Boolean isUrgent;

    // ========== STATS ==========
    @Schema(description = "Number of comments", example = "5")
    Integer commentCount;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy HH:mm:ss")
    @Schema(description = "Creation timestamp", example = "15/01/2024 09:00:00", type = "string")
    LocalDateTime createdAt;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy HH:mm:ss")
    @Schema(description = "Last update timestamp", example = "20/01/2024 16:45:00", type = "string")
    LocalDateTime updatedAt;

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Integer id;
        private String taskCode;
        private String title;
        private String taskType;
        private String priority;
        private Integer statusId;
        private String statusName;
        private String statusColor;
        private String statusIcon;
        private Integer assigneeId;
        private String assigneeName;
        private String assigneeEmail;
        private Integer reporterId;
        private String reporterName;
        private Integer departmentId;
        private String departmentName;
        private Integer projectId;
        private String projectName;
        private BigDecimal estimatedHours;
        private BigDecimal actualHours;
        private LocalDate startDate;
        private LocalDate dueDate;
        private LocalDateTime completedAt;
        private Integer completionRate;
        private Boolean isLate;
        private Boolean isUrgent;
        private Integer commentCount;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;

        public Builder id(Integer id) {
            this.id = id;
            return this;
        }

        public Builder taskCode(String taskCode) {
            this.taskCode = taskCode;
            return this;
        }

        public Builder title(String title) {
            this.title = title;
            return this;
        }

        public Builder taskType(String taskType) {
            this.taskType = taskType;
            return this;
        }

        public Builder priority(String priority) {
            this.priority = priority;
            return this;
        }

        public Builder statusId(Integer statusId) {
            this.statusId = statusId;
            return this;
        }

        public Builder statusName(String statusName) {
            this.statusName = statusName;
            return this;
        }

        public Builder statusColor(String statusColor) {
            this.statusColor = statusColor;
            return this;
        }

        public Builder statusIcon(String statusIcon) {
            this.statusIcon = statusIcon;
            return this;
        }

        public Builder assigneeId(Integer assigneeId) {
            this.assigneeId = assigneeId;
            return this;
        }

        public Builder assigneeName(String assigneeName) {
            this.assigneeName = assigneeName;
            return this;
        }

        public Builder assigneeEmail(String assigneeEmail) {
            this.assigneeEmail = assigneeEmail;
            return this;
        }

        public Builder reporterId(Integer reporterId) {
            this.reporterId = reporterId;
            return this;
        }

        public Builder reporterName(String reporterName) {
            this.reporterName = reporterName;
            return this;
        }

        public Builder departmentId(Integer departmentId) {
            this.departmentId = departmentId;
            return this;
        }

        public Builder departmentName(String departmentName) {
            this.departmentName = departmentName;
            return this;
        }

        public Builder projectId(Integer projectId) {
            this.projectId = projectId;
            return this;
        }

        public Builder projectName(String projectName) {
            this.projectName = projectName;
            return this;
        }

        public Builder estimatedHours(BigDecimal estimatedHours) {
            this.estimatedHours = estimatedHours;
            return this;
        }

        public Builder actualHours(BigDecimal actualHours) {
            this.actualHours = actualHours;
            return this;
        }

        public Builder startDate(LocalDate startDate) {
            this.startDate = startDate;
            return this;
        }

        public Builder dueDate(LocalDate dueDate) {
            this.dueDate = dueDate;
            return this;
        }

        public Builder completedAt(LocalDateTime completedAt) {
            this.completedAt = completedAt;
            return this;
        }

        public Builder completionRate(Integer completionRate) {
            this.completionRate = completionRate;
            return this;
        }

        public Builder isLate(Boolean isLate) {
            this.isLate = isLate;
            return this;
        }

        public Builder isUrgent(Boolean isUrgent) {
            this.isUrgent = isUrgent;
            return this;
        }

        public Builder commentCount(Integer commentCount) {
            this.commentCount = commentCount;
            return this;
        }

        public Builder createdAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public Builder updatedAt(LocalDateTime updatedAt) {
            this.updatedAt = updatedAt;
            return this;
        }

        public TaskResponse build() {
            TaskResponse response = new TaskResponse();
            response.id = this.id;
            response.taskCode = this.taskCode;
            response.title = this.title;
            response.taskType = this.taskType;
            response.priority = this.priority;
            response.statusId = this.statusId;
            response.statusName = this.statusName;
            response.statusColor = this.statusColor;
            response.statusIcon = this.statusIcon;
            response.assigneeId = this.assigneeId;
            response.assigneeName = this.assigneeName;
            response.assigneeEmail = this.assigneeEmail;
            response.reporterId = this.reporterId;
            response.reporterName = this.reporterName;
            response.departmentId = this.departmentId;
            response.departmentName = this.departmentName;
            response.projectId = this.projectId;
            response.projectName = this.projectName;
            response.estimatedHours = this.estimatedHours;
            response.actualHours = this.actualHours;
            response.startDate = this.startDate;
            response.dueDate = this.dueDate;
            response.completedAt = this.completedAt;
            response.completionRate = this.completionRate;
            response.isLate = this.isLate;
            response.isUrgent = this.isUrgent;
            response.commentCount = this.commentCount;
            response.createdAt = this.createdAt;
            response.updatedAt = this.updatedAt;
            return response;
        }
    }
}