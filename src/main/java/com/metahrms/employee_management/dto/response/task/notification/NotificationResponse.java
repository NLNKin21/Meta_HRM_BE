package com.metahrms.employee_management.dto.response.task.notification;

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
@Schema(description = "Notification response")
public class NotificationResponse {

    @Schema(description = "Notification ID", example = "1")
    Integer id;

    @Schema(description = "User ID", example = "2")
    Integer userId;

    @Schema(description = "Notification type", example = "TASK_ASSIGNED")
    String type;

    @Schema(description = "Reference ID (task_id, comment_id, etc)", example = "5")
    Integer referenceId;

    @Schema(description = "Notification title", example = "Bạn được giao task mới")
    String title;

    @Schema(description = "Notification message", example = "Task 'Develop employee module' đã được giao cho bạn")
    String message;

    @Schema(description = "Link to redirect", example = "/tasks/5")
    String link;

    @Schema(description = "Is read?", example = "false")
    Boolean isRead;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy HH:mm:ss")
    @Schema(description = "Read timestamp", example = "20/01/2024 16:00:00")
    LocalDateTime readAt;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy HH:mm:ss")
    @Schema(description = "Creation timestamp", example = "20/01/2024 15:30:00")
    LocalDateTime createdAt;

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Integer id;
        private Integer userId;
        private String type;
        private Integer referenceId;
        private String title;
        private String message;
        private String link;
        private Boolean isRead;
        private LocalDateTime readAt;
        private LocalDateTime createdAt;

        public Builder id(Integer id) { this.id = id; return this; }
        public Builder userId(Integer userId) { this.userId = userId; return this; }
        public Builder type(String type) { this.type = type; return this; }
        public Builder referenceId(Integer referenceId) { this.referenceId = referenceId; return this; }
        public Builder title(String title) { this.title = title; return this; }
        public Builder message(String message) { this.message = message; return this; }
        public Builder link(String link) { this.link = link; return this; }
        public Builder isRead(Boolean isRead) { this.isRead = isRead; return this; }
        public Builder readAt(LocalDateTime readAt) { this.readAt = readAt; return this; }
        public Builder createdAt(LocalDateTime createdAt) { this.createdAt = createdAt; return this; }

        public NotificationResponse build() {
            NotificationResponse response = new NotificationResponse();
            response.id = this.id;
            response.userId = this.userId;
            response.type = this.type;
            response.referenceId = this.referenceId;
            response.title = this.title;
            response.message = this.message;
            response.link = this.link;
            response.isRead = this.isRead;
            response.readAt = this.readAt;
            response.createdAt = this.createdAt;
            return response;
        }
    }
}