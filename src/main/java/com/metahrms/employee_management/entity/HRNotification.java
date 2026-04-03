package com.metahrms.employee_management.entity;

import com.metahrms.employee_management.enums.HRNotificationPriority;
import com.metahrms.employee_management.enums.HRNotificationType;
import com.metahrms.employee_management.enums.HRRelatedEntityType;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "hr_notifications")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HRNotification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Người nhận thông báo
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "recipient_id", nullable = false)
    private Employee recipient;

    @Column(nullable = false, length = 255)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private HRNotificationType type;

    @Enumerated(EnumType.STRING)
    @Column(name = "related_entity_type", length = 50)
    private HRRelatedEntityType relatedEntityType;

    @Column(name = "related_entity_id")
    private Long relatedEntityId;

    @Column(name = "is_read", nullable = false)
    private Boolean isRead;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private HRNotificationPriority priority;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "read_at")
    private LocalDateTime readAt;

    @Column(name = "created_by_system", nullable = false)
    private Boolean createdBySystem;

    @PrePersist
    public void prePersist() {
        if (this.createdAt == null) {
            this.createdAt = LocalDateTime.now();
        }
        if (this.isRead == null) {
            this.isRead = false;
        }
        if (this.priority == null) {
            this.priority = HRNotificationPriority.NORMAL;
        }
        if (this.createdBySystem == null) {
            this.createdBySystem = true;
        }
    }
}