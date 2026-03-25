package com.metahrms.employee_management.mapper.task;

import org.springframework.stereotype.Component;

import com.metahrms.employee_management.dto.response.task.notification.NotificationResponse;
import com.metahrms.employee_management.entity.Task.Notification;

@Component
public class NotificationMapper {

    public NotificationResponse toResponse(Notification notification) {
        return NotificationResponse.builder()
            .id(notification.getId())
            .userId(notification.getUser() != null ? notification.getUser().getId() : null)
            .type(notification.getType() != null ? notification.getType().name() : null)
            .referenceId(notification.getReferenceId())
            .title(notification.getTitle())
            .message(notification.getMessage())
            .link(notification.getLink())
            .isRead(notification.getIsRead())
            .readAt(notification.getReadAt())
            .createdAt(notification.getCreatedAt())
            .build();
    }
}
