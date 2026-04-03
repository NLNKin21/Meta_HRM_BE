package com.metahrms.employee_management.controller;

import com.metahrms.employee_management.dto.response.ApiResponse;
import com.metahrms.employee_management.dto.response.HRNotificationResponseDto;
import com.metahrms.employee_management.dto.response.PageResponseDto;
import com.metahrms.employee_management.mapper.PageMapper;
import com.metahrms.employee_management.service.HRNotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/hr-notifications")
@RequiredArgsConstructor
public class HRNotificationController {

    private final HRNotificationService hrNotificationService;

    @GetMapping("/recipient/{recipientId}")
    public ApiResponse<PageResponseDto<HRNotificationResponseDto>> getNotificationsByRecipient(
            @PathVariable Integer recipientId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Page<HRNotificationResponseDto> result =
                hrNotificationService.getNotificationsByRecipient(recipientId, PageRequest.of(page, size));

        return ApiResponse.success(
                PageMapper.toPageResponse(result),
                "Lấy danh sách thông báo thành công"
        );
    }

    @GetMapping("/recipient/{recipientId}/unread")
    public ApiResponse<PageResponseDto<HRNotificationResponseDto>> getUnreadNotificationsByRecipient(
            @PathVariable Integer recipientId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Page<HRNotificationResponseDto> result =
                hrNotificationService.getUnreadNotificationsByRecipient(recipientId, PageRequest.of(page, size));

        return ApiResponse.success(
                PageMapper.toPageResponse(result),
                "Lấy danh sách thông báo chưa đọc thành công"
        );
    }

    @GetMapping("/recipient/{recipientId}/unread-count")
    public ApiResponse<Long> countUnreadNotifications(@PathVariable Integer recipientId) {
        return ApiResponse.success(
                hrNotificationService.countUnreadNotifications(recipientId),
                "Đếm số thông báo chưa đọc thành công"
        );
    }

    @PutMapping("/{notificationId}/read")
    public ApiResponse<HRNotificationResponseDto> markAsRead(
            @PathVariable Long notificationId,
            @RequestParam Integer recipientId
    ) {
        return ApiResponse.success(
                hrNotificationService.markAsRead(notificationId, recipientId),
                "Đánh dấu thông báo đã đọc thành công"
        );
    }

    @PutMapping("/recipient/{recipientId}/read-all")
    public ApiResponse<String> markAllAsRead(@PathVariable Integer recipientId) {
        hrNotificationService.markAllAsRead(recipientId);
        return ApiResponse.success("OK", "Đánh dấu tất cả thông báo đã đọc thành công");
    }

    @DeleteMapping("/{notificationId}")
    public ApiResponse<String> deleteNotification(
            @PathVariable Long notificationId,
            @RequestParam Integer recipientId
    ) {
        hrNotificationService.deleteNotification(notificationId, recipientId);
        return ApiResponse.success("OK", "Xóa thông báo thành công");
    }
}