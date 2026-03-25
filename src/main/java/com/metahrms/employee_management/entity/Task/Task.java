package com.metahrms.employee_management.entity.Task;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.metahrms.employee_management.entity.BaseEntity;
import com.metahrms.employee_management.entity.Department;
import com.metahrms.employee_management.entity.Employee;
import com.metahrms.employee_management.enums.Task.TaskPriority;
import com.metahrms.employee_management.enums.Task.TaskType;

@Entity
@Table(name = "tasks", indexes = {
    @Index(name = "idx_task_code", columnList = "task_code"),
    @Index(name = "idx_task_assignee", columnList = "assignee_id"),
    @Index(name = "idx_task_status", columnList = "status_id"),
    @Index(name = "idx_task_due_date", columnList = "due_date")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Task extends BaseEntity {

    // ========== ĐỊNH DANH ==========
    @Column(name = "task_code", unique = true, nullable = false, length = 50)
    private String taskCode; // TSK-2024-001 (auto generate)

    @Column(nullable = false, length = 255)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    // ========== PHÂN LOẠI ==========
    @Enumerated(EnumType.STRING)
    @Column(name = "task_type", length = 20)
    @Builder.Default
    private TaskType taskType = TaskType.TASK;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    @Builder.Default
    private TaskPriority priority = TaskPriority.MEDIUM;

    // ========== TRẠNG THÁI ==========
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "status_id", nullable = false)
    private TaskStatus status;

    // ========== NGƯỜI LIÊN QUAN ==========
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reporter_id", nullable = false)
    private Employee reporter; // Người tạo (thường là trưởng phòng)

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assignee_id", nullable = false)
    private Employee assignee; // Người thực hiện

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "approver_id")
    private Employee approver; // Người duyệt (nếu cần)

    // ========== PHÂN NHÓM ==========
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "department_id", nullable = false)
    private Department department;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id")
    private Project project; // nullable

    // ========== THỜI GIAN ==========
    @Column(name = "estimated_hours", precision = 8, scale = 2)
    private BigDecimal estimatedHours; // Ước lượng giờ làm

    @Column(name = "actual_hours", precision = 8, scale = 2)
    @Builder.Default
    private BigDecimal actualHours = BigDecimal.ZERO; // Giờ thực tế

    @Column(name = "start_date")
    private LocalDate startDate;

    @Column(name = "due_date")
    private LocalDate dueDate;

    @Column(name = "completed_at")
    private LocalDateTime completedAt; // Thời điểm hoàn thành thực tế

    // ========== TIẾN ĐỘ & KPI ==========
    @Column(name = "completion_rate")
    @Builder.Default
    private Integer completionRate = 0; // % hoàn thành (0-100)

    @Column(name = "is_late")
    @Builder.Default
    private Boolean isLate = false; // Đánh dấu trễ deadline

    @Column(name = "is_urgent")
    @Builder.Default
    private Boolean isUrgent = false; // Đánh dấu gấp

    // ========== QUAN HỆ ==========
    @OneToMany(mappedBy = "task", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<TaskComment> comments = new ArrayList<>();


    @OneToMany(mappedBy = "task", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<TaskHistory> histories = new ArrayList<>();

    @OneToMany(mappedBy = "task", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<TaskReminder> reminders = new ArrayList<>();

    // ========== METADATA ==========
    @Column(name = "is_deleted")
    @Builder.Default
    private Boolean isDeleted = false; // Soft delete

    // ========== METHODS ==========
    @PrePersist
    public void prePersist() {
        if (this.taskCode == null) {
            this.taskCode = generateTaskCode();
        }
    }

    private String generateTaskCode() {
        // Logic sinh mã: TSK-2024-001
        // Implement trong Service
        return "TSK-" + System.currentTimeMillis();
    }
}