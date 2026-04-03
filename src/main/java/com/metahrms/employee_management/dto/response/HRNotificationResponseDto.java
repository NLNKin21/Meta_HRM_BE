package com.metahrms.employee_management.dto.response;

import com.metahrms.employee_management.enums.HRNotificationPriority;
import com.metahrms.employee_management.enums.HRNotificationType;
import com.metahrms.employee_management.enums.HRRelatedEntityType;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HRNotificationResponseDto {
    private Long id;
    private Integer recipientId;
    private String recipientName;
    private String title;
    private String content;
    private HRNotificationType type;
    private HRRelatedEntityType relatedEntityType;
    private Long relatedEntityId;
    private Boolean isRead;
    private HRNotificationPriority priority;
    private LocalDateTime createdAt;
    private LocalDateTime readAt;
    private Boolean createdBySystem;
}