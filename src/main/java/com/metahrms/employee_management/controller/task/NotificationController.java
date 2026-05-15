package com.metahrms.employee_management.controller.task;

import com.metahrms.employee_management.dto.response.ApiResponse;
import com.metahrms.employee_management.dto.response.task.notification.NotificationResponse;
import com.metahrms.employee_management.service.task.NotificationService;
import com.metahrms.employee_management.util.SecurityUtils; // ✅ thêm import

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

    @GetMapping
    @Operation(summary = "Get user notifications")
    public ResponseEntity<ApiResponse<Page<NotificationResponse>>> getNotifications(
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "20") int size) {

        // ✅ Sửa: bỏ @RequestHeader, lấy từ JWT
        Integer userId = SecurityUtils.getCurrentUserId();

        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<NotificationResponse> notifications = notificationService.getNotificationsByUser(userId, pageable);
        return ResponseEntity.ok(
                ApiResponse.success(notifications, "Retrieved notifications successfully")
        );
    }

    @GetMapping("/unread")
    @Operation(summary = "Get unread notifications")
    public ResponseEntity<ApiResponse<List<NotificationResponse>>> getUnreadNotifications() {

        // ✅ Sửa: bỏ @RequestHeader, lấy từ JWT
        Integer userId = SecurityUtils.getCurrentUserId();

        List<NotificationResponse> notifications = notificationService.getUnreadNotifications(userId);
        return ResponseEntity.ok(
                ApiResponse.success(notifications, "Retrieved unread notifications successfully")
        );
    }

    @GetMapping("/unread/count")
    @Operation(summary = "Count unread notifications")
    public ResponseEntity<ApiResponse<Long>> countUnread() {

        // ✅ Sửa: bỏ @RequestHeader, lấy từ JWT
        Integer userId = SecurityUtils.getCurrentUserId();

        Long count = notificationService.countUnread(userId);
        return ResponseEntity.ok(
                ApiResponse.success(count, "Retrieved unread count successfully")
        );
    }

    @PutMapping("/{id}/read")
    @Operation(summary = "Mark notification as read")
    public ResponseEntity<ApiResponse<Void>> markAsRead(
            @PathVariable("id") Integer id) {

        // không cần userId - giữ nguyên
        notificationService.markAsRead(id);
        return ResponseEntity.ok(
                ApiResponse.successMessage("Notification marked as read")
        );
    }

    @PutMapping("/read-all")
    @Operation(summary = "Mark all as read")
    public ResponseEntity<ApiResponse<Void>> markAllAsRead() {

        // ✅ Sửa: bỏ @RequestHeader, lấy từ JWT
        Integer userId = SecurityUtils.getCurrentUserId();

        notificationService.markAllAsRead(userId);
        return ResponseEntity.ok(
                ApiResponse.successMessage("All notifications marked as read")
        );
    }
}