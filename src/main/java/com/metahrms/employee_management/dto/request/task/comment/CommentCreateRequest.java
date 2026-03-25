package com.metahrms.employee_management.dto.request.task.comment;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@Schema(description = "Request to create a comment")
public class CommentCreateRequest {

    @NotBlank(message = "Comment content is required")
    @Schema(description = "Comment content", example = "Đã hoàn thành phần UI", required = true)
    String content;

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String content;

        public Builder content(String content) { this.content = content; return this; }

        public CommentCreateRequest build() {
            CommentCreateRequest request = new CommentCreateRequest();
            request.content = this.content;
            return request;
        }
    }
}
