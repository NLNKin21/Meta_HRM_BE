package com.metahrms.employee_management.entity.Task;

import jakarta.persistence.*;
import lombok.*;
import java.util.ArrayList;
import java.util.List;

import com.metahrms.employee_management.entity.BaseEntity;
import com.metahrms.employee_management.entity.Department;

@Entity
@Table(name = "task_statuses")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TaskStatus extends BaseEntity {

    @Column(name = "status_name", nullable = false, length = 100)
    private String statusName; // "Chờ xử lý"

    @Column(name = "status_name_en", length = 100)
    private String statusNameEn; // "To Do"

    @Column(name = "order_index", nullable = false)
    @Builder.Default
    private Integer orderIndex = 0; // Thứ tự hiển thị trên Board

    @Column(length = 7)
    @Builder.Default
    private String color = "#1976d2"; // Màu hiển thị

    @Column(length = 50)
    private String icon; // Tên icon MUI: "CheckCircle", "PendingActions"

    @Column(name = "is_completed", nullable = false)
    @Builder.Default
    private Boolean isCompleted = false; // Đánh dấu trạng thái kết thúc

    @Column(name = "is_default", nullable = false)
    @Builder.Default
    private Boolean isDefault = false; // Trạng thái mặc định khi tạo task

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "department_id")
    private Department department; // null = áp dụng chung, có giá trị = riêng phòng ban

    @Column(name = "is_active")
    @Builder.Default
    private Boolean isActive = true;

    @OneToMany(mappedBy = "status", cascade = CascadeType.ALL)
    @Builder.Default
    private List<Task> tasks = new ArrayList<>();

    // Quan hệ với transitions (nếu cần workflow phức tạp)
    @OneToMany(mappedBy = "fromStatus")
    @Builder.Default
    private List<TaskStatusTransition> transitionsFrom = new ArrayList<>();

    @OneToMany(mappedBy = "toStatus")
    @Builder.Default
    private List<TaskStatusTransition> transitionsTo = new ArrayList<>();
}