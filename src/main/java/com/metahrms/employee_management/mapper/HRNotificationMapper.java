package com.metahrms.employee_management.mapper;

import com.metahrms.employee_management.dto.response.HRNotificationResponseDto;
import com.metahrms.employee_management.entity.HRNotification;

public class HRNotificationMapper {

    private HRNotificationMapper() {
    }

    public static HRNotificationResponseDto toDto(HRNotification entity) {
        if (entity == null) {
            return null;
        }

        return HRNotificationResponseDto.builder()
                .id(entity.getId())
                .recipientId(entity.getRecipient() != null ? entity.getRecipient().getId() : null)
                .recipientName(entity.getRecipient() != null ? entity.getRecipient().getFullName() : null)
                .title(entity.getTitle())
                .content(entity.getContent())
                .type(entity.getType())
                .relatedEntityType(entity.getRelatedEntityType())
                .relatedEntityId(entity.getRelatedEntityId())
                .isRead(entity.getIsRead())
                .priority(entity.getPriority())
                .createdAt(entity.getCreatedAt())
                .readAt(entity.getReadAt())
                .createdBySystem(entity.getCreatedBySystem())
                .build();
    }
}