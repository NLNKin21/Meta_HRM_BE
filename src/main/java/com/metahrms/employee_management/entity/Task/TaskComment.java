package com.metahrms.employee_management.entity.Task;

import com.metahrms.employee_management.entity.BaseEntity;
import com.metahrms.employee_management.entity.Employee;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "task_comments", indexes = {
    @Index(name = "idx_comment_task", columnList = "task_id")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TaskComment extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "task_id", nullable = false)
    private Task task;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private Employee user;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;

    @Column(name = "is_deleted")
    @Builder.Default
    private Boolean isDeleted = false;
}