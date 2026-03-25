package com.metahrms.employee_management.entity.Task;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

import com.metahrms.employee_management.entity.Employee;

@Entity
@Table(name = "task_histories", indexes = {
    @Index(name = "idx_history_task", columnList = "task_id"),
    @Index(name = "idx_history_created", columnList = "created_at")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TaskHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "task_id", nullable = false)
    private Task task;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private Employee user;

    @Column(name = "field_name", length = 50)
    private String fieldName; // "status", "assignee", "due_date"

    @Column(name = "old_value", columnDefinition = "TEXT")
    private String oldValue;

    @Column(name = "new_value", columnDefinition = "TEXT")
    private String newValue;

    @Column(name = "action_type", length = 50)
    private String actionType; // CREATE, UPDATE, DELETE, COMMENT

    @Column(name = "created_at", nullable = false)
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();
}
