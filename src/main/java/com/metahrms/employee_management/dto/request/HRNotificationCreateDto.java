package com.metahrms.employee_management.dto.request;

import com.metahrms.employee_management.enums.HRNotificationPriority;
import com.metahrms.employee_management.enums.HRNotificationType;
import com.metahrms.employee_management.enums.HRRelatedEntityType;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HRNotificationCreateDto {
    private Integer recipientId;
    private String title;
    private String content;
    private HRNotificationType type;
    private HRRelatedEntityType relatedEntityType;
    private Long relatedEntityId;
    private HRNotificationPriority priority;
    private Boolean createdBySystem;
}