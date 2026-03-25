package com.metahrms.employee_management.dto.response.task.task;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "Task status information response")
public class TaskStatusResponse {

    @Schema(description = "Status ID", example = "1")
    Integer id;

    @Schema(description = "Status name in Vietnamese", example = "Chờ xử lý")
    String statusName;

    @Schema(description = "Status name in English", example = "To Do")
    String statusNameEn;

    @Schema(description = "Display order index", example = "1")
    Integer orderIndex;

    @Schema(description = "Status color (hex)", example = "#2196F3")
    String color;

    @Schema(description = "Icon name", example = "Assignment")
    String icon;

    @Schema(description = "Is this a completed status?", example = "false")
    Boolean isCompleted;

    @Schema(description = "Is this the default status?", example = "true")
    Boolean isDefault;

    @Schema(description = "Department name (null if common)", example = "IT Department")
    String department;

    @Schema(description = "Department ID (null if common)", example = "1")
    Integer departmentId;

    @Schema(description = "Is active?", example = "true")
    Boolean isActive;

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Integer id;
        private String statusName;
        private String statusNameEn;
        private Integer orderIndex;
        private String color;
        private String icon;
        private Boolean isCompleted;
        private Boolean isDefault;
        private String department;
        private Integer departmentId;
        private Boolean isActive;

        public Builder id(Integer id) {
            this.id = id;
            return this;
        }

        public Builder statusName(String statusName) {
            this.statusName = statusName;
            return this;
        }

        public Builder statusNameEn(String statusNameEn) {
            this.statusNameEn = statusNameEn;
            return this;
        }

        public Builder orderIndex(Integer orderIndex) {
            this.orderIndex = orderIndex;
            return this;
        }

        public Builder color(String color) {
            this.color = color;
            return this;
        }

        public Builder icon(String icon) {
            this.icon = icon;
            return this;
        }

        public Builder isCompleted(Boolean isCompleted) {
            this.isCompleted = isCompleted;
            return this;
        }

        public Builder isDefault(Boolean isDefault) {
            this.isDefault = isDefault;
            return this;
        }

        public Builder department(String department) {
            this.department = department;
            return this;
        }

        public Builder departmentId(Integer departmentId) {
            this.departmentId = departmentId;
            return this;
        }

        public Builder isActive(Boolean isActive) {
            this.isActive = isActive;
            return this;
        }

        public TaskStatusResponse build() {
            TaskStatusResponse response = new TaskStatusResponse();
            response.id = this.id;
            response.statusName = this.statusName;
            response.statusNameEn = this.statusNameEn;
            response.orderIndex = this.orderIndex;
            response.color = this.color;
            response.icon = this.icon;
            response.isCompleted = this.isCompleted;
            response.isDefault = this.isDefault;
            response.department = this.department;
            response.departmentId = this.departmentId;
            response.isActive = this.isActive;
            return response;
        }
    }
}
