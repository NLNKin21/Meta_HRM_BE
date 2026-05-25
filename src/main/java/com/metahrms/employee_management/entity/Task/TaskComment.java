package com.metahrms.employee_management.entity.Task;

import com.metahrms.employee_management.entity.BaseEntity;
import com.metahrms.employee_management.entity.User;

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
    private User user;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;

     @Column(name = "attachment_url", length = 1000)
    private String attachmentUrl;

    @Column(name = "attachment_name", length = 255)
    private String attachmentName;

    @Column(name = "attachment_type", length = 20)
    private String attachmentType; // "image" | "pdf" | "other"

    @Column(name = "is_deleted")
    @Builder.Default
    private Boolean isDeleted = false;
}