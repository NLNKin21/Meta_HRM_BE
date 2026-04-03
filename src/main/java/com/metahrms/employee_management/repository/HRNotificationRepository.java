package com.metahrms.employee_management.repository;

import com.metahrms.employee_management.entity.HRNotification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HRNotificationRepository extends JpaRepository<HRNotification, Long> {

    Page<HRNotification> findByRecipient_IdOrderByCreatedAtDesc(Integer recipientId, Pageable pageable);

    Page<HRNotification> findByRecipient_IdAndIsReadFalseOrderByCreatedAtDesc(Integer recipientId, Pageable pageable);

    long countByRecipient_IdAndIsReadFalse(Integer recipientId);
}