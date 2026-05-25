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
    @Schema(
            description = "Comment content",
            example = "Đã hoàn thành phần UI",
            required = true
    )
    String content;

    @Schema(description = "File URL on Cloudinary")
    String attachmentUrl;

    @Schema(description = "Original file name", example = "bao-cao.pdf")
    String attachmentName;

    @Schema(description = "File type: image | pdf | other")
    String attachmentType;

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {

        private String content;
        private String attachmentUrl;
        private String attachmentName;
        private String attachmentType;

        public Builder content(String content) {
            this.content = content;
            return this;
        }

        public Builder attachmentUrl(String attachmentUrl) {
            this.attachmentUrl = attachmentUrl;
            return this;
        }

        public Builder attachmentName(String attachmentName) {
            this.attachmentName = attachmentName;
            return this;
        }

        public Builder attachmentType(String attachmentType) {
            this.attachmentType = attachmentType;
            return this;
        }

        public CommentCreateRequest build() {
            CommentCreateRequest request = new CommentCreateRequest();
            request.content = this.content;
            request.attachmentUrl = this.attachmentUrl;
            request.attachmentName = this.attachmentName;
            request.attachmentType = this.attachmentType;
            return request;
        }
    }
}