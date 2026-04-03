package com.metahrms.employee_management.service;

import com.metahrms.employee_management.dto.request.HRNotificationCreateDto;
import com.metahrms.employee_management.dto.response.HRNotificationResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface HRNotificationService {

    HRNotificationResponseDto create(HRNotificationCreateDto dto);

    Page<HRNotificationResponseDto> getNotificationsByRecipient(Integer recipientId, Pageable pageable);

    Page<HRNotificationResponseDto> getUnreadNotificationsByRecipient(Integer recipientId, Pageable pageable);

    long countUnreadNotifications(Integer recipientId);

    HRNotificationResponseDto markAsRead(Long notificationId, Integer recipientId);

    void markAllAsRead(Integer recipientId);

    void deleteNotification(Long notificationId, Integer recipientId);
}