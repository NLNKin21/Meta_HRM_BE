package com.metahrms.employee_management.entity.Task;

import com.metahrms.employee_management.entity.BaseEntity;
import com.metahrms.employee_management.entity.Department;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "task_status_transitions", 
    uniqueConstraints = @UniqueConstraint(columnNames = {"from_status_id", "to_status_id"})
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TaskStatusTransition extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "from_status_id", nullable = false)
    private TaskStatus fromStatus;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "to_status_id", nullable = false)
    private TaskStatus toStatus;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "department_id")
    private Department department;

    @Column(name = "allowed_roles", columnDefinition = "TEXT")
    private String allowedRoles; // JSON array: ["HEAD", "DEPUTY_HEAD"]

    @Column(name = "is_active")
    @Builder.Default
    private Boolean isActive = true;
}