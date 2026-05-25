package com.metahrms.employee_management.controller.task;

import com.metahrms.employee_management.dto.request.task.comment.CommentCreateRequest;
import com.metahrms.employee_management.dto.response.ApiResponse;
import com.metahrms.employee_management.dto.response.task.comment.TaskCommentResponse;
import com.metahrms.employee_management.service.CloudinaryService;
import com.metahrms.employee_management.service.task.TaskCommentService;
import com.metahrms.employee_management.util.SecurityUtils;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
@RequestMapping("/tasks")
@RequiredArgsConstructor
@Tag(name = "Task Comments", description = "APIs for managing task comments")
public class TaskCommentController {

    private final TaskCommentService commentService;
    private final CloudinaryService cloudinaryService;

    /**
     * GET /api/tasks/{taskId}/comments
     * Lấy tất cả comments của task
     */
    @GetMapping("/{taskId}/comments")
    @Operation(summary = "Get comments by task", description = "Returns all comments for specific task")
    public ResponseEntity<ApiResponse<List<TaskCommentResponse>>> getCommentsByTaskId(
            @PathVariable("taskId") Integer taskId) {

        Integer currentUserId = SecurityUtils.getCurrentUserId();

        List<TaskCommentResponse> comments = commentService.getCommentsByTaskId(taskId, currentUserId);
        return ResponseEntity.ok(
            ApiResponse.success(comments, "Retrieved comments successfully")
        );
    }
    /**
     * POST /api/tasks/{taskId}/comments
     * Thêm comment vào task
     */
    @PostMapping("/{taskId}/comments")
    @Operation(summary = "Add comment to task", description = "Creates a new comment on task")
    public ResponseEntity<ApiResponse<TaskCommentResponse>> addComment(
            @PathVariable("taskId") Integer taskId,
            @Valid @RequestBody CommentCreateRequest request) {

        Integer userId = SecurityUtils.getCurrentUserId();

        TaskCommentResponse created = commentService.addComment(taskId, request, userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(
            ApiResponse.success(created, "Comment added successfully")
        );
    }

    /**
     * PUT /api/tasks/comments/{commentId}
     * Cập nhật comment
     */
    @PutMapping("/comments/{commentId}")
    @Operation(summary = "Update comment", description = "Updates an existing comment")
    public ResponseEntity<ApiResponse<TaskCommentResponse>> updateComment(
            @PathVariable("commentId") Integer commentId,
            @RequestParam String content) {

        Integer userId = SecurityUtils.getCurrentUserId();

        TaskCommentResponse updated = commentService.updateComment(commentId, content, userId);
        return ResponseEntity.ok(
            ApiResponse.success(updated, "Comment updated successfully")
        );
    }

    /**
     * DELETE /api/tasks/comments/{commentId}
     * Xóa comment
     */
    @DeleteMapping("/comments/{commentId}")
    @Operation(summary = "Delete comment", description = "Soft deletes a comment")
    public ResponseEntity<ApiResponse<Void>> deleteComment(
            @PathVariable("commentId") Integer commentId) {

        Integer userId = SecurityUtils.getCurrentUserId();

        commentService.deleteComment(commentId, userId);
        return ResponseEntity.ok(
            ApiResponse.successMessage("Comment deleted successfully")
        );
    }

    /**
     * GET /api/tasks/{taskId}/comments/count
     * Đếm số comments
     */
    @GetMapping("/{taskId}/comments/count")
    @Operation(summary = "Count comments", description = "Returns number of comments for task")
    public ResponseEntity<ApiResponse<Long>> countComments(
            @PathVariable("taskId") Integer taskId) {

        Long count = commentService.countCommentsByTaskId(taskId);
        return ResponseEntity.ok(
            ApiResponse.success(count, "Retrieved comment count successfully")
        );
    }

    /**
     * ✅ THÊM: Upload attachment trước, trả về URL
     * POST /api/tasks/comments/upload-attachment
     */
    @PostMapping(value = "/comments/upload-attachment",
                 consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Upload attachment for comment")
    public ResponseEntity<ApiResponse<AttachmentUploadResponse>> uploadAttachment(
            @RequestParam("file") MultipartFile file) {

        Integer userId = SecurityUtils.getCurrentUserId();
        log.info("[COMMENT-ATTACHMENT] Uploading for userId={}, file={}, size={}",
                userId, file.getOriginalFilename(), file.getSize());

        try {
            // Validate
            if (file.isEmpty()) {
                return ResponseEntity.badRequest()
                    .body(ApiResponse.error(400, "File is empty"));
            }

            String contentType = file.getContentType();
            String originalName = file.getOriginalFilename();
            String ext = getExtension(originalName);

            // Chỉ cho phép image và pdf
            if (!isAllowedType(contentType, ext)) {
                return ResponseEntity.badRequest()
                    .body(ApiResponse.error(400,
                        "Chỉ chấp nhận file ảnh (JPG, PNG) hoặc PDF. File hiện tại: " + contentType));
            }

            // Max 10MB
            if (file.getSize() > 10 * 1024 * 1024) {
                return ResponseEntity.badRequest()
                    .body(ApiResponse.error(400, "File không được vượt quá 10MB"));
            }

            // Upload lên Cloudinary vào folder reports
            String attachmentUrl = cloudinaryService.uploadReportFile(file, userId);

            // Xác định type
            String attachmentType = isImage(contentType, ext) ? "image"
                                  : "pdf".equalsIgnoreCase(ext) ? "pdf"
                                  : "other";

            AttachmentUploadResponse data = AttachmentUploadResponse.builder()
                .attachmentUrl(attachmentUrl)
                .attachmentName(originalName)
                .attachmentType(attachmentType)
                .build();

            log.info("[COMMENT-ATTACHMENT] Upload success: url={}", attachmentUrl);

            return ResponseEntity.ok(
                ApiResponse.success(data, "Upload thành công")
            );

        } catch (IOException e) {
            log.error("[COMMENT-ATTACHMENT] Upload failed", e);
            return ResponseEntity.status(500)
                .body(ApiResponse.error(500, "Upload thất bại: " + e.getMessage()));
        }
    }

    // ── Helper methods ──
    private String getExtension(String filename) {
        if (filename == null || !filename.contains(".")) return "";
        return filename.substring(filename.lastIndexOf(".") + 1).toLowerCase();
    }

    private boolean isAllowedType(String contentType, String ext) {
        if (contentType == null) return false;
        return contentType.startsWith("image/")
            || "application/pdf".equals(contentType)
            || "pdf".equals(ext);
    }

    private boolean isImage(String contentType, String ext) {
        if (contentType != null && contentType.startsWith("image/")) return true;
        return Set.of("jpg", "jpeg", "png", "gif", "webp").contains(ext);
    }

    // ── Inner DTO ──
    @lombok.Data
    @lombok.Builder
    @lombok.NoArgsConstructor
    @lombok.AllArgsConstructor
    public static class AttachmentUploadResponse {
        private String attachmentUrl;
        private String attachmentName;
        private String attachmentType;
    }

}