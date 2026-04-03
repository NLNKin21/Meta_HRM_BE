package com.metahrms.employee_management.service.impl;

import com.metahrms.employee_management.dto.request.HRNotificationCreateDto;
import com.metahrms.employee_management.dto.response.HRNotificationResponseDto;
import com.metahrms.employee_management.entity.Employee;
import com.metahrms.employee_management.entity.HRNotification;
import com.metahrms.employee_management.mapper.HRNotificationMapper;
import com.metahrms.employee_management.repository.EmployeeRepository;
import com.metahrms.employee_management.repository.HRNotificationRepository;
import com.metahrms.employee_management.service.HRNotificationService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class HRNotificationServiceImpl implements HRNotificationService {

    private final HRNotificationRepository hrNotificationRepository;
    private final EmployeeRepository employeeRepository;

    @Override
    public HRNotificationResponseDto create(HRNotificationCreateDto dto) {
        Employee recipient = employeeRepository.findById(dto.getRecipientId())
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy người nhận thông báo với id = " + dto.getRecipientId()));

        HRNotification entity = HRNotification.builder()
                .recipient(recipient)
                .title(dto.getTitle())
                .content(dto.getContent())
                .type(dto.getType())
                .relatedEntityType(dto.getRelatedEntityType())
                .relatedEntityId(dto.getRelatedEntityId())
                .priority(dto.getPriority())
                .createdBySystem(dto.getCreatedBySystem() != null ? dto.getCreatedBySystem() : true)
                .isRead(false)
                .build();

        entity = hrNotificationRepository.save(entity);
        return HRNotificationMapper.toDto(entity);
    }

    @Override
    public Page<HRNotificationResponseDto> getNotificationsByRecipient(Integer recipientId, Pageable pageable) {
        return hrNotificationRepository
                .findByRecipient_IdOrderByCreatedAtDesc(recipientId, pageable)
                .map(HRNotificationMapper::toDto);
    }

    @Override
    public Page<HRNotificationResponseDto> getUnreadNotificationsByRecipient(Integer recipientId, Pageable pageable) {
        return hrNotificationRepository
                .findByRecipient_IdAndIsReadFalseOrderByCreatedAtDesc(recipientId, pageable)
                .map(HRNotificationMapper::toDto);
    }

    @Override
    public long countUnreadNotifications(Integer recipientId) {
        return hrNotificationRepository.countByRecipient_IdAndIsReadFalse(recipientId);
    }

    @Override
    public HRNotificationResponseDto markAsRead(Long notificationId, Integer recipientId) {
        HRNotification notification = hrNotificationRepository.findById(notificationId)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy thông báo với id = " + notificationId));

        if (!notification.getRecipient().getId().equals(recipientId)) {
            throw new RuntimeException("Bạn không có quyền cập nhật thông báo này");
        }

        notification.setIsRead(true);
        notification.setReadAt(LocalDateTime.now());

        notification = hrNotificationRepository.save(notification);
        return HRNotificationMapper.toDto(notification);
    }

    @Override
    public void markAllAsRead(Integer recipientId) {
        Page<HRNotification> page = hrNotificationRepository
                .findByRecipient_IdAndIsReadFalseOrderByCreatedAtDesc(recipientId, Pageable.unpaged());

        for (HRNotification notification : page.getContent()) {
            notification.setIsRead(true);
            notification.setReadAt(LocalDateTime.now());
        }

        hrNotificationRepository.saveAll(page.getContent());
    }

    @Override
    public void deleteNotification(Long notificationId, Integer recipientId) {
        HRNotification notification = hrNotificationRepository.findById(notificationId)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy thông báo với id = " + notificationId));

        if (!notification.getRecipient().getId().equals(recipientId)) {
            throw new RuntimeException("Bạn không có quyền xóa thông báo này");
        }

        hrNotificationRepository.delete(notification);
    }
}