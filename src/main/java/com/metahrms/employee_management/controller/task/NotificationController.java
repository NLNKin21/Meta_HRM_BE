package com.metahrms.employee_management.controller.task;

import com.metahrms.employee_management.dto.response.ApiResponse;
import com.metahrms.employee_management.dto.response.task.notification.NotificationResponse;
import com.metahrms.employee_management.service.task.NotificationService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/notifications")
@RequiredArgsConstructor
@Tag(name = "Notifications", description = "APIs for managing notifications")
public class NotificationController {

    private final NotificationService notificationService;

    /**
     * GET /api/notifications
     * Lấy notifications của user (có phân trang)
     */
    @GetMapping
    @Operation(summary = "Get user notifications", description = "Returns paginated notifications for current user")
    public ResponseEntity<ApiResponse<Page<NotificationResponse>>> getNotifications(
            @Parameter(description = "Current user ID", required = true)
            @RequestHeader("X-User-Id") Integer userId,
            @Parameter(description = "Page number", example = "0")
            @RequestParam(name = "page", defaultValue = "0") int page,
            @Parameter(description = "Page size", example = "20")
            @RequestParam(name = "size", defaultValue = "20") int size) {
        
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<NotificationResponse> notifications = notificationService.getNotificationsByUser(userId, pageable);
        return ResponseEntity.ok(
            ApiResponse.success(notifications, "Retrieved notifications successfully")
        );
    }

    /**
     * GET /api/notifications/unread
     * Lấy unread notifications
     */
    @GetMapping("/unread")
    @Operation(summary = "Get unread notifications", description = "Returns all unread notifications for current user")
    public ResponseEntity<ApiResponse<List<NotificationResponse>>> getUnreadNotifications(
            @Parameter(description = "Current user ID", required = true)
            @RequestHeader("X-User-Id") Integer userId) {
        
        List<NotificationResponse> notifications = notificationService.getUnreadNotifications(userId);
        return ResponseEntity.ok(
            ApiResponse.success(notifications, "Retrieved unread notifications successfully")
        );
    }

    /**
     * GET /api/notifications/unread/count
     * Đếm unread notifications
     */
    @GetMapping("/unread/count")
    @Operation(summary = "Count unread notifications", description = "Returns count of unread notifications")
    public ResponseEntity<ApiResponse<Long>> countUnread(
            @Parameter(description = "Current user ID", required = true)
            @RequestHeader("X-User-Id") Integer userId) {
        
        Long count = notificationService.countUnread(userId);
        return ResponseEntity.ok(
            ApiResponse.success(count, "Retrieved unread count successfully")
        );
    }

    /**
     * PUT /api/notifications/{id}/read
     * Đánh dấu notification đã đọc
     */
    @PutMapping("/{id}/read")
    @Operation(summary = "Mark notification as read", description = "Marks a single notification as read")
    public ResponseEntity<ApiResponse<Void>> markAsRead(
            @Parameter(description = "Notification ID", required = true)
            @PathVariable("id") Integer id) {
        
        notificationService.markAsRead(id);
        return ResponseEntity.ok(
            ApiResponse.successMessage("Notification marked as read")
        );
    }

    /**
     * PUT /api/notifications/read-all
     * Đánh dấu tất cả đã đọc
     */
    @PutMapping("/read-all")
    @Operation(summary = "Mark all as read", description = "Marks all notifications as read for current user")
    public ResponseEntity<ApiResponse<Void>> markAllAsRead(
            @Parameter(description = "Current user ID", required = true)
            @RequestHeader("X-User-Id") Integer userId) {
        
        notificationService.markAllAsRead(userId);
        return ResponseEntity.ok(
            ApiResponse.successMessage("All notifications marked as read")
        );
    }
}
