package com.metahrms.employee_management.dto.response.task.history;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "Task history/audit log response")
public class TaskHistoryResponse {

    @Schema(description = "History ID", example = "1")
    Integer id;

    @Schema(description = "Task ID", example = "1")
    Integer taskId;

    @Schema(description = "User ID who made the change", example = "2")
    Integer userId;

    @Schema(description = "User name", example = "Nguyen Van A")
    String userName;

    @Schema(description = "Field name that changed", example = "status")
    String fieldName;

    @Schema(description = "Old value", example = "Chờ xử lý")
    String oldValue;

    @Schema(description = "New value", example = "Đang làm")
    String newValue;

    @Schema(description = "Action type (CREATE/UPDATE/DELETE/COMMENT)", example = "UPDATE")
    String actionType;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy HH:mm:ss")
    @Schema(description = "Change timestamp", example = "20/01/2024 15:30:00")
    LocalDateTime createdAt;

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Integer id;
        private Integer taskId;
        private Integer userId;
        private String userName;
        private String fieldName;
        private String oldValue;
        private String newValue;
        private String actionType;
        private LocalDateTime createdAt;

        public Builder id(Integer id) { this.id = id; return this; }
        public Builder taskId(Integer taskId) { this.taskId = taskId; return this; }
        public Builder userId(Integer userId) { this.userId = userId; return this; }
        public Builder userName(String userName) { this.userName = userName; return this; }
        public Builder fieldName(String fieldName) { this.fieldName = fieldName; return this; }
        public Builder oldValue(String oldValue) { this.oldValue = oldValue; return this; }
        public Builder newValue(String newValue) { this.newValue = newValue; return this; }
        public Builder actionType(String actionType) { this.actionType = actionType; return this; }
        public Builder createdAt(LocalDateTime createdAt) { this.createdAt = createdAt; return this; }

        public TaskHistoryResponse build() {
            TaskHistoryResponse response = new TaskHistoryResponse();
            response.id = this.id;
            response.taskId = this.taskId;
            response.userId = this.userId;
            response.userName = this.userName;
            response.fieldName = this.fieldName;
            response.oldValue = this.oldValue;
            response.newValue = this.newValue;
            response.actionType = this.actionType;
            response.createdAt = this.createdAt;
            return response;
        }
    }
}