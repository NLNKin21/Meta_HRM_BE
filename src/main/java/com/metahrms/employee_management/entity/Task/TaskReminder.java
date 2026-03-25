package com.metahrms.employee_management.entity.Task;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

import com.metahrms.employee_management.entity.BaseEntity;

@Entity
@Table(name = "task_reminders")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TaskReminder extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "task_id", nullable = false)
    private Task task;

    @Column(name = "remind_before_hours", nullable = false)
    private Integer remindBeforeHours; // 24, 48, 72

    @Column(name = "remind_at")
    private LocalDateTime remindAt; // Thời điểm sẽ nhắc

    @Column(name = "is_sent")
    @Builder.Default
    private Boolean isSent = false;

    @Column(name = "sent_at")
    private LocalDateTime sentAt;
}
