package com.metahrms.employee_management.controller.task;

import com.metahrms.employee_management.dto.request.task.comment.CommentCreateRequest;
import com.metahrms.employee_management.dto.response.ApiResponse;
import com.metahrms.employee_management.dto.response.task.comment.TaskCommentResponse;
import com.metahrms.employee_management.service.task.TaskCommentService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tasks")
@RequiredArgsConstructor
@Tag(name = "Task Comments", description = "APIs for managing task comments")
public class TaskCommentController {

    private final TaskCommentService commentService;

    /**
     * GET /api/tasks/{taskId}/comments
     * Lấy tất cả comments của task
     */
    @GetMapping("/{taskId}/comments")
    @Operation(summary = "Get comments by task", description = "Returns all comments for specific task")
    public ResponseEntity<ApiResponse<List<TaskCommentResponse>>> getCommentsByTaskId(
            @Parameter(description = "Task ID", required = true)
            @PathVariable("taskId") Integer taskId,
            @Parameter(description = "Current user ID", required = true)
            @RequestHeader("X-User-Id") Integer currentUserId) {
        
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
            @Parameter(description = "Task ID", required = true)
            @PathVariable Integer taskId,
            @Valid @RequestBody CommentCreateRequest request,
            @Parameter(description = "Current user ID", required = true)
            @RequestHeader("X-User-Id") Integer userId) {
        
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
            @Parameter(description = "Comment ID", required = true)
            @PathVariable Integer commentId,
            @Parameter(description = "New comment content", required = true)
            @RequestParam String content,
            @Parameter(description = "Current user ID", required = true)
            @RequestHeader("X-User-Id") Integer userId) {
        
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
            @Parameter(description = "Comment ID", required = true)
            @PathVariable Integer commentId,
            @Parameter(description = "Current user ID", required = true)
            @RequestHeader("X-User-Id") Integer userId) {
        
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
            @Parameter(description = "Task ID", required = true)
            @PathVariable Integer taskId) {
        
        Long count = commentService.countCommentsByTaskId(taskId);
        return ResponseEntity.ok(
            ApiResponse.success(count, "Retrieved comment count successfully")
        );
    }
}