package com.metahrms.employee_management.dto.response.task.comment;

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
@Schema(description = "Task comment response")
public class TaskCommentResponse {

    @Schema(description = "Comment ID", example = "1")
    Integer id;

    @Schema(description = "Task ID", example = "1")
    Integer taskId;

    @Schema(description = "User ID who commented", example = "2")
    Integer userId;

    @Schema(description = "User name", example = "Nguyen Van A")
    String userName;

    @Schema(description = "User email", example = "nguyenvana@example.com")
    String userEmail;

    @Schema(description = "Comment content")
    String content;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy HH:mm:ss")
    @Schema(description = "Comment created time", example = "20/01/2024 10:30:00")
    LocalDateTime createdAt;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy HH:mm:ss")
    @Schema(description = "Comment updated time", example = "20/01/2024 11:00:00")
    LocalDateTime updatedAt;

    String attachmentUrl;
    String attachmentName;
    String attachmentType;

    @Schema(description = "Can current user edit this comment?", example = "true")
    Boolean canEdit;

    @Schema(description = "Can current user delete this comment?", example = "true")
    Boolean canDelete;

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Integer id;
        private Integer taskId;
        private Integer userId;
        private String userName;
        private String userEmail;
        private String content;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
        private String attachmentUrl;
        private String attachmentName;
        private String attachmentType;
        private Boolean canEdit;
        private Boolean canDelete;

        public Builder id(Integer id) { this.id = id; return this; }
        public Builder taskId(Integer taskId) { this.taskId = taskId; return this; }
        public Builder userId(Integer userId) { this.userId = userId; return this; }
        public Builder userName(String userName) { this.userName = userName; return this; }
        public Builder userEmail(String userEmail) { this.userEmail = userEmail; return this; }
        public Builder content(String content) { this.content = content; return this; }
        public Builder createdAt(LocalDateTime createdAt) { this.createdAt = createdAt; return this; }
        public Builder updatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; return this; }
        public Builder attachmentUrl(String attachmentUrl) { this.attachmentUrl = attachmentUrl; return this; }
        public Builder attachmentName(String attachmentName) { this.attachmentName = attachmentName; return this; }
        public Builder attachmentType(String attachmentType) { this.attachmentType = attachmentType; return this; }
        public Builder canEdit(Boolean canEdit) { this.canEdit = canEdit; return this; }
        public Builder canDelete(Boolean canDelete) { this.canDelete = canDelete; return this; }

        public TaskCommentResponse build() {
            TaskCommentResponse response = new TaskCommentResponse();
            response.id = this.id;
            response.taskId = this.taskId;
            response.userId = this.userId;
            response.userName = this.userName;
            response.userEmail = this.userEmail;
            response.content = this.content;
            response.createdAt = this.createdAt;
            response.updatedAt = this.updatedAt;
            response.attachmentUrl = this.attachmentUrl;
            response.attachmentName = this.attachmentName;
            response.attachmentType = this.attachmentType;
            response.canEdit = this.canEdit;
            response.canDelete = this.canDelete;
            return response;
        }
    }
}