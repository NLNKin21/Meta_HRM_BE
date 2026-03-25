package com.metahrms.employee_management.dto.request.task.taskstatus;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@Schema(description = "Request to update a task status")
public class TaskStatusUpdateRequest {

    @Size(max = 100, message = "Status name must not exceed 100 characters")
    @Schema(description = "Status name in Vietnamese", example = "Chờ xử lý")
    String statusName;

    @Size(max = 100, message = "Status name (English) must not exceed 100 characters")
    @Schema(description = "Status name in English", example = "To Do")
    String statusNameEn;

    @Schema(description = "Display order", example = "1")
    Integer orderIndex;

    @Size(max = 7, message = "Color must be hex format (#RRGGBB)")
    @Schema(description = "Status color (hex)", example = "#2196F3")
    String color;

    @Schema(description = "Icon name", example = "Assignment")
    String icon;

    @Schema(description = "Is this a completed status?", example = "false")
    Boolean isCompleted;

    @Schema(description = "Is this the default status?", example = "true")
    Boolean isDefault;

    @Schema(description = "Is active?", example = "true")
    Boolean isActive;

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String statusName;
        private String statusNameEn;
        private Integer orderIndex;
        private String color;
        private String icon;
        private Boolean isCompleted;
        private Boolean isDefault;
        private Boolean isActive;

        public Builder statusName(String statusName) { this.statusName = statusName; return this; }
        public Builder statusNameEn(String statusNameEn) { this.statusNameEn = statusNameEn; return this; }
        public Builder orderIndex(Integer orderIndex) { this.orderIndex = orderIndex; return this; }
        public Builder color(String color) { this.color = color; return this; }
        public Builder icon(String icon) { this.icon = icon; return this; }
        public Builder isCompleted(Boolean isCompleted) { this.isCompleted = isCompleted; return this; }
        public Builder isDefault(Boolean isDefault) { this.isDefault = isDefault; return this; }
        public Builder isActive(Boolean isActive) { this.isActive = isActive; return this; }

        public TaskStatusUpdateRequest build() {
            TaskStatusUpdateRequest request = new TaskStatusUpdateRequest();
            request.statusName = this.statusName;
            request.statusNameEn = this.statusNameEn;
            request.orderIndex = this.orderIndex;
            request.color = this.color;
            request.icon = this.icon;
            request.isCompleted = this.isCompleted;
            request.isDefault = this.isDefault;
            request.isActive = this.isActive;
            return request;
        }
    }
}
