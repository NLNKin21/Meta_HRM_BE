package com.metahrms.employee_management.dto.request.task.task;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@Schema(description = "Request to update task status")
public class TaskStatusUpdateRequest {

    @NotNull(message = "Status ID is required")
    @Schema(description = "New status ID", example = "2", required = true)
    Integer statusId;

    @Schema(description = "Comment when changing status", example = "Đã bắt đầu làm việc")
    String comment;

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Integer statusId;
        private String comment;

        public Builder statusId(Integer statusId) { this.statusId = statusId; return this; }
        public Builder comment(String comment) { this.comment = comment; return this; }

        public TaskStatusUpdateRequest build() {
            TaskStatusUpdateRequest request = new TaskStatusUpdateRequest();
            request.statusId = this.statusId;
            request.comment = this.comment;
            return request;
        }
    }
}
