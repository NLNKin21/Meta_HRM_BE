package com.metahrms.employee_management.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Schema(description = "Response object containing presigned URL for uploading files to S3")
public class PresignedUrlResponse {
    @Schema(description = "Presigned URL for direct file upload to S3", example = "https://s3.amazonaws.com/bucket/documents/doc-123.pdf?X-Amz-Algorithm=AWS4-HMAC-SHA256&X-Amz-Credential=...")
    String presignedUrl;

    @Schema(description = "Object key/path in S3 bucket", example = "documents/2025/11/contract-123.pdf")
    String objectKey;

    @Schema(description = "MIME type of the file to be uploaded", example = "application/pdf")
    String contentType;

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String presignedUrl;
        private String objectKey;
        private String contentType;

        public Builder presignedUrl(String presignedUrl) {
            this.presignedUrl = presignedUrl;
            return this;
        }

        public Builder objectKey(String objectKey) {
            this.objectKey = objectKey;
            return this;
        }

        public Builder contentType(String contentType) {
            this.contentType = contentType;
            return this;
        }

        public PresignedUrlResponse build() {
            PresignedUrlResponse response = new PresignedUrlResponse();
            response.presignedUrl = this.presignedUrl;
            response.objectKey = this.objectKey;
            response.contentType = this.contentType;
            return response;
        }
    }
}
