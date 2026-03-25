package com.metahrms.employee_management.entity.Task;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

import com.metahrms.employee_management.entity.Employee;
import com.metahrms.employee_management.enums.Task.NotificationType;

@Entity
@Table(name = "notifications", indexes = {
    @Index(name = "idx_notification_user", columnList = "user_id"),
    @Index(name = "idx_notification_read", columnList = "is_read"),
    @Index(name = "idx_notification_created", columnList = "created_at")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private Employee user;

    @Enumerated(EnumType.STRING)
    @Column(name = "notification_type", length = 50)
    private NotificationType type;

    @Column(name = "reference_id")
    private Integer referenceId; // task_id, comment_id, etc.

    @Column(length = 255)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String message;

    @Column(length = 500)
    private String link; // URL để redirect

    @Column(name = "is_read")
    @Builder.Default
    private Boolean isRead = false;

    @Column(name = "read_at")
    private LocalDateTime readAt;

    @Column(name = "created_at")
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();
}