package com.metahrms.employee_management.dto.response.task.task;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "Task summary response (for Board/Kanban view)")
public class TaskSummaryResponse {

    @Schema(description = "Task ID", example = "1")
    Integer id;

    @Schema(description = "Task code", example = "TSK-2024-001")
    String taskCode;

    @Schema(description = "Task title", example = "Develop employee module")
    String title;

    @Schema(description = "Priority", example = "HIGH")
    String priority;

    @Schema(description = "Status ID", example = "1")
    Integer statusId;

    @Schema(description = "Assignee ID", example = "2")
    Integer assigneeId;

    @Schema(description = "Assignee name", example = "Nguyen Van A")
    String assigneeName;

    @Schema(description = "Assignee avatar URL", example = "https://example.com/avatar.jpg")
    String assigneeAvatar;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy")
    @Schema(description = "Due date", example = "15/02/2024")
    LocalDate dueDate;

    @Schema(description = "Is late?", example = "false")
    Boolean isLate;

    @Schema(description = "Is urgent?", example = "true")
    Boolean isUrgent;

    @Schema(description = "Completion rate (%)", example = "75")
    Integer completionRate;

    @Schema(description = "Comment count", example = "5")
    Integer commentCount;

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Integer id;
        private String taskCode;
        private String title;
        private String priority;
        private Integer statusId;
        private Integer assigneeId;
        private String assigneeName;
        private String assigneeAvatar;
        private LocalDate dueDate;
        private Boolean isLate;
        private Boolean isUrgent;
        private Integer completionRate;
        private Integer commentCount;

        public Builder id(Integer id) { this.id = id; return this; }
        public Builder taskCode(String taskCode) { this.taskCode = taskCode; return this; }
        public Builder title(String title) { this.title = title; return this; }
        public Builder priority(String priority) { this.priority = priority; return this; }
        public Builder statusId(Integer statusId) { this.statusId = statusId; return this; }
        public Builder assigneeId(Integer assigneeId) { this.assigneeId = assigneeId; return this; }
        public Builder assigneeName(String assigneeName) { this.assigneeName = assigneeName; return this; }
        public Builder assigneeAvatar(String assigneeAvatar) { this.assigneeAvatar = assigneeAvatar; return this; }
        public Builder dueDate(LocalDate dueDate) { this.dueDate = dueDate; return this; }
        public Builder isLate(Boolean isLate) { this.isLate = isLate; return this; }
        public Builder isUrgent(Boolean isUrgent) { this.isUrgent = isUrgent; return this; }
        public Builder completionRate(Integer completionRate) { this.completionRate = completionRate; return this; }
        public Builder commentCount(Integer commentCount) { this.commentCount = commentCount; return this; }

        public TaskSummaryResponse build() {
            TaskSummaryResponse response = new TaskSummaryResponse();
            response.id = this.id;
            response.taskCode = this.taskCode;
            response.title = this.title;
            response.priority = this.priority;
            response.statusId = this.statusId;
            response.assigneeId = this.assigneeId;
            response.assigneeName = this.assigneeName;
            response.assigneeAvatar = this.assigneeAvatar;
            response.dueDate = this.dueDate;
            response.isLate = this.isLate;
            response.isUrgent = this.isUrgent;
            response.completionRate = this.completionRate;
            response.commentCount = this.commentCount;
            return response;
        }
    }
}